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
package com.codenvy.ide.extension.runner.client.inject;

import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.api.runner.gwt.client.RunnerServiceClientImpl;
import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.extension.runner.client.RunOptionsView;
import com.codenvy.ide.extension.runner.client.RunOptionsViewImpl;
import com.codenvy.ide.extension.runner.client.UpdateServiceClient;
import com.codenvy.ide.extension.runner.client.UpdateServiceClientImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author Artem Zatsarynnyy */
@ExtensionGinModule
public class RunnerGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(RunnerServiceClient.class).to(RunnerServiceClientImpl.class).in(Singleton.class);
        bind(UpdateServiceClient.class).to(UpdateServiceClientImpl.class).in(Singleton.class);
        bind(RunOptionsView.class).to(RunOptionsViewImpl.class).in(Singleton.class);
    }
}
