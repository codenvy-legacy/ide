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
package com.codenvy.ide.api.ui.wizard.newresource;

import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;


/**
 * Aggregate information about registered resource type. Also provides creating an instance of resource.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public abstract class NewResourceProvider {
    private String        id;
    private String        title;
    private ImageResource icon;
    private String        extension;

    /**
     * Create wizard's data.
     *
     * @param id
     *         resource identification
     * @param title
     *         title that will be shown on a new resource wizard
     * @param icon
     *         image that will be shown on a new resource wizard
     * @param extension
     *         extension of a resource type
     */
    public NewResourceProvider(@NotNull String id,
                               @NotNull String title,
                               @Nullable ImageResource icon,
                               @Nullable String extension) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.extension = extension;
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

    /** @return extension for this kind of resource */
    @Nullable
    public String getExtension() {
        return extension;
    }

    /**
     * Create a resource.
     *
     * @param name
     *         resource name
     * @param parent
     *         folder where a resource needs to be created
     * @param project
     *         project where a resource needs to be created
     * @param callback
     *         callback provides actions after a resource has been created
     */
    public abstract void create(@NotNull String name, @NotNull Folder parent, @NotNull Project project,
                                @NotNull AsyncCallback<Resource> callback);

    /**
     * Returns whether this resource is in context.
     * This option is usable in case a resource is available for current use case.
     *
     * @return <code>true</code> if this resource is in context, and <code>false</code> otherwise
     */
    public boolean inContext() {
        return true;
    }
}