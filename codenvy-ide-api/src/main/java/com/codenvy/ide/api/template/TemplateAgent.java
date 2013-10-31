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

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Provider;

/**
 * Provides a way to register a new template for creating a project.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface TemplateAgent {
    /**
     * Registers a new template for creating a project.
     *
     * @param id
     *         id for template identification
     * @param title
     *         title that will be shown on a new project wizard page
     * @param icon
     *         icon that will be shown on a new project wizard page
     * @param primaryNature
     *         primary nature which supports the template
     * @param secondaryNatures
     *         secondary natures which supports the template
     * @param wizardPages
     *         pages which need to be added to a new project wizard
     */
    void register(@NotNull String id,
                  @NotNull String title,
                  @Nullable ImageResource icon,
                  @NotNull String primaryNature,
                  @NotNull JsonArray<String> secondaryNatures,
                  @NotNull JsonArray<Provider<? extends AbstractTemplatePage>> wizardPages);
}