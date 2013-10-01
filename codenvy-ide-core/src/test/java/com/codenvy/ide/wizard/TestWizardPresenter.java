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

import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.google.gwt.junit.GWTMockUtilities;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Testing {@link WizardDialogPresenter} functionality
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestWizardPresenter {
    private static final boolean HAS_NEXT_BUTTON = true;

    private static final boolean HAS_BACK_BUTTON = true;

    private static final boolean PAGE_IS_COMPLITED = true;

    private static final boolean CAN_FINISH = true;

    @Mock
    private WizardPagePresenter currentPage;

    @Mock
    private WizardDialogView view;

    private WizardDialogPresenter presenter;

    @Before
    public void disarm() {
        // don't throw an exception if GWT.create() invoked
        GWTMockUtilities.disarm();

        setUp();
    }

    /** Create general components for all test. */
    private void setUp() {
        // TODO
//        presenter = new WizardDialogPresenter(currentPage, view);
    }

    @After
    public void restore() {
        GWTMockUtilities.restore();
    }

    /** If press on Cancel button then must be call doCancel method on currentPage and close dialog. */
    @Test
    @Ignore
    public void shouldBeCallCancelAndClose() {
        presenter.onCancelClicked();

        verify(currentPage).doCancel();
        verify(view).close();
    }

    /** If press on Finish button then must be call doFinish method on currentPage and close dialog. */
    @Test
    @Ignore
    public void shouldBeCallFinishAndClose() {
        presenter.onFinishClicked();

        verify(currentPage).doFinish();
        verify(view).close();
    }

    /** Check visible and enable properties for navigation button. */
    @Test
    @Ignore
    public void checkVisibledAndEnabledNavigationBtn() {
        when(currentPage.canFinish()).thenReturn(CAN_FINISH);
        when(currentPage.isCompleted()).thenReturn(!PAGE_IS_COMPLITED);
        when(currentPage.hasNext()).thenReturn(HAS_NEXT_BUTTON);
        when(currentPage.hasPrevious()).thenReturn(HAS_BACK_BUTTON);

        //needs reset because following methods called 2 times
        reset(view);
        presenter.updateControls();

        verify(view).setBackButtonVisible(HAS_BACK_BUTTON);
        verify(view).setNextButtonVisible(HAS_NEXT_BUTTON);
        verify(view).setNextButtonEnabled(!PAGE_IS_COMPLITED);
        verify(view).setFinishButtonEnabled(CAN_FINISH && !PAGE_IS_COMPLITED);
    }

    /** If sets new wizard page then must be set new previous page for current page and go to new page. */
    @Test
    @Ignore
    public void shouldBeChangeCurrentWizardPage() {
        WizardPagePresenter newPage = mock(WizardPagePresenter.class);
        // TODO
//        presenter.setPage(newPage);

        verify(newPage).setPrevious(currentPage);
        verify(newPage).go(view.getContentPanel());
    }
}