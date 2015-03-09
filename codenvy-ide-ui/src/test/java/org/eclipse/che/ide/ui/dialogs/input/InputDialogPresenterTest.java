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
package org.eclipse.che.ide.ui.dialogs.input;

import org.eclipse.che.ide.ui.dialogs.BaseTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Testing {@link InputDialogPresenter} functionality.
 *
 * @author Artem Zatsarynnyy
 */
public class InputDialogPresenterTest extends BaseTest {
    @Mock
    private InputDialogView      view;
    private InputDialogPresenter presenter;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        presenter = new InputDialogPresenter(view, TITLE, MESSAGE, inputCallback, cancelCallback);
    }

    @Test
    public void shouldCallCallbackOnCanceled() throws Exception {
        presenter.cancelled();

        verify(view).closeDialog();
        verify(cancelCallback).cancelled();
    }

    @Test
    public void shouldNotCallCallbackOnCanceled() throws Exception {
        presenter = new InputDialogPresenter(view, TITLE, MESSAGE, inputCallback, null);

        presenter.cancelled();

        verify(view).closeDialog();
        verify(cancelCallback, never()).cancelled();
    }

    @Test
    public void shouldCallCallbackOnAccepted() throws Exception {
        presenter.accepted();

        verify(view).closeDialog();
        verify(view).getValue();
        verify(inputCallback).accepted(anyString());
    }

    @Test
    public void shouldNotCallCallbackOnAccepted() throws Exception {
        presenter = new InputDialogPresenter(view, TITLE, MESSAGE, null, cancelCallback);

        presenter.accepted();

        verify(view).closeDialog();
        verify(inputCallback, never()).accepted(anyString());
    }

    @Test
    public void shouldShowView() throws Exception {
        presenter.show();

        verify(view).showDialog();
    }

    @Test
    public void shouldSetValidator() throws Exception {
        final InputValidator validatorMock = mock(InputValidator.class);
        presenter.withValidator(validatorMock);

        verify(view).setValidator(validatorMock);
    }
}
