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
package com.codenvy.ide.api.paas;

import com.codenvy.ide.collections.Array;
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
    private String        id;
    /** Title of the PaaS. */
    private String        title;
    /** PaaS image. */
    private ImageResource image;
    private Array<String> projectTypeIds;
    private boolean       provideTemplate;

    /**
     * Create the PaaS.
     *
     * @param id
     *         PaaS identification
     * @param title
     *         title that will be shown on a new project wizard
     * @param image
     *         image that will be shown on a new project wizard
     * @param projectTypeIds
     *         project type identifiers which PaaS supports
     * @param provideTemplate
     *         <code>true</code> if the PaaS doesn't need general templates (it has own template), and <code>false</code> otherwise
     */
    public PaaS(@NotNull String id,
                @NotNull String title,
                @Nullable ImageResource image,
                @NotNull Array<String> projectTypeIds,
                boolean provideTemplate) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.projectTypeIds = projectTypeIds;
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
     * Returns whether the PaaS is available for the specified project type.
     *
     * @param projectTypeId
     *         project type id to check
     * @return <code>true</code> if the PaaS supports project type, and <code>false</code> otherwise
     */
    public boolean isAvailable(@NotNull String projectTypeId) {
        return projectTypeIds.contains(projectTypeId);
    }
}