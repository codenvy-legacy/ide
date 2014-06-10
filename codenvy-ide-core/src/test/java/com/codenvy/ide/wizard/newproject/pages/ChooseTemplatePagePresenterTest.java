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
package com.codenvy.ide.wizard.newproject.pages;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.wizard.Wizard;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.wizard.newproject.pages.template.ChooseTemplatePagePresenter;
import com.codenvy.ide.wizard.newproject.pages.template.ChooseTemplatePageView;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_TYPE;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.TEMPLATE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link com.codenvy.ide.wizard.newproject.pages.template.ChooseTemplatePagePresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ChooseTemplatePagePresenterTest {
    public static final boolean COMPLETED    = true;
    public static final boolean CAN_SKIP     = true;
    public static final boolean CAN_NOT_SKIP = false;
    @Mock
    private ChooseTemplatePageView      view;
    @Mock
    private Resources                   resources;
    @Mock
    private CoreLocalizationConstant    constant;
    @Mock
    private Wizard.UpdateDelegate       delegate;
    @Mock
    private WizardContext               wizardContext;
    @Mock
    private ProjectTypeDescriptor       projectTypeDescriptor;
    @Mock
    private ProjectTemplateDescriptor   template;
    private ChooseTemplatePagePresenter page;
    private List<ProjectTemplateDescriptor> singleTemplatesList = new ArrayList<>(1);

    @Before
    public void setUp() {
        page = new ChooseTemplatePagePresenter(view, resources, constant);
        page.setContext(wizardContext);
        page.setUpdateDelegate(delegate);

        singleTemplatesList.add(template);
    }

    @Test
    public void testIsCompletedWhenTemplateIsNotChosen() throws Exception {
        assertEquals(page.isCompleted(), !COMPLETED);
    }

    @Test
    public void testIsCompleted() throws Exception {
        when(wizardContext.getData(TEMPLATE)).thenReturn(template);
        assertEquals(page.isCompleted(), COMPLETED);
    }

    @Test
    public void testCanSkip() throws Exception {
        when(wizardContext.getData(PROJECT_TYPE)).thenReturn(projectTypeDescriptor);
        when(projectTypeDescriptor.getTemplates()).thenReturn(singleTemplatesList);

        assertEquals(page.canSkip(), CAN_SKIP);

        verify(view).setTemplates((Array<ProjectTemplateDescriptor>)anyObject());
        verify(wizardContext).putData(eq(TEMPLATE), eq(template));
    }

    @Test
    public void testCanNotSkip() throws Exception {
        singleTemplatesList.add(template);
        when(wizardContext.getData(PROJECT_TYPE)).thenReturn(projectTypeDescriptor);
        when(projectTypeDescriptor.getTemplates()).thenReturn(singleTemplatesList);

        assertEquals(page.canSkip(), CAN_NOT_SKIP);

        verify(view).setTemplates((Array<ProjectTemplateDescriptor>)anyObject());
        verify(wizardContext).putData(eq(TEMPLATE), eq(template));
    }

    @Test
    public void testFocusComponent() throws Exception {
        when(wizardContext.getData(PROJECT_TYPE)).thenReturn(projectTypeDescriptor);
        when(projectTypeDescriptor.getTemplates()).thenReturn(singleTemplatesList);

        page.canSkip();
        reset(wizardContext);
        page.focusComponent();

        verify(wizardContext).putData(eq(TEMPLATE), eq(template));
        verify(view, times(2)).selectItem(template);
        verify(delegate).updateControls();
    }

    @Test
    public void testRemoveOptions() throws Exception {
        page.removeOptions();

        verify(wizardContext).removeData(eq(TEMPLATE));
    }

    @Test
    public void testGetNoticeWhenTemplateIsNotChosen() throws Exception {
        String message = "message";
        when(constant.createProjectFromTemplateSelectTemplate()).thenReturn(message);

        assertEquals(page.getNotice(), message);
    }

    @Test
    public void testGetNotice() throws Exception {
        when(wizardContext.getData(TEMPLATE)).thenReturn(template);

        assertNull(page.getNotice());
    }

    @Test
    public void testGo() throws Exception {
        when(wizardContext.getData(PROJECT_TYPE)).thenReturn(projectTypeDescriptor);
        when(projectTypeDescriptor.getTemplates()).thenReturn(singleTemplatesList);
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        page.go(container);

        verify(view).setTemplates((Array<ProjectTemplateDescriptor>)anyObject());
        verify(wizardContext).putData(eq(TEMPLATE), eq(template));
        verify(container).setWidget(eq(view));
    }

    @Test
    public void testOnTemplateSelected() throws Exception {
        page.onTemplateSelected(template);

        verify(wizardContext).putData(eq(TEMPLATE), eq(template));
        verify(view).selectItem(template);
        verify(delegate).updateControls();
    }
}