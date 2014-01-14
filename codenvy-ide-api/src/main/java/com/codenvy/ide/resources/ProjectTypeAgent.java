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

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.resources.model.Property;
import com.google.gwt.resources.client.ImageResource;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Provides a way to register a new project type.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ProjectTypeAgent {
    /**
     * Registers a new project type.
     *
     * @param typeName
     *         project type name
     * @param title
     *         title that will be shown on a new project wizard page
     * @param icon
     *         icon that will be shown on a new project wizard page
     * @param primaryNature
     *         primary nature which supports the project type
     * @param secondaryNature
     *         secondary natures which supports the project type
     * @param projectProperties
     *         properties of a project
     */
    void register(@NotNull String typeName,
                  @NotNull String title,
                  @Nullable ImageResource icon,
                  @NotNull String primaryNature,
                  @NotNull Array<String> secondaryNature,
                  @NotNull Array<Property> projectProperties);
}