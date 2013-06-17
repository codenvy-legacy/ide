/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
import com.codenvy.ide.ext.java.jdi.client.run.ApplicationRunnerClientService;
import com.codenvy.ide.ext.java.jdi.client.run.ApplicationRunnerClientServiceImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@ExtensionGinModule
public class JavaRuntimeGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(ApplicationRunnerClientService.class).to(ApplicationRunnerClientServiceImpl.class).in(Singleton.class);
        bind(DebuggerClientService.class).to(DebuggerClientServiceImpl.class).in(Singleton.class);
        bind(DebuggerView.class).to(DebuggerViewImpl.class).in(Singleton.class);
        bind(ReLaunchDebuggerView.class).to(ReLaunchDebuggerViewImpl.class).in(Singleton.class);
        bind(EvaluateExpressionView.class).to(EvaluateExpressionViewImpl.class).in(Singleton.class);
        bind(ChangeValueView.class).to(ChangeValueViewImpl.class).in(Singleton.class);
    }
}