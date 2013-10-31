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
package com.codenvy.ide.wizard.newresource;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.ui.wizard.newresource.CreateResourceHandler;
import com.google.gwt.resources.client.ImageResource;


/**
 * Aggregate information about registered resource type.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class ResourceData {
    private String                id;
    private String                title;
    private ImageResource         icon;
    private CreateResourceHandler handler;

    /**
     * Create wizard's data.
     *
     * @param id
     *         resource identification
     * @param title
     *         title that will be shown on a new resource wizard
     * @param icon
     *         image that will be shown on a new resource wizard
     * @param handler
     *         handler that provides creating a resource
     */
    public ResourceData(@NotNull String id,
                        @NotNull String title,
                        @Nullable ImageResource icon,
                        @NotNull CreateResourceHandler handler) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.handler = handler;
    }

    /** @return {@link String} resource id */
    @NotNull
    public String getId() {
        return id;
    }

    /** @return resource's title */
    @NotNull
    public String getTitle() {
        return title;
    }

    /** @return the resource's icon, or <code>null</code> if it is not */
    @Nullable
    public ImageResource getIcon() {
        return icon;
    }

    /** @return handler that provides creating resource */
    @NotNull
    public CreateResourceHandler getHandler() {
        return handler;
    }
}