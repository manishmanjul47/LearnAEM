package com.mysite.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.result.Hit;
import com.mysite.core.AssetUpdate;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletName;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


import javax.jcr.RepositoryException;
import javax.jcr.Session;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component(service = {Servlet.class}, immediate = true,
         property = {
         "sling.servlet.methods=" + HttpConstants.METHOD_GET})
@SlingServletPaths("/bin/assetUpdate/metadata")
@SlingServletName(servletName = "Update DAM")
public class UpdatedMetaDataPropServlet extends SlingSafeMethodsServlet {

    @Reference
    private transient AssetUpdate assetUpdate;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {

        ResourceResolver resourceResolver = request.getResourceResolver();

        //image path storage
        String imagePath = request.getParameter("assetPath");

        Resource resource = resourceResolver.getResource(imagePath);

        Session session = resourceResolver.adaptTo(Session.class);

        if(Objects.isNull(resource)) {
            response.getWriter().println("Invalid Path");
            return;
        }
        Map<String,String> map= new HashMap<>();
        map.put("type","dam:asset");
        map.put("path","/content/dam/mysite");
        map.put("p.limit","-1");
        map.put("1_property","jcr:content/metadata/dam:ColorSpace");
        map.put("1_property.operation","exists");
        map.put("1_property.value","-1");
        QueryBuilder builder = resourceResolver.adaptTo(QueryBuilder.class);

        SearchResult searchResult = null;
        if(builder!=null) {
            Query query = builder.createQuery(PredicateGroup.create(map),resourceResolver.adaptTo(Session.class));
            searchResult = query.getResult();
        }
        List<Hit> result = searchResult.getHits();
        for(Hit hit: result) {
            try {
                String path = hit.getPath();
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }

        if(Objects.nonNull(resource))
        {
            String[] data = assetUpdate.getAssetData();
            String propertyName = data[0];
            String propertyValue = data[1];

            ModifiableValueMap modifiableValueMap = resource.getChild("jcr:content/metadata").adaptTo(ModifiableValueMap.class);
            modifiableValueMap.put(propertyName,propertyValue);
            response.getWriter().println("DATA UPDATED");
            try {
                session.save();
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }

        }


    }

}
