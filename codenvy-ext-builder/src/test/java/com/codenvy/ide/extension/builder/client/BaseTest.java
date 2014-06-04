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
package com.codenvy.ide.extension.builder.client;

import com.codenvy.ide.extension.builder.client.console.BuilderConsolePresenter;
import com.codenvy.ide.websocket.MessageBus;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;

import org.junit.Before;
import org.mockito.Mock;

/**
 * Base test for builder extension.
 *
 * @author Artem Zatsarynnyy
 */
@GwtModule("com.codenvy.ide.extension.builder.Builder")
public abstract class BaseTest extends GwtTestWithMockito {
    @Mock
    protected MessageBus              messageBus;
    @Mock
    protected BuilderConsolePresenter builderConsolePresenter;

    @Before
    public void setUp() {
    }
}
