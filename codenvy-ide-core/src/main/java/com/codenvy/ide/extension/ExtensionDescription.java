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
package com.codenvy.ide.extension;

import com.codenvy.ide.collections.Array;

/**
 * Provides Extension information:
 * <ul>
 * <li>id - unique String id;</li>
 * <li>version - version of the Extension;</li>
 * <li>title - brief description of the Extension;</li>
 * <li>dependencies - the list of required dependencies</li>
 * </ul>
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ExtensionDescription {
    private final String                       id;
    private final String                       version;
    private final Array<DependencyDescription> dependencies;
    private final String                       title;
    private final String                       description;
    private boolean enabled = false;

    /**
     * Construct {@link ExtensionDescription}
     *
     * @param id
     * @param version
     * @param title
     * @param dependencies
     * @param description
     */
    public ExtensionDescription(String id, String version, String title, String description,
                                Array<DependencyDescription> dependencies) {
        this.id = id;
        this.version = version;
        this.title = title;
        this.dependencies = dependencies;
        this.description = description;
    }

    /**
     * Get Extension description
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get Extension ID
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Get Extension Version
     *
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get Extension title
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the list of {@link DependencyDescription}
     *
     * @return
     */
    public Array<DependencyDescription> getDependencies() {
        return dependencies;
    }

    /** @return the enabled */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     *         the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}