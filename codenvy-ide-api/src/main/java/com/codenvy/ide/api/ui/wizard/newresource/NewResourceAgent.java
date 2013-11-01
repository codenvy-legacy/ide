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
import com.codenvy.ide.api.extension.SDK;


/**
 * Provides register resource for creating new resource wizard.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@SDK(title = "ide.api.ui.wizard.newresource")
public interface NewResourceAgent {
    /**
     * Registers a new resource that will be added to new resource wizard.
     *
     * @param resource
     *         resource that need to be registered
     */
    void register(@NotNull ResourceData resource);
}