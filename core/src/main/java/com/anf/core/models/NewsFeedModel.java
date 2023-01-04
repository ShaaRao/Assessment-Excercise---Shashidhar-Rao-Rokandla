package com.anf.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.osgi.service.component.annotations.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

// ***Begin Code - Candidate Shashi Rao ***

@Component
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewsFeedModel {

    @ValueMapValue
    @Default(values = "/var/commerce/products/anf-code-challenge")
    public String newsFeedPath;

    private List<NewsArticle> newsArticleList = new ArrayList();

    @SlingObject
    private ResourceResolver resourceResolver;

    @PostConstruct
    public void init(){
        Resource newsFeedSrc = resourceResolver.getResource(newsFeedPath+"/newsData");

        if(newsFeedSrc != null){
            Iterator<Resource> newsFeedItems = newsFeedSrc.listChildren();
            while(newsFeedItems.hasNext()){
                final Resource newsFeedItemResource = newsFeedItems.next();
                NewsArticle newsFeed = getNewsArticleAsBean(newsFeedItemResource);
                newsArticleList.add(newsFeed);
            }
        }
    }

    private NewsArticle getNewsArticleAsBean(Resource resource) {

        ValueMap valueMap = resource.getValueMap();
        NewsArticle newsArticleItem = new NewsArticle();

        newsArticleItem.setAuthor(valueMap.get("author", ""));
        newsArticleItem.setContent(valueMap.get("content", ""));
        newsArticleItem.setDescription(valueMap.get("description", ""));
        newsArticleItem.setTitle(valueMap.get("title", ""));
        newsArticleItem.setUrl(valueMap.get("url", ""));
        newsArticleItem.setUrlImage(valueMap.get("urlImage", ""));
        newsArticleItem.setDate(getCurrentDate());

        return newsArticleItem;
    }

    private String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return simpleDateFormat.format(new Date());
    }

    public String getNewsFeedPath() {
        return newsFeedPath;
    }

    public List<NewsArticle> getNewsArticleList() {
        return this.newsArticleList;
    }

}
// ***END Code*****