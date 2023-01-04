package com.anf.core.services.impl;

import com.anf.core.services.QueryService;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.*;

// ***Begin Code - Candidate Shashi Rao ***
@Component(immediate = true, service = QueryService.class)
public class QueryServiceImpl implements QueryService {

    @Reference
    private QueryBuilder queryBuilder;

    private static Logger LOGGER = LoggerFactory.getLogger(QueryServiceImpl.class);

    @Override
    public List<String> getTopTenPageListUsingQueryBuilder(SlingHttpServletRequest request) {
            return this.getPagesUsingQueryBuilder(request, 10);
    }

    @Override
    public List<String> getTopTenPageListUsingSQL2(SlingHttpServletRequest request) {
        return this.getPagesUsingSQL2(request, 10);
    }

    public List<String> getPagesUsingQueryBuilder(SlingHttpServletRequest request, int limit) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("path", "/content/anf-code-challenge/us/en");
        params.put("type", "cq:Page");
        params.put("p.offset", "0");
        params.put("p.limit", "10");
        params.put("1_property", JcrConstants.JCR_CONTENT + "/anfCodeChallenge");
        params.put("1_property.value","true");


        Session session = null;
        try {
            session = request.getResource().getResourceResolver().adaptTo(Session.class);
            Query query = queryBuilder.createQuery(PredicateGroup.create(params), session);
            SearchResult searchResult = query.getResult();
            List<String> pages = new ArrayList<>();

            // iterate through results and consolidate page paths
            if (null != searchResult) {
                final Iterator<Node> nodeItr = searchResult.getNodes();
                while (nodeItr.hasNext()) {
                    Node resultNode = nodeItr.next();
                    pages.add(resultNode.getPath());
                }
            }
            return pages;
        } catch (RepositoryException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (session.isLive() || session != null) {
                session.logout();
            }
        }
        return null;
    }

    public List<String> getPagesUsingSQL2(SlingHttpServletRequest request, int limit) {
        Session session = null;
       try{
           final String sql2Query =
                   "SELECT parent.* FROM [cq:Page] AS parent \n"
                           + "INNER JOIN [nt:base] AS child ON ISCHILDNODE(child,parent) \n"
                           + "WHERE ISDESCENDANTNODE(parent, '/content/anf-code-challenge/us/en') AND child.[anfCodeChallenge] = 'true'";
           session = request.getResource().getResourceResolver().adaptTo(Session.class);
           QueryManager queryManager = session.getWorkspace().getQueryManager();
           javax.jcr.query.Query query = queryManager.createQuery(sql2Query,"JCR-SQL2");
           query.setLimit(limit);
           QueryResult queryResult = query.execute();
           Iterator<Resource> result = queryResult.getNodes();
           List<String> pagePaths = new ArrayList<>();
           result.forEachRemaining(resource -> pagePaths.add(resource.getPath()));

           return pagePaths;
       }  catch (RepositoryException e) {
           LOGGER.error(e.getMessage(), e);
       } finally {
           if (session.isLive() || session != null) {
               session.logout();
           }
       }
        return null;
    }

}