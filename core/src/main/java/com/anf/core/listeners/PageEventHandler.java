package com.anf.core.listeners;

import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

//  ***Begin Code - Candidate Shashi Rao ***
@Component(service = EventHandler.class,
        immediate = true,
        property = {
                EventConstants.EVENT_TOPIC + "=" + PageEvent.EVENT_TOPIC
        })
public class PageEventHandler implements EventHandler {

    private static final Logger logger = LoggerFactory.getLogger(PageEventHandler.class);

    @Reference
    ResourceResolverFactory resourceResolverFactory;


    public void handleEvent(final Event event)  {
        Session session = null;
        final Map<String, Object> authInfo = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, (Object) "AnfCodeChallengeServiceUser");
        try(ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
            Iterator<PageModification> pageInfo = PageEvent.fromEvent(event).getModifications();
            while (pageInfo.hasNext()){
                final PageModification pageModification = pageInfo.next();
                if(pageModification != null && pageModification.getType().toString().equals("PageCreated")) {
                    session = resourceResolver.adaptTo(Session.class);
                    Node pageNode = session.getNode(pageModification.getPath() + "/jcr:content");
                    pageNode.setProperty("pageCreated", "true");
                    session.save();
                }
                logger.info("\n Type :  {},  Page : {}",pageModification.getType(),pageModification.getPath());
            }

        }catch (Exception e){
            logger.info("\n Error while adding property to page while creating page - {} " , e);
        } finally {
            session.logout();
        }
    }
}
// ***END Code*****