package com.mysite.core.models;

import com.adobe.cq.wcm.core.components.models.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.inject.Inject;

@Model(
        adaptables = {Resource.class},
        adapters = {Info.class},
        resourceType = {InfoImpl.RESOURCE_TYPE},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class InfoImpl implements Info {
   protected static final String RESOURCE_TYPE = "mysite/component/content/info";

   @Self
    private SlingHttpServletRequest request;

   @Inject
    private Resource resource;

   @ScriptVariable
    private Page currentPage;

   @Inject
    private String firstName;

   @Inject
    private String lastName;

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName.toUpperCase();
    }
}
