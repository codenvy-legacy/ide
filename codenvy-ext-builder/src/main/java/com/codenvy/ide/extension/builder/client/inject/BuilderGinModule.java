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
package com.codenvy.ide.extension.builder.client.inject;

import com.codenvy.api.builder.gwt.client.BuilderServiceClient;
import com.codenvy.api.builder.gwt.client.BuilderServiceClientImpl;
import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.extension.builder.client.console.BuilderConsoleView;
import com.codenvy.ide.extension.builder.client.console.BuilderConsoleViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author Andrey Plotnikov */
@ExtensionGinModule
public class BuilderGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(BuilderServiceClient.class).to(BuilderServiceClientImpl.class).in(Singleton.class);
        bind(BuilderConsoleView.class).to(BuilderConsoleViewImpl.class).in(Singleton.class);
    }
}