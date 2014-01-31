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
package com.codenvy.ide.api.template;

import com.google.gwt.resources.client.ImageResource;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Aggregate information about registered template for creating a project.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class Template {
    private ImageResource icon;
    private String        id;
    private String        title;
    private String        description;
    private String        projectTypeId;

    /**
     * Create template.
     *
     * @param id
     *         template identification
     * @param title
     *         title that will be shown on a new project wizard
     * @param description
     *         description that will be shown on a new project wizard
     * @param icon
     *         image that will be shown on a new project wizard
     * @param projectTypeId
     *         project type that this template supports
     */
    public Template(@NotNull String id,
                    @NotNull String title,
                    @NotNull String description,
                    @Nullable ImageResource icon,
                    @NotNull String projectTypeId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.projectTypeId = projectTypeId;
    }

    /** @return {@link String} template id */
    @NotNull
    public String getId() {
        return id;
    }

    /** @return template's icon */
    @Nullable
    public ImageResource getIcon() {
        return icon;
    }

    /** @return template's title */
    @NotNull
    public String getTitle() {
        return title;
    }

    /** @return template's description */
    @NotNull
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the template supports the specified project type.
     *
     * @param projectTypeId
     *         project type to check
     * @return <code>true</code> if a template supports project type, and <code>false</code> otherwise
     */
    public boolean isAvailable(@NotNull String projectTypeId) {
        return this.projectTypeId.equals(projectTypeId);
    }
}