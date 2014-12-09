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
package com.codenvy.ide.extension.runner.client.manage.ram;

import com.codenvy.ide.api.preferences.PreferencePagePresenter;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.codenvy.ide.extension.runner.client.RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link RamManagePresenter} functionality.
 *
 * @author Roman Nikitenko
 */
@RunWith(MockitoJUnitRunner.class)
public class RamManagePresenterTest {
    @Mock
    private RunnerLocalizationConstant localizationConstant;
    @Mock
    private RamManagerView             view;
    @Mock
    private PreferencesManager         preferencesManager;
    @InjectMocks
    private RamManagePresenter         presenter;

    private PreferencePagePresenter.DirtyStateListener delegate;

    @Before
    public void setUp() {
        PreferencePagePresenter.DirtyStateListener delegate = mock(PreferencePagePresenter.DirtyStateListener.class);
        this.delegate = delegate;
        presenter.setUpdateDelegate(delegate);
    }

    @Test
    public void testGo() {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);
        when(preferencesManager.getValue(PREFS_RUNNER_RAM_SIZE_DEFAULT)).thenReturn("128");

        presenter.go(container);

        verify(container).setWidget(eq(view));
        verify(view).showRam(eq("128"));
        verify(preferencesManager).getValue(eq(PREFS_RUNNER_RAM_SIZE_DEFAULT));
    }

    @Test
    public void testGoWhenRamIsNull() {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);
        when(preferencesManager.getValue(PREFS_RUNNER_RAM_SIZE_DEFAULT)).thenReturn(null);

        presenter.go(container);

        verify(container).setWidget(eq(view));
        verify(view, never()).showRam(anyString());
        verify(preferencesManager).getValue(eq(PREFS_RUNNER_RAM_SIZE_DEFAULT));
    }

    @Test
    public void testValidateRamSizeWhenRamIsEmpty() {

        presenter.validateRamSize("");
        boolean dirty = presenter.isDirty();

        Assert.assertEquals(true, dirty);
        verify(view).hideWarnMessage();
        verify(delegate).onDirtyChanged();
        verify(view, never()).showWarnMessage(anyString());
    }

    @Test
    public void testValidateRamSizeWhenRamIsMultipleOf128() {

        presenter.validateRamSize("256");
        boolean dirty = presenter.isDirty();

        Assert.assertEquals(true, dirty);
        verify(view).hideWarnMessage();
        verify(delegate).onDirtyChanged();
        verify(view, never()).showWarnMessage(anyString());
    }

    @Test
    public void testValidateRamSizeWhenRamIsNotMultipleOf128() {

        presenter.validateRamSize("130");
        boolean dirty = presenter.isDirty();

        Assert.assertEquals(false, dirty);
        verify(localizationConstant).ramSizeMustBeMultipleOf(eq("128"));
        verify(view).showWarnMessage(eq(localizationConstant.ramSizeMustBeMultipleOf("128")));
        verify(view, never()).hideWarnMessage();
        verify(delegate).onDirtyChanged();
    }

    @Test
    public void testValidateRamSizeWhenEnteredValueNotCorrect() {

        presenter.validateRamSize("128ff");
        boolean dirty = presenter.isDirty();

        Assert.assertEquals(false, dirty);
        verify(localizationConstant).enteredValueNotCorrect();
        verify(view).showWarnMessage(eq(localizationConstant.enteredValueNotCorrect()));
        verify(view, never()).hideWarnMessage();
        verify(delegate).onDirtyChanged();
    }

    @Test
    public void testStoreChanges() {
        when(view.getRam()).thenReturn("128");

        presenter.storeChanges();
        boolean dirty = presenter.isDirty();

        Assert.assertEquals(false, dirty);
        verify(view).getRam();
        verify(preferencesManager).setPreference(eq(PREFS_RUNNER_RAM_SIZE_DEFAULT), eq("128"));
    }

    @Test
    public void testRevertChanges() {
        when(preferencesManager.getValue(PREFS_RUNNER_RAM_SIZE_DEFAULT)).thenReturn("128");

        presenter.revertChanges();
        boolean dirty = presenter.isDirty();

        Assert.assertEquals(false, dirty);
        verify(view).showRam(eq("128"));
        verify(view).hideWarnMessage();
        verify(preferencesManager).getValue(eq(PREFS_RUNNER_RAM_SIZE_DEFAULT));
    }
}
