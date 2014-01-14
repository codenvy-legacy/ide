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

import com.codenvy.ide.collections.Array;
import com.google.gwt.resources.client.ImageResource;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Aggregate information about registered Template for creating a project.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class Template {
    private ImageResource icon;
    private String        id;
    private String        title;
    private String        primaryNature;
    private Array<String> secondaryNature;

    /**
     * Create template.
     *
     * @param id
     *         template identification
     * @param title
     *         title that will be shown on a new project wizard
     * @param icon
     *         image that will be shown on a new project wizard
     * @param primaryNature
     *         primary nature that this template supports
     * @param secondaryNature
     *         secondary nature which this template supports
     */
    public Template(@NotNull String id,
                    @NotNull String title,
                    @Nullable ImageResource icon,
                    @NotNull String primaryNature,
                    @NotNull Array<String> secondaryNature) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.primaryNature = primaryNature;
        this.secondaryNature = secondaryNature;
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

    /**
     * Returns whether the template is available for a chosen primary and secondary natures.
     *
     * @param primaryNature
     *         chosen primary nature
     * @param secondaryNature
     *         chosen secondary nature
     * @return <code>true</code> if a template is available, and <code>false</code> otherwise
     */
    public boolean isAvailable(@NotNull String primaryNature, @NotNull Array<String> secondaryNature) {
        if (this.primaryNature.equals(primaryNature)) {
            for (String nature : secondaryNature.asIterable()) {
                if (!this.secondaryNature.contains(nature)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}