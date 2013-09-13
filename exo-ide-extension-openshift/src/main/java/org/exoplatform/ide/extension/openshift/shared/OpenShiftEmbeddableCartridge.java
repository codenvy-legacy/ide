package org.exoplatform.ide.extension.openshift.shared;

import java.util.Map;

public interface OpenShiftEmbeddableCartridge {
    String getName();

    void setName(String name);

    String getUrl();

    void setUrl(String url);

    Map<String, String> getProperties();

    void setProperties(Map<String, String> properties);
}
