
package com.anf.core.datasource;

import org.apache.sling.api.wrappers.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(
        resourceTypes="datasource/countrylist",
        methods="GET"
)

//  ***Begin Code - Candidate Shashi Rao ***
public class CountryDataSource extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    private static Logger LOGGER = LoggerFactory.getLogger(CountryDataSource.class);
    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) {
        try {
            ResourceResolver resourceResolver = req.getResourceResolver();
            List<Country> countryList = new ArrayList<>();
            Resource currRes = req.getResource();
            String jsonFilePath = currRes.getChild("datasource").getValueMap().get("srcPath", String.class);
            Resource damResource = req.getResourceResolver().getResource(jsonFilePath);
            JSONObject jsonOut = getJsonFromDamFile(damResource);
            jsonOut.keys().forEachRemaining(key -> {
                try {
                    countryList.add(new Country(jsonOut.getString((String) key),(String) key));
                } catch (JSONException e) {
                    LOGGER.error("Exception while parsing json", e);
                }
            });
            LOGGER.debug("countryList ::{}", countryList);
            DataSource ds = getCountryListFromDataSource(resourceResolver, countryList);
            req.setAttribute(DataSource.class.getName(), ds);

        } catch (Exception e) {
            LOGGER.error("Error in Get Drop Down Values", e);
        }
    }

    private static DataSource getCountryListFromDataSource(ResourceResolver resourceResolver, List<Country> countryList) {
        DataSource ds = new SimpleDataSource(new TransformIterator(countryList.iterator(), input -> {
            Country CountryItem = (Country) input;
            ValueMap vmap = new ValueMapDecorator(new HashMap<>());
            vmap.put("value", CountryItem.key);
            vmap.put("text", CountryItem.value);
            return new ValueMapResource(resourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vmap);
        }));
        return ds;
    }

    private JSONObject getJsonFromDamFile(Resource resource) {
        JSONObject jsonObj = new JSONObject();
        try {
            Node _jcr_content = resource.adaptTo(Node.class).getNode("jcr:content");
            InputStream inputStream = _jcr_content.getProperty("jcr:data").getBinary().getStream();
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            jsonObj = new JSONObject(sb.toString());
            LOGGER.debug("getJsonFromDamFile :: {}", jsonObj.toString());
        } catch (RepositoryException | JSONException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return jsonObj;
    }
}
// ***END Code*****
