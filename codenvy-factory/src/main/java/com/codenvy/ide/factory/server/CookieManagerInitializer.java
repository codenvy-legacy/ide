/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.factory.server;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * Initializes cookie manager on application startup.
 * Cookie manager is used for cookie saving during  HTTP requests from java code.
 * Must me initialized first, so the 3rd party libraries (like GData) can use it instead of creating own one.
 *
 */

public class CookieManagerInitializer implements Startable {

    private static final Log LOG = ExoLogger.getLogger(CookieManagerInitializer.class);

    @Override
    public void start() {
        CookieHandler handler = CookieHandler.getDefault();
        if (handler == null) {
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        } else {
            if (!(handler instanceof CookieManager)) {
                LOG.error("Wrong instance of cookie handler found: {}", handler.getClass().getName());
            }
        }
    }

    @Override
    public void stop() {

    }
}
