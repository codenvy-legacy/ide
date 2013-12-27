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

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.StringMap;
import com.google.gwt.resources.client.ImageResource;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;


/**
 * Aggregate information about registered PaaS.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class PaaS {
    /** Id of the PaaS. */
    private String                   id;
    /** Title of the PaaS. */
    private String                   title;
    /** PaaS image. */
    private ImageResource            image;
    private StringMap<Array<String>> natures;
    private boolean                  provideTemplate;

    /**
     * Create the PaaS.
     *
     * @param id
     *         PaaS identification
     * @param title
     *         title that will be shown on a new project wizard
     * @param image
     *         image that will be shown on a new project wizard
     * @param natures
     *         nature which the PaaS supports
     * @param provideTemplate
     *         <code>true</code> if the PaaS doesn't need general templates (it has own template), and <code>false</code> otherwise
     */
    public PaaS(@NotNull String id,
                @NotNull String title,
                @Nullable ImageResource image,
                @NotNull StringMap<Array<String>> natures,
                boolean provideTemplate) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.natures = natures;
        this.provideTemplate = provideTemplate;
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

    /** @return <code>true</code> if the PaaS doesn't need general templates (it has own template), and <code>false</code> otherwise */
    public boolean isProvideTemplate() {
        return provideTemplate;
    }

    /**
     * Returns whether the PaaS is available for chosen primary and secondary natures.
     *
     * @param primaryNature
     *         chosen primary nature
     * @param secondaryNature
     *         chosen secondary nature
     * @return <code>true</code> if the PaaS is available, and <code>false</code> otherwise
     */
    public boolean isAvailable(@NotNull String primaryNature, @NotNull Array<String> secondaryNature) {
        Array<String> secondary = natures.get(primaryNature);
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