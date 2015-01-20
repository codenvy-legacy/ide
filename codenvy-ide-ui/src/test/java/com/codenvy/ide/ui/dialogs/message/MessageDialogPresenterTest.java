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
package com.codenvy.ide.ui.dialogs.message;

import com.codenvy.ide.ui.dialogs.BaseTest;
import com.codenvy.ide.ui.dialogs.ConfirmCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Testing {@link MessageDialogPresenter} functionality.
 *
 * @author Artem Zatsarynnyy
 */
public class MessageDialogPresenterTest extends BaseTest {
    @Mock
    private MessageDialogView      view;
    @Mock
    private ConfirmCallback        confirmCallback;
    private MessageDialogPresenter presenter;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        presenter = new MessageDialogPresenter(view, TITLE, MESSAGE, confirmCallback);
    }

    @Test
    public void shouldCallCallbackOnAccepted() throws Exception {
        presenter.accepted();

        verify(view).closeDialog();
        verify(confirmCallback).accepted();
    }

    @Test
    public void shouldNotCallCallbackOnAccepted() throws Exception {
        presenter = new MessageDialogPresenter(view, TITLE, MESSAGE, null);

        presenter.accepted();

        verify(view).closeDialog();
        verify(confirmCallback, never()).accepted();
    }

    @Test
    public void shouldShowView() throws Exception {
        presenter.show();

        verify(view).showDialog();
    }
}
