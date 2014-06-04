/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@ExtensionGinModule
public class JavaRuntimeGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(DebuggerClientService.class).to(DebuggerClientServiceImpl.class).in(Singleton.class);
        bind(DebuggerView.class).to(DebuggerViewImpl.class).in(Singleton.class);
        bind(EvaluateExpressionView.class).to(EvaluateExpressionViewImpl.class).in(Singleton.class);
        bind(ChangeValueView.class).to(ChangeValueViewImpl.class).in(Singleton.class);
    }
}