package org.exoplatform.ide.extension.openshift.shared;

import java.util.Map;

public interface OpenShiftEmbeddableCartridge {
    String getName();

    void setName(String name);

    String getUrl();

    void setUrl(String url);

    /** Contains info which should be displayed to the user. It contains important info, e.g. url, username, password for database. */
    String getCreationLog();

    void setCreationLog(String creationLog);

    Map<String, String> getProperties();

    void setProperties(Map<String, String> properties);
}
