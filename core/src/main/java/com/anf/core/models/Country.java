package com.anf.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

//  ***Begin Code - Candidate Shashi Rao ***
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Country {
    @Inject
    private String countries;

    public String getCountries() {
        return countries;
    }
}
// ***END Code*****