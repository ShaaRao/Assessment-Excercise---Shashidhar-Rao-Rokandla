package com.anf.core.services.impl;

import com.anf.core.services.ContentService;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// ***Begin Code - Candidate Shashi Rao ***
@Component(immediate = true, service = ContentService.class)
public class ContentServiceImpl implements ContentService {

    private static Logger LOGGER = LoggerFactory.getLogger(ContentServiceImpl.class);
    @Reference
    ResourceResolverFactory resourceResolverFactory;
    private ResourceResolver resourceResolver;
    @Override
    public boolean commitUserDetails(String firstName, String lastName, String age) {
        try {
            resourceResolver = getServiceResourceResolver();
            Resource source = ResourceUtil.getOrCreateResource(resourceResolver, "/var/anf-code-challenge",
                    Collections.singletonMap("jcr:primaryType", (Object) "sling:OrderedFolder"),
                    null, false);
            Map<String, Object> userInputValues = getUserInputValuesMap(firstName, lastName, age);
            resourceResolver.create(source, "data_"+new Date().getTime(), userInputValues);
            resourceResolver.commit();
            return true;
        }
        catch (PersistenceException | LoginException err){
            LOGGER.error(err.getMessage(), err);
        }
        finally {
            if(resourceResolver != null)
                resourceResolver.close();
        }
        return false;
    }

    private ResourceResolver getServiceResourceResolver() throws LoginException {
        return resourceResolverFactory.getServiceResourceResolver(Collections.singletonMap(
                ResourceResolverFactory.SUBSERVICE,
                (Object) "subServiceCluster"));
    }

    private static Map<String, Object> getUserInputValuesMap(String firstName, String lastName, String age) {
        Map<String, Object> userInputValues = new HashMap<>();
        userInputValues.put("jcr:primaryType", (Object) "nt:unstructured");
        userInputValues.put("firstName", firstName);
        userInputValues.put("lastName", lastName);
        userInputValues.put("age", age);
        return userInputValues;
    }
}