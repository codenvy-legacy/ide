/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.github.client.load;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ProjectData.java Nov 18, 2011 3:27:38 PM vereshchaka $
 */
public class ProjectData {
    private String        name;
    private String        description;
    private String        type;
    /** Url to clone from GitHub. */
    private String        repositoryUrl;
    /** Url to clone from GitHub (readOnly). */
    private String        readOnlyUrl;
    private Array<String> targets;

    public ProjectData(String name, String description, String type, Array<String> targets, String repositoryUrl, String readOnlyUrl) {
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
    public Array<String> getTargets() {
        if (targets == null) {
            targets = Collections.createArray();
        }
        return targets;
    }

    /**
     * @param targets
     *         the targets to set
     */
    public void setTargets(Array<String> targets) {
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