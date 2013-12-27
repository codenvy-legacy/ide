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
package com.codenvy.ide.wizard;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.ui.wizard.DefaultWizard;
import com.codenvy.ide.api.ui.wizard.Wizard;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.google.inject.Provider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link com.codenvy.ide.api.ui.wizard.DefaultWizard} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class DefaultWizardTest extends BaseWizardTest {
    public static final String TITLE = "title";
    @Mock
    private WizardPage           page1;
    @Mock
    private Provider<WizardPage> provider1;
    @Mock
    private WizardPage           page2;
    @Mock
    private Provider<WizardPage> provider2;
    @Mock
    private WizardPage           page3;
    @Mock
    private Provider<WizardPage> provider3;
    @Mock
    private WizardPage           page4;
    @Mock
    private Provider<WizardPage> provider4;
    private DefaultWizard        wizard;

    @Before
    public void setUp() {
        wizard = new DefaultWizard(notificationManager, TITLE);
        wizard.setUpdateDelegate(mock(Wizard.UpdateDelegate.class));

        when(provider1.get()).thenReturn(page1);
        when(provider2.get()).thenReturn(page2);
        when(provider3.get()).thenReturn(page3);
        when(provider4.get()).thenReturn(page4);
    }

    @Test
    public void testAddPage() throws Exception {
        when(page1.inContext()).thenReturn(IN_CONTEXT);
        when(page2.inContext()).thenReturn(IN_CONTEXT);
        when(page3.inContext()).thenReturn(IN_CONTEXT);

        wizard.addPage(provider1);
        wizard.addPage(provider2);
        wizard.addPage(provider3);

        assertEquals(wizard.flipToFirst(), page1);
        assertEquals(wizard.flipToNext(), page2);
        assertEquals(wizard.flipToNext(), page3);
        assertNull(wizard.flipToNext());
    }

    @Test
    public void testAddPageByIndex() throws Exception {
        when(page1.inContext()).thenReturn(IN_CONTEXT);
        when(page2.inContext()).thenReturn(IN_CONTEXT);
        when(page3.inContext()).thenReturn(IN_CONTEXT);

        wizard.addPage(provider1);
        wizard.addPage(provider3);
        wizard.addPage(provider2, 1, false);

        assertEquals(wizard.flipToFirst(), page1);
        assertEquals(wizard.flipToNext(), page2);
        assertEquals(wizard.flipToNext(), page3);
        assertNull(wizard.flipToNext());
    }

    @Test
    public void testAddPageWithReplace() throws Exception {
        when(page1.inContext()).thenReturn(IN_CONTEXT);
        when(page2.inContext()).thenReturn(IN_CONTEXT);

        wizard.addPage(provider1);
        wizard.addPage(provider3);
        wizard.addPage(provider2, 1, true);

        assertEquals(wizard.flipToFirst(), page1);
        assertEquals(wizard.flipToNext(), page2);
        assertNull(wizard.flipToNext());
    }

    @Test
    public void testGetTitle() throws Exception {
        assertEquals(wizard.getTitle(), TITLE);
    }

    @Test
    public void testFlipToFirstWhenNeedToSkipFirstPages() throws Exception {
        when(page1.inContext()).thenReturn(IN_CONTEXT);
        when(page1.canSkip()).thenReturn(IN_CONTEXT);
        when(page2.inContext()).thenReturn(NOT_CONTEXT);
        when(page3.inContext()).thenReturn(IN_CONTEXT);

        wizard.addPage(provider1);
        wizard.addPage(provider2);
        wizard.addPage(provider3);

        assertEquals(wizard.flipToFirst(), page3);
    }

    @Test
    public void testFlipToFirst() throws Exception {
        when(page1.inContext()).thenReturn(IN_CONTEXT);
        wizard.addPage(provider1);

        assertEquals(wizard.flipToFirst(), page1);
    }

    @Test
    public void testFlipToNextUseCase1() throws Exception {
        prepareTestCase1();

        assertEquals(wizard.flipToFirst(), page1);
        assertEquals(wizard.flipToNext(), page2);
        assertEquals(wizard.flipToNext(), page4);
        assertNull(wizard.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase1() throws Exception {
        prepareTestCase1();

        wizard.flipToFirst();
        flipPages(wizard, 2);

        assertEquals(wizard.flipToPrevious(), page2);
        assertEquals(wizard.flipToPrevious(), page1);
    }

    @Test
    public void testHasNextUseCase1() throws Exception {
        prepareTestCase1();

        wizard.flipToFirst();
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase1() throws Exception {
        prepareTestCase1();

        wizard.flipToFirst();
        assertEquals(wizard.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase1() throws Exception {
        prepareTestCase1();

        wizard.flipToFirst();
        Assert.assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinish() throws Exception {
        prepareTestCase1();

        prepareSuccessfulCommitCallback(page1);
        prepareSuccessfulCommitCallback(page2);
        prepareSuccessfulCommitCallback(page4);

        wizard.flipToFirst();
        flipPages(wizard, 2);

        wizard.onFinish();

        verify(page1).commit((WizardPage.CommitCallback)anyObject());
        verify(page2).commit((WizardPage.CommitCallback)anyObject());
        verify(page3, never()).commit((WizardPage.CommitCallback)anyObject());
        verify(page4).commit((WizardPage.CommitCallback)anyObject());
    }

    /** In case the wizard has got 3 enabled pages and 1 skipped page. */
    private void prepareTestCase1() {
        when(page1.inContext()).thenReturn(IN_CONTEXT);
        when(page1.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(page1.isCompleted()).thenReturn(COMPLETED);

        when(page2.inContext()).thenReturn(IN_CONTEXT);
        when(page2.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(page2.isCompleted()).thenReturn(COMPLETED);

        when(page3.inContext()).thenReturn(NOT_CONTEXT);
        when(page3.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(page3.isCompleted()).thenReturn(COMPLETED);

        when(page4.inContext()).thenReturn(IN_CONTEXT);
        when(page4.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(page4.isCompleted()).thenReturn(COMPLETED);

        wizard.addPage(provider1);
        wizard.addPage(provider2);
        wizard.addPage(provider3);
        wizard.addPage(provider4);
    }

    @Test
    public void testFlipToNextUseCase2() throws Exception {
        prepareTestCase2();

        assertEquals(wizard.flipToFirst(), page1);
        assertEquals(wizard.flipToNext(), page2);
        assertNull(wizard.flipToNext());
    }

    @Test
    public void testFlipToPreviousUseCase2() throws Exception {
        prepareTestCase2();

        wizard.flipToFirst();
        flipPages(wizard, 1);

        assertEquals(wizard.flipToPrevious(), page1);
    }

    @Test
    public void testHasNextUseCase2() throws Exception {
        prepareTestCase2();

        wizard.flipToFirst();
        assertEquals(wizard.hasNext(), HAS_NEXT);

        flipPages(wizard, 1);
        assertEquals(wizard.hasNext(), HAS_NOT_NEXT);
    }

    @Test
    public void testHasPreviousUseCase2() throws Exception {
        prepareTestCase2();

        wizard.flipToFirst();
        assertEquals(wizard.hasPrevious(), HAS_NOT_PREVIOUS);

        flipPages(wizard, 1);
        assertEquals(wizard.hasPrevious(), HAS_PREVIOUS);
    }

    @Test
    public void testCanFinishUseCase2() throws Exception {
        prepareTestCase2();

        wizard.flipToFirst();
        assertEquals(wizard.canFinish(), CAN_NOT_FINISH);

        flipPages(wizard, 1);
        assertEquals(wizard.canFinish(), CAN_FINISH);
    }

    @Test
    public void testOnFinishUseCase2() throws Exception {
        prepareTestCase2();

        prepareSuccessfulCommitCallback(page1);
        prepareSuccessfulCommitCallback(page2);
        prepareSuccessfulCommitCallback(page4);

        wizard.flipToFirst();
        flipPages(wizard, 1);

        wizard.onFinish();

        verify(page1).commit((WizardPage.CommitCallback)anyObject());
        verify(page2).commit((WizardPage.CommitCallback)anyObject());
        verify(page3, never()).commit((WizardPage.CommitCallback)anyObject());
        verify(page4).commit((WizardPage.CommitCallback)anyObject());
    }

    @Test
    public void testOnFinishWhenFailure() throws Exception {
        prepareTestCase2();

        prepareSuccessfulCommitCallback(page1);
        prepareFailureCommitCallback(page2);

        wizard.flipToFirst();
        flipPages(wizard, 1);

        wizard.onFinish();

        verify(page1).commit((WizardPage.CommitCallback)anyObject());
        verify(page2).commit((WizardPage.CommitCallback)anyObject());
        verify(page3, never()).commit((WizardPage.CommitCallback)anyObject());
        verify(page4, never()).commit((WizardPage.CommitCallback)anyObject());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    /** In case the wizard has got 2 enabled pages, 1 skipped page and 1 disable page. */
    private void prepareTestCase2() {
        when(page1.inContext()).thenReturn(IN_CONTEXT);
        when(page1.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(page1.isCompleted()).thenReturn(COMPLETED);

        when(page2.inContext()).thenReturn(IN_CONTEXT);
        when(page2.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(page2.isCompleted()).thenReturn(COMPLETED);

        when(page3.inContext()).thenReturn(NOT_CONTEXT);
        when(page3.canSkip()).thenReturn(CAN_NOT_SKIP);
        when(page3.isCompleted()).thenReturn(COMPLETED);

        when(page4.inContext()).thenReturn(IN_CONTEXT);
        when(page4.canSkip()).thenReturn(CAN_SKIP);
        when(page4.isCompleted()).thenReturn(COMPLETED);

        wizard.addPage(provider1);
        wizard.addPage(provider2);
        wizard.addPage(provider3);
        wizard.addPage(provider4);
    }
}