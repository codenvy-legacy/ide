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

import com.codenvy.ide.ui.UILocalizationConstant;
import com.codenvy.ide.ui.dialogs.BaseTest;
import com.google.gwt.event.dom.client.ClickEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.codenvy.ide.ui.dialogs.message.MessageDialogView.ActionDelegate;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Testing {@link MessageDialogFooter} functionality.
 *
 * @author Artem Zatsarynnyy
 */
public class MessageDialogFooterTest extends BaseTest {
    @Mock
    private UILocalizationConstant uiLocalizationConstant;
    @Mock
    private ActionDelegate         actionDelegate;
    @InjectMocks
    private MessageDialogFooter    footer;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        footer.setDelegate(actionDelegate);
    }

    @Test
    public void shouldCallAcceptedOnOkClicked() throws Exception {
        footer.handleOkClick(mock(ClickEvent.class));

        verify(actionDelegate).accepted();
    }
}
