package com.mysite.core.models;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.mysite.core.bean.ArticleListDataBean;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

/**
 * Creates an Article List Component
 */
@Getter
@Setter
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class ArticleList {

    private static final Logger logger = LoggerFactory.getLogger(ArticleList.class);

    List<ArticleListDataBean> articleListDataBeansArray = null;

    @SlingObject
    Resource resource;

    @SlingObject
    Page page;

    @SlingObject
    ResourceResolver resourceResolver;


    /**
     * Gets/sets articleListRootPath Property
     */
    @ValueMapValue
    private String articleListRootPath;


    @PostConstruct
    protected void init() {

        resourceResolver = resource.getResourceResolver();

        Session session = resourceResolver.adaptTo(Session.class);

        QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);

        articleListDataBeansArray = new ArrayList<>();

        Query query = null;

        Map<String,String> map = new HashMap<>();
        map.put("type","cq:Page");
        map.put("path","/content/mysite/us/en/article");

        if (queryBuilder != null) {
            query = queryBuilder.createQuery(PredicateGroup.create(map),session);

        }


        SearchResult searchResult = null;
        if (query != null) {
            searchResult = query.getResult();
        }

        List<Hit> result = null;
        if (searchResult != null) {
            result = searchResult.getHits();
        }

        if (result != null) {
            for (Hit hit : result) {
                try {

                    ArticleListDataBean articleListDataBean = new ArticleListDataBean();
                    String path = hit.getPath();
                    Resource articleResource = resourceResolver.getResource(path);
                    Page articlepage = null;
                    if (articleResource != null) {
                        articlepage = articleResource.adaptTo(Page.class);
                    }
                    String description = null;
                    if (articlepage != null) {
                        description = articlepage.getDescription();
                    }
                    String title = null;
                    if (articlepage != null) {
                        title = articlepage.getTitle();
                    }
                    String imagePath = articlepage != null ? articlepage.getContentResource("cq:featuredimage").getValueMap().get("fileReference", String.class) : null;

                    articleListDataBean.setPath(path);
                    articleListDataBean.setTitle(title);
                    articleListDataBean.setDescription(description);
                    articleListDataBean.setImagePath(imagePath);

                    logger.debug("Title:{}, Description:{},Image:{}",title,description,imagePath);

                    articleListDataBeansArray.add(articleListDataBean);

                } catch (RepositoryException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

    public List<ArticleListDataBean> getArticleListDataBeansArray() {
        return articleListDataBeansArray;
    }
}
