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

import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.api.wizard.WizardPage;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    public static final boolean COMPLETED    = true;
    @Mock
    private WizardDialogView      view;
    @Mock
    private Wizard                wizard;
    @Mock
    private WizardPage            firstPage;
    @Mock
    private WizardPage            otherPage;
    private WizardDialogPresenter presenter;

    @Before
    public void setUp() {
        presenter = new WizardDialogPresenter(view, wizard);

        when(wizard.flipToFirst()).thenReturn(firstPage);
    }

    @Test
    public void testOnNextClicked() throws Exception {
        preparePresenter();
        when(wizard.flipToNext()).thenReturn(otherPage);

        presenter.onNextClicked();

        verify(firstPage).storeOptions();
        verify(wizard).flipToNext();
        updateControls(otherPage);
        verify(otherPage).focusComponent();
    }

    @Test
    public void testOnBackClicked() throws Exception {
        preparePresenter();
        when(wizard.flipToPrevious()).thenReturn(otherPage);

        presenter.onBackClicked();

        verify(firstPage).removeOptions();
        verify(wizard).flipToPrevious();
        updateControls(otherPage);
    }

    @Test
    public void testOnFinishClicked() throws Exception {
        presenter.onFinishClicked();

        verify(wizard).onFinish();
        verify(view).close();
    }

    @Test
    public void testOnCancelClicked() throws Exception {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testUpdateControls() throws Exception {
        preparePresenter();

        when(wizard.hasNext()).thenReturn(HAS_NEXT);
        when(wizard.hasPrevious()).thenReturn(HAS_PREVIOUS);
        when(wizard.canFinish()).thenReturn(CAN_FINISH);
        when(firstPage.isCompleted()).thenReturn(COMPLETED);

        presenter.updateControls();

        verify(wizard).hasPrevious();
        verify(wizard).hasNext();
        verify(wizard).canFinish();

        verify(firstPage, times(2)).isCompleted();
        verify(firstPage).getCaption();
        verify(firstPage).getNotice();
        verify(firstPage).getImage();

        verify(view).setBackButtonVisible(eq(HAS_PREVIOUS));
        verify(view).setNextButtonVisible(eq(HAS_NEXT));
        verify(view).setNextButtonEnabled(eq(COMPLETED));
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
        reset(wizard);
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

        verify(wizard).hasPrevious();
        verify(wizard).hasNext();
        verify(wizard).canFinish();

        verify(wizardPage).isCompleted();
        verify(wizardPage).getCaption();
        verify(wizardPage).getNotice();
        verify(wizardPage).getImage();
    }
}