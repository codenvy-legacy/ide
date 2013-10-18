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
package com.codenvy.ide.wizard.newproject.pages;

import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.ui.wizard.Wizard;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.resources.ProjectTypeData;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.template.TemplateAgentImpl;
import com.codenvy.ide.wizard.WizardResource;
import com.codenvy.ide.wizard.newproject.pages.template.ChooseTemplatePagePresenter;
import com.codenvy.ide.wizard.newproject.pages.template.ChooseTemplatePageView;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_TYPE;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.TEMPLATE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

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
    private WizardResource              resources;
    @Mock
    private TemplateAgentImpl           templateAgent;
    @Mock
    private Wizard.UpdateDelegate       delegate;
    @Mock
    private WizardContext               wizardContext;
    private Template                    template;
    private ProjectTypeData             projectType;
    private ChooseTemplatePagePresenter page;

    @Before
    public void setUp() {
        template = new Template("id", "title", null, "primaryNature", JsonCollections.createArray("secondaryNature"));
        projectType = new ProjectTypeData("typeName", "title", null, "primaryNature", JsonCollections.createArray("secondaryNature"));

        page = new ChooseTemplatePagePresenter(view, resources, templateAgent);
        page.setContext(wizardContext);
        page.setUpdateDelegate(delegate);
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
        when(wizardContext.getData(PROJECT_TYPE)).thenReturn(projectType);
        when(templateAgent.getTemplatesForProjectType(anyString(), (JsonArray<String>)anyObject()))
                .thenReturn(JsonCollections.createArray(template));

        assertEquals(page.canSkip(), CAN_SKIP);

        verify(view).setTemplates((JsonArray<Template>)anyObject());
        verify(wizardContext).putData(eq(TEMPLATE), eq(template));
    }

    @Test
    public void testCanNotSkip() throws Exception {
        when(wizardContext.getData(PROJECT_TYPE)).thenReturn(projectType);
        when(templateAgent.getTemplatesForProjectType(anyString(), (JsonArray<String>)anyObject()))
                .thenReturn(JsonCollections.createArray(template, template));

        assertEquals(page.canSkip(), CAN_NOT_SKIP);

        verify(view).setTemplates((JsonArray<Template>)anyObject());
        verify(wizardContext).putData(eq(TEMPLATE), eq(template));
    }

    @Test
    public void testFocusComponent() throws Exception {
        when(wizardContext.getData(PROJECT_TYPE)).thenReturn(projectType);
        when(templateAgent.getTemplatesForProjectType(anyString(), (JsonArray<String>)anyObject()))
                .thenReturn(JsonCollections.createArray(template));

        page.canSkip();
        reset(wizardContext);
        page.focusComponent();

        verify(wizardContext).putData(eq(TEMPLATE), eq(template));
        verify(view).selectItem(template);
        verify(delegate).updateControls();
    }

    @Test
    public void testRemoveOptions() throws Exception {
        page.removeOptions();

        verify(wizardContext).removeData(eq(TEMPLATE));
    }

    @Test
    public void testGetNoticeWhenTemplateIsNotChosen() throws Exception {
        assertEquals(page.getNotice(), "Please, select template.");
    }

    @Test
    public void testGetNotice() throws Exception {
        when(wizardContext.getData(TEMPLATE)).thenReturn(template);

        assertNull(page.getNotice());
    }

    @Test
    public void testGo() throws Exception {
        when(wizardContext.getData(PROJECT_TYPE)).thenReturn(projectType);
        when(templateAgent.getTemplatesForProjectType(anyString(), (JsonArray<String>)anyObject()))
                .thenReturn(JsonCollections.createArray(template, template));
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        page.go(container);

        verify(view).setTemplates((JsonArray<Template>)anyObject());
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