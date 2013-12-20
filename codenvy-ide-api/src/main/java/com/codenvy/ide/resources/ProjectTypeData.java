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
package com.codenvy.ide.resources;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.collections.Array;
import com.google.gwt.resources.client.ImageResource;

/**
 * Aggregate information about registered project type.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class ProjectTypeData {
    private String        typeName;
    private String        title;
    private ImageResource icon;
    private String        primaryNature;
    private Array<String> secondaryNature;

    /**
     * Create a project type.
     *
     * @param typeName
     *         project type name
     * @param title
     *         title that will be shown on a new project wizard
     * @param icon
     *         image that will be shown on a new project wizard
     * @param primaryNature
     *         primary nature that this project type supports
     * @param secondaryNature
     *         secondary nature which this project type supports
     */
    public ProjectTypeData(@NotNull String typeName,
                           @NotNull String title,
                           @Nullable ImageResource icon,
                           @NotNull String primaryNature,
                           @NotNull Array<String> secondaryNature) {
        this.typeName = typeName;
        this.title = title;
        this.icon = icon;
        this.primaryNature = primaryNature;
        this.secondaryNature = secondaryNature;
    }

    /** @return the project type's name */
    @NotNull
    public String getTypeName() {
        return typeName;
    }

    /** @return the title */
    @NotNull
    public String getTitle() {
        return title;
    }

    /** @return the icon */
    @NotNull
    public ImageResource getIcon() {
        return icon;
    }

    /** @return primary nature */
    @NotNull
    public String getPrimaryNature() {
        return primaryNature;
    }

    /** @return secondary nature */
    @NotNull
    public Array<String> getSecondaryNature() {
        return secondaryNature;
    }
}