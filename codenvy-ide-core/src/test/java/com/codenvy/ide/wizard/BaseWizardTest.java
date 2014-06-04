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
package com.codenvy.ide.wizard;

import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.ui.wizard.Wizard;
import com.codenvy.ide.api.ui.wizard.WizardPage;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Base test for testing wizard.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseWizardTest {
    public static final boolean CAN_SKIP         = true;
    public static final boolean CAN_NOT_SKIP     = false;
    public static final boolean HAS_NEXT         = true;
    public static final boolean HAS_NOT_NEXT     = false;
    public static final boolean HAS_PREVIOUS     = true;
    public static final boolean HAS_NOT_PREVIOUS = false;
    public static final boolean CAN_FINISH       = true;
    public static final boolean CAN_NOT_FINISH   = false;
    public static final boolean COMPLETED        = true;
    public static final boolean IN_CONTEXT       = true;
    public static final boolean NOT_CONTEXT      = false;
    @Mock
    protected NotificationManager notificationManager;

    /**
     * Flip pages into wizard.
     *
     * @param count
     *         count of pages how many need to flip
     */
    protected void flipPages(Wizard wizard, int count) {
        for (int i = 0; i < count; i++) {
            wizard.flipToNext();
        }
    }

    /**
     * Prepare commit callback on mock wizard page. Commit callback is usually successful.
     *
     * @param page
     *         page that need to be prepared
     */
    protected void prepareSuccessfulCommitCallback(WizardPage page) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                WizardPage.CommitCallback callback = (WizardPage.CommitCallback)arguments[0];
                callback.onSuccess();
                return null;
            }
        }).when(page).commit((WizardPage.CommitCallback)anyObject());
    }

    /**
     * Prepare commit callback on mock wizard page. Commit callback is usually failed.
     *
     * @param page
     *         page that need to be prepared
     */
    protected void prepareFailureCommitCallback(WizardPage page) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                WizardPage.CommitCallback callback = (WizardPage.CommitCallback)arguments[0];
                callback.onFailure(mock(Throwable.class));
                return null;
            }
        }).when(page).commit((WizardPage.CommitCallback)anyObject());
    }
}