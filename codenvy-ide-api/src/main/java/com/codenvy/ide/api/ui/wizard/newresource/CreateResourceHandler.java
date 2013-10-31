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

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The handler for creating a resource. It needs to make it possible to create a new kind of resource.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface CreateResourceHandler {
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
    void create(@NotNull String name, @NotNull Folder parent, @NotNull Project project, @NotNull AsyncCallback<Resource> callback);
}