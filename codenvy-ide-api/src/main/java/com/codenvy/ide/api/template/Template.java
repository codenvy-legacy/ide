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
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.resources.client.ImageResource;

/**
 * Aggregate information about registered Template for creating project.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class Template {
    private ImageResource     icon;
    private String            title;
    private String            primaryNature;
    private JsonArray<String> secondaryNature;

    /**
     * Create template.
     *
     * @param icon
     * @param title
     */
    // TODO javadoc
    public Template(@NotNull String title, @Nullable ImageResource icon, @NotNull String primaryNature,
                    @NotNull JsonArray<String> secondaryNature) {
        this.icon = icon;
        this.title = title;
        this.primaryNature = primaryNature;
        this.secondaryNature = secondaryNature;
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

    public boolean isAvailable(@NotNull String primaryNature, @NotNull JsonArray<String> secondaryNature) {
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