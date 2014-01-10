/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.ext.java.jdi.client;

import com.codenvy.ide.ext.java.jdi.client.debug.changevalue.ChangeValuePresenter;
import com.codenvy.ide.ext.java.jdi.client.debug.changevalue.ChangeValueView;

import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link ChangeValuePresenter} functionality.
 *
 * @author Artem Zatsarynnyy
 */
public class ChangeVariableValueTest extends BaseTest {
    private static final String VALUE = "value";
    @Mock
    private ChangeValueView      view;
    private ChangeValuePresenter presenter;

    @Override
    public void setUp() {
        super.setUp();
        presenter = new ChangeValuePresenter(view, service, constants, notificationManager, dtoFactory);
    }

    @Test
    public void testOnValueChanged() throws Exception {
        when(view.getValue()).thenReturn(VALUE);

        presenter.onValueChanged();

        verify(view).setEnableChangeButton(eq(!DISABLE_BUTTON));
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }
}
