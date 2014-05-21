/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.factory.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.factory.client.accept.AcceptFactoryHandler;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Vladyslav Zhukovskii
 */
@Singleton
@Extension(title = "Factory", version = "3.0.0")
public class FactoryExtension {

    @Inject
    public FactoryExtension(AcceptFactoryHandler acceptFactoryHandler) {
        acceptFactoryHandler.process();
    }
}
