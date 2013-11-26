/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.github.client.load;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ProjectData.java Nov 18, 2011 3:27:38 PM vereshchaka $
 */
public class ProjectData {
    private String            name;
    private String            description;
    private String            type;
    /** Url to clone from GitHub. */
    private String            repositoryUrl;
    /** Url to clone from GitHub (readOnly). */
    private String            readOnlyUrl;
    private JsonArray<String> targets;

    public ProjectData(String name, String description, String type, JsonArray<String> targets, String repositoryUrl, String readOnlyUrl) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.repositoryUrl = repositoryUrl;
        this.targets = targets;
        this.readOnlyUrl = readOnlyUrl;
    }

    /**
     * Get the url to clone from GitHub.
     *
     * @return the repositoryUrl
     */
    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    /**
     * @param repositoryUrl
     *         the repositoryUrl to set
     */
    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /** @return the description */
    public String getDescription() {
        return description;
    }

    /** @return the type */
    public String getType() {
        return type;
    }

    /**
     * @param name
     *         the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    /** @return the targets */
    public JsonArray<String> getTargets() {
        if (targets == null) {
            targets = JsonCollections.createArray();
        }
        return targets;
    }

    /**
     * @param targets
     *         the targets to set
     */
    public void setTargets(JsonArray<String> targets) {
        this.targets = targets;
    }

    /**
     * @param description
     *         the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getReadOnlyUrl() {
        return readOnlyUrl;
    }

    public void setReadOnlyUrl(String readOnlyUrl) {
        this.readOnlyUrl = readOnlyUrl;
    }
}