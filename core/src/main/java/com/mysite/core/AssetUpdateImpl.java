package com.mysite.core;


import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.security.auth.login.Configuration;

@Component(service = AssetUpdate.class,configurationPolicy = ConfigurationPolicy.REQUIRE,configurationPid = "com.mysite.core.AssetUpdate",immediate = true)
@Designate(ocd = AssetUpdateImpl.Configuration.class)
public class AssetUpdateImpl implements AssetUpdate{

    private Configuration configuration;

    @Activate
    @Modified
    protected void activate(AssetUpdateImpl.Configuration configuration1) {this.configuration = configuration1;}

    @Override
    public String[] getAssetData() {
        return this.configuration.assetData();
    }

    @ObjectClassDefinition(name = "Asset Update")
    @interface Configuration {
        @AttributeDefinition(name = "field 1")
        String[] assetData() default {"hello"};
    }


}
