package org.exoplatform.ide.extension.openshift.server;

import org.exoplatform.ide.extension.openshift.shared.OpenShiftEmbeddableCartridge;

import java.util.LinkedHashMap;
import java.util.Map;

public class OpenShiftEmbeddableCartridgeImpl implements OpenShiftEmbeddableCartridge {
    private String              name;
    private String              url;
    private Map<String, String> properties;

    public OpenShiftEmbeddableCartridgeImpl(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public OpenShiftEmbeddableCartridgeImpl() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Map<String, String> getProperties() {
        if (properties == null) {
            properties = new LinkedHashMap<String, String>(4);
        }
        return properties;
    }

    @Override
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        return "OpenShiftEmbeddableCartridgeImpl{" +
               "name='" + name + '\'' +
               ", url='" + url + '\'' +
               ", properties=" + properties +
               '}';
    }
}
