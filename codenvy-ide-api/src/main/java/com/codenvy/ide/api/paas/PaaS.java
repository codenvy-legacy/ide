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
package com.codenvy.ide.api.paas;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonStringMap;
import com.google.gwt.resources.client.ImageResource;


/**
 * Aggregate information about registered PaaS.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class PaaS {
    /** Id of the PaaS. */
    private String                           id;
    /** Title of the PaaS. */
    private String                           title;
    /** PaaS image. */
    private ImageResource                    image;
    private JsonStringMap<JsonArray<String>> natures;

    /**
     * Create PaaS.
     *
     * @param id
     * @param title
     * @param image
     */
    // TODO javadoc
    public PaaS(@NotNull String id, @NotNull String title, @Nullable ImageResource image,
                @NotNull JsonStringMap<JsonArray<String>> natures) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.natures = natures;
    }

    /** @return {@link String} PaaS id */
    public String getId() {
        return id;
    }

    /** @return the title */
    public String getTitle() {
        return title;
    }

    /** @return the image */
    public ImageResource getImage() {
        return image;
    }

    public boolean isAvailable(@NotNull String primaryNature, @NotNull JsonArray<String> secondaryNature) {
        JsonArray<String> secondary = natures.get(primaryNature);
        if (secondary != null) {
            for (String nature : secondaryNature.asIterable()) {
                if (!secondary.contains(nature)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}