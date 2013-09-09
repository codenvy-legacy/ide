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
package com.codenvy.ide.extension.android.client;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public abstract class AndroidExtensionService {
    private static AndroidExtensionService instance;

    public static AndroidExtensionService getInstance() {
        return instance;
    }

    protected AndroidExtensionService() {
        instance = this;
    }

    /**
     * Start Android project.
     *
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void start(String apkUrl, String oauthToken, ProjectModel project, AsyncRequestCallback<StringBuilder> callback) throws RequestException;
}
