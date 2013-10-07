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

import com.codenvy.ide.api.ui.wizard.WizardModel;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testing {@link WizardDialogPresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class WizardDialogPresenterTest {
    public static final boolean IS_SHOWN     = true;
    public static final boolean HAS_NEXT     = true;
    public static final boolean HAS_PREVIOUS = true;
    public static final boolean CAN_FINISH   = true;
    public static final boolean IS_COMPLITED = true;
    @Mock
    private WizardDialogView      view;
    @Mock
    private WizardModel           wizardModel;
    @Mock
    private WizardPage            firstPage;
    @Mock
    private WizardPage            otherPage;
    private WizardDialogPresenter presenter;

    @Before
    public void disarm() {
        // don't throw an exception if GWT.create() invoked
        GWTMockUtilities.disarm();

        presenter = new WizardDialogPresenter(view, wizardModel);

        when(wizardModel.flipToFirst()).thenReturn(firstPage);
    }

    @After
    public void restore() {
        GWTMockUtilities.restore();
    }

    @Test
    public void testOnNextClicked() throws Exception {
        preparePresenter();
        when(wizardModel.flipToNext()).thenReturn(otherPage);

        presenter.onNextClicked();

        verify(firstPage).storeOptions();
        verify(wizardModel).flipToNext();
        updateControls(otherPage);
        verify(otherPage).focusComponent();
    }

    @Test
    public void testOnBackClicked() throws Exception {
        preparePresenter();
        when(wizardModel.flipToPrevious()).thenReturn(otherPage);

        presenter.onBackClicked();

        verify(firstPage).removeOptions();
        verify(wizardModel).flipToPrevious();
        updateControls(otherPage);
    }

    @Test
    public void testOnFinishClicked() throws Exception {
        presenter.onFinishClicked();

        verify(wizardModel).onFinish();
        verify(view).close();
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(wizardModel).onCancel();
        verify(view).close();
    }

    @Test
    public void testUpdateControls() throws Exception {
        preparePresenter();

        when(wizardModel.hasNext()).thenReturn(HAS_NEXT);
        when(wizardModel.hasPrevious()).thenReturn(HAS_PREVIOUS);
        when(wizardModel.canFinish()).thenReturn(CAN_FINISH);
        when(firstPage.isCompleted()).thenReturn(IS_COMPLITED);

        presenter.updateControls();

        verify(wizardModel).hasPrevious();
        verify(wizardModel).hasNext();
        verify(wizardModel).canFinish();

        verify(firstPage, times(2)).isCompleted();
        verify(firstPage).getCaption();
        verify(firstPage).getNotice();
        verify(firstPage).getImage();

        verify(view).setBackButtonVisible(eq(HAS_PREVIOUS));
        verify(view).setNextButtonVisible(eq(HAS_NEXT));
        verify(view).setNextButtonEnabled(eq(IS_COMPLITED));
        verify(view).setFinishButtonEnabled(eq(IS_SHOWN));
        verify(view).setCaption(anyString());
        verify(view).setNotice(anyString());
        verify(view).setImage((ImageResource)anyObject());
    }

    @Test
    public void testShow() throws Exception {
        presenter.show();

        verify(view).setTitle(anyString());
        verify(view).showDialog();
        verify(view).setEnabledAnimation(eq(IS_SHOWN));
        verify(view).getContentPanel();

        verify(firstPage).focusComponent();
        verify(firstPage).go((AcceptsOneWidget)anyObject());

        updateControls(firstPage);
    }

    /** Prepare presenter fields for testing */
    private void preparePresenter() {
        presenter.show();

        //clear information about using those components
        reset(view);
        reset(wizardModel);
        reset(firstPage);
    }

    /**
     * Check updating buttons and other gui elements for current page.
     *
     * @param wizardPage
     *         page that affects on gui elements
     */
    private void updateControls(WizardPage wizardPage) {
        verify(view).setBackButtonVisible(anyBoolean());
        verify(view).setNextButtonVisible(anyBoolean());
        verify(view).setNextButtonEnabled(anyBoolean());
        verify(view).setFinishButtonEnabled(anyBoolean());
        verify(view).setCaption(anyString());
        verify(view).setNotice(anyString());
        verify(view).setImage((ImageResource)anyObject());

        verify(wizardModel).hasPrevious();
        verify(wizardModel).hasNext();
        verify(wizardModel).canFinish();

        verify(wizardPage).isCompleted();
        verify(wizardPage).getCaption();
        verify(wizardPage).getNotice();
        verify(wizardPage).getImage();
    }
}