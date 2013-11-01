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
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.extension.SDK;
import com.google.gwt.resources.client.ImageResource;


/**
 * Provides register resource for creating new resource wizard.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@SDK(title = "ide.api.ui.wizard.newresource")
public interface NewResourceAgent {
    /**
     * Registers new resource for creating new resource wizard.
     *
     * @param id
     *         id for resource identification
     * @param title
     *         the text what will be showed on wizard page
     * @param icon
     *         the icon what will be showed on wizard page
     * @param extension
     *         extension of resource type
     * @param handler
     *         handler that provides creating a resource
     */
    void register(@NotNull String id,
                  @NotNull String title,
                  @Nullable ImageResource icon,
                  @Nullable String extension,
                  @NotNull CreateResourceHandler handler);
}