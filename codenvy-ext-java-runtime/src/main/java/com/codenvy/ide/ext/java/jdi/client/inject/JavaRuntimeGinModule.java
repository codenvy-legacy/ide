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
package com.codenvy.ide.ext.java.jdi.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerClientService;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerClientServiceImpl;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerView;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerViewImpl;
import com.codenvy.ide.ext.java.jdi.client.debug.changevalue.ChangeValueView;
import com.codenvy.ide.ext.java.jdi.client.debug.changevalue.ChangeValueViewImpl;
import com.codenvy.ide.ext.java.jdi.client.debug.expression.EvaluateExpressionView;
import com.codenvy.ide.ext.java.jdi.client.debug.expression.EvaluateExpressionViewImpl;
import com.codenvy.ide.ext.java.jdi.client.debug.relaunch.ReLaunchDebuggerView;
import com.codenvy.ide.ext.java.jdi.client.debug.relaunch.ReLaunchDebuggerViewImpl;
//import com.codenvy.ide.ext.java.jdi.client.run.ApplicationRunnerClientService;
//import com.codenvy.ide.ext.java.jdi.client.run.ApplicationRunnerClientServiceImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@ExtensionGinModule
public class JavaRuntimeGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
//        bind(ApplicationRunnerClientService.class).to(ApplicationRunnerClientServiceImpl.class).in(Singleton.class);
        bind(DebuggerClientService.class).to(DebuggerClientServiceImpl.class).in(Singleton.class);
        bind(DebuggerView.class).to(DebuggerViewImpl.class).in(Singleton.class);
        bind(ReLaunchDebuggerView.class).to(ReLaunchDebuggerViewImpl.class).in(Singleton.class);
        bind(EvaluateExpressionView.class).to(EvaluateExpressionViewImpl.class).in(Singleton.class);
        bind(ChangeValueView.class).to(ChangeValueViewImpl.class).in(Singleton.class);
    }
}