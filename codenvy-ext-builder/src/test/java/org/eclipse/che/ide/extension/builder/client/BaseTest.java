/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension.builder.client;

import org.eclipse.che.ide.extension.builder.client.console.BuilderConsolePresenter;
import org.eclipse.che.ide.websocket.MessageBus;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;

/**
 * Base test for builder extension.
 *
 * @author Artem Zatsarynnyy
 */
@RunWith(GwtMockitoTestRunner.class)
public abstract class BaseTest {
    @Mock
    protected MessageBus              messageBus;
    @Mock
    protected BuilderConsolePresenter builderConsolePresenter;

    @Before
    public void setUp() {
    }
}
