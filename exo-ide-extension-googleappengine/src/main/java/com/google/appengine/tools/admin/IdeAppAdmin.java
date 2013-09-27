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
package com.google.appengine.tools.admin;

import java.io.PrintWriter;

import static com.google.appengine.tools.admin.AppAdminFactory.ApplicationProcessingOptions;
import static com.google.appengine.tools.admin.AppAdminFactory.ConnectOptions;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class IdeAppAdmin extends AppAdminImpl {
    private final GenericApplication app;

    public IdeAppAdmin(ConnectOptions options,
                       GenericApplication app,
                       PrintWriter errorWriter,
                       ApplicationProcessingOptions appOptions,
                       Class<? extends AppVersionUpload> appVersionUploadClass) {
        super(options, app, errorWriter, appOptions, appVersionUploadClass);
        this.app = app;
    }

    @Override
    protected ServerConnection getServerConnection(ConnectOptions options) {
        return new OAuth2ServerConnection(options);
    }

    public GenericApplication getApplication() {
        return app;
    }
}
