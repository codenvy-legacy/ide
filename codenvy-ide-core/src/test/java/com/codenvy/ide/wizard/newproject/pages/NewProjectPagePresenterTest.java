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

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.WizardKeys;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.paas.PaaSAgentImpl;
import com.codenvy.ide.wizard.newproject.ProjectTypeAgentImpl;
import com.codenvy.ide.wizard.newproject.ProjectTypeData;
import com.codenvy.ide.wizard.newproject.pages.start.NewProjectPagePresenter;
import com.codenvy.ide.wizard.newproject.pages.start.NewProjectPageView;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static com.codenvy.ide.api.ui.wizard.Wizard.UpdateDelegate;
import static com.codenvy.ide.wizard.newproject.NewProjectWizard.PAAS;
import static com.codenvy.ide.wizard.newproject.NewProjectWizard.PROJECT_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

/**
 * Testing {@link com.codenvy.ide.wizard.newproject.pages.start.NewProjectPagePresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class NewProjectPagePresenterTest {
    public static final String  PROJECT_NAME     = "project name";
    public static final boolean IS_COMPLETED     = true;
    public static final boolean IS_NOT_COMPLETED = false;
    public static final boolean AVAILABLE        = true;
    @Mock
    private NewProjectPageView       view;
    @Mock
    private Resources                resources;
    @Mock
    private ProjectTypeAgentImpl     projectTypeAgent;
    @Mock
    private PaaSAgentImpl            paasAgent;
    @Mock
    private ResourceProvider         resourceProvider;
    @Mock
    private CoreLocalizationConstant constant;
    @Mock
    private WizardContext            wizardContext;
    @Mock
    private ProjectTypeData          projectType;
    @Mock
    private PaaS                     paas;
    @Mock
    private UpdateDelegate           delegate;
    private NewProjectPagePresenter  presenter;

    /** Prepare test when project list is come. */
    private void setUpWithProjects() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                AsyncCallback<JsonArray<String>> callback = (AsyncCallback<JsonArray<String>>)arguments[0];
                JsonArray<String> projects = JsonCollections.createArray(PROJECT_NAME);
                callback.onSuccess(projects);
                return null;
            }
        }).when(resourceProvider).listProjects((AsyncCallback<JsonArray<String>>)anyObject());

        setUp();
    }

    /** Prepare test when project list is not come. */
    private void setUp() {
        JsonArray<ProjectTypeData> projectTypes = JsonCollections.createArray(projectType);
        when(projectTypeAgent.getProjectTypes()).thenReturn(projectTypes);

        JsonArray<PaaS> paases = JsonCollections.createArray(paas);
        when(paasAgent.getPaaSes()).thenReturn(paases);

        presenter = new NewProjectPagePresenter(view, resources, projectTypeAgent, paasAgent, resourceProvider, constant);
        presenter.setContext(wizardContext);
        presenter.setUpdateDelegate(delegate);
    }

    @Test
    public void testIsCompletedWhenHaveNotProjectName() throws Exception {
        setUp();
        when(wizardContext.getData(WizardKeys.PROJECT_NAME)).thenReturn(null);

        assertEquals(presenter.isCompleted(), IS_NOT_COMPLETED);
    }

    @Test
    public void testIsCompletedWhenHaveNotProjectType() throws Exception {
        setUp();
        wizardContext.putData(WizardKeys.PROJECT_NAME, PROJECT_NAME);

        assertEquals(presenter.isCompleted(), IS_NOT_COMPLETED);
    }

    @Test
    public void testIsCompletedWhenHaveNotPaaS() throws Exception {
        setUp();
        when(wizardContext.getData(WizardKeys.PROJECT_NAME)).thenReturn(PROJECT_NAME);
        when(wizardContext.getData(PROJECT_TYPE)).thenReturn(mock(ProjectTypeData.class));

        assertEquals(presenter.isCompleted(), IS_NOT_COMPLETED);
    }

    @Test
    public void testIsCompleted() throws Exception {
        setUpWithProjects();
        when(wizardContext.getData(WizardKeys.PROJECT_NAME)).thenReturn(PROJECT_NAME);
        when(wizardContext.getData(PROJECT_TYPE)).thenReturn(mock(ProjectTypeData.class));
        when(wizardContext.getData(PAAS)).thenReturn(mock(PaaS.class));

        assertEquals(presenter.isCompleted(), IS_COMPLETED);
    }

    @Test
    public void testFocusComponent() throws Exception {
        setUp();
        when(paas.isAvailable(anyString(), (JsonArray<String>)anyObject())).thenReturn(AVAILABLE);

        presenter.focusComponent();

        verify(view).focusProjectName();
        verify(view).selectProjectType(0);
        verify(view).selectPaas(0);
        verify(delegate, times(2)).updateControls();
        verify(wizardContext).putData(eq(PROJECT_TYPE), eq(projectType));
        verify(wizardContext).putData(eq(PAAS), eq(paas));
    }

    @Test
    public void testRemoveOptions() throws Exception {
        setUp();

        presenter.removeOptions();

        verify(wizardContext).removeData(eq(PROJECT_TYPE));
        verify(wizardContext).removeData(eq(PAAS));
        verify(wizardContext).removeData(eq(WizardKeys.PROJECT_NAME));
    }

    @Test
    public void testGetNoticeWhenProjectNameIsEmpty() throws Exception {
        setUp();
        when(view.getProjectName()).thenReturn("");

        assertEquals(presenter.getNotice(), "Please, enter a project name.");
    }

    @Test
    public void testGetNoticeWhenProjectListIsNotCome() throws Exception {
        setUp();
        when(view.getProjectName()).thenReturn(PROJECT_NAME);

        assertEquals(presenter.getNotice(), "Please wait, checking project list");
    }

    @Test
    public void testGetNoticeWhenProjectWithSameNameIsExist() throws Exception {
        setUpWithProjects();
        when(view.getProjectName()).thenReturn(PROJECT_NAME);
        presenter.checkProjectName();

        assertEquals(presenter.getNotice(), "Project with this name already exists.");
    }

    @Test
    public void testGetNoticeWhenProjectNameIsIncorrect() throws Exception {
        setUpWithProjects();
        when(view.getProjectName()).thenReturn("project!");
        presenter.checkProjectName();

        assertEquals(presenter.getNotice(), "Incorrect project name.");
    }

    @Test
    public void testGetNoticeWhenProjectTypeIsNotSelected() throws Exception {
        String message = "message";
        setUpWithProjects();
        when(view.getProjectName()).thenReturn("projectName2");
        when(constant.noTechnologyMessage()).thenReturn(message);
        presenter.checkProjectName();

        assertEquals(presenter.getNotice(), message);
        verify(constant).noTechnologyMessage();
    }

    @Test
    public void testGetNoticeWhenPaasIsNotSelected() throws Exception {
        setUpWithProjects();
        when(view.getProjectName()).thenReturn("projectName2");
        when(wizardContext.getData(eq(PROJECT_TYPE))).thenReturn(projectType);
        presenter.checkProjectName();

        assertEquals(presenter.getNotice(), "Please, choose PaaS");
    }

    @Test
    public void testGetNotice() throws Exception {
        setUpWithProjects();
        when(view.getProjectName()).thenReturn("projectName2");
        when(wizardContext.getData(eq(PROJECT_TYPE))).thenReturn(projectType);
        when(wizardContext.getData(eq(PAAS))).thenReturn(paas);
        presenter.checkProjectName();

        assertNull(presenter.getNotice());
    }

    @Test
    public void testGo() throws Exception {
        setUp();
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        presenter.go(container);

        verify(container).setWidget(eq(view));
    }

    @Test
    public void testOnProjectTypeSelected() throws Exception {
        setUp();
        when(paas.isAvailable(anyString(), (JsonArray<String>)anyObject())).thenReturn(AVAILABLE);

        presenter.focusComponent();
        reset(view);
        reset(delegate);
        reset(wizardContext);
        presenter.onProjectTypeSelected(0);

        verify(view).selectProjectType(0);
        verify(view).selectPaas(0);
        verify(delegate, times(2)).updateControls();
        verify(wizardContext).putData(eq(PROJECT_TYPE), eq(projectType));
        verify(wizardContext).putData(eq(PAAS), eq(paas));
    }

    @Test
    public void testOnPaaSSelected() throws Exception {
        setUp();

        presenter.focusComponent();
        reset(view);
        reset(delegate);
        reset(wizardContext);
        presenter.onPaaSSelected(0);

        verify(view).selectPaas(0);
        verify(delegate).updateControls();
        verify(wizardContext).putData(eq(PAAS), eq(paas));
    }

    @Test
    public void testCheckProjectNameWhenProjectNameIsIncorrect() throws Exception {
        setUpWithProjects();
        when(view.getProjectName()).thenReturn("projectName!");

        presenter.checkProjectName();

        verify(wizardContext).removeData(eq(WizardKeys.PROJECT_NAME));
        verify(delegate).updateControls();
    }

    @Test
    public void testCheckProjectNameWhenProjectWithSameNameIsExist() throws Exception {
        setUpWithProjects();
        when(view.getProjectName()).thenReturn(PROJECT_NAME);

        presenter.checkProjectName();

        verify(wizardContext).removeData(eq(WizardKeys.PROJECT_NAME));
        verify(delegate).updateControls();
    }

    @Test
    public void testCheckProjectNameWhenProjectListIsNotCome() throws Exception {
        setUp();
        when(view.getProjectName()).thenReturn(PROJECT_NAME);

        presenter.checkProjectName();

        verify(wizardContext).removeData(eq(WizardKeys.PROJECT_NAME));
        verify(delegate).updateControls();
    }

    @Test
    public void testCheckProjectNameWhenProjectNameIsEmpty() throws Exception {
        setUpWithProjects();
        when(view.getProjectName()).thenReturn("");

        presenter.checkProjectName();

        verify(wizardContext).removeData(eq(WizardKeys.PROJECT_NAME));
        verify(delegate).updateControls();
    }

    @Test
    public void testCheckProjectNameWhenProjectNameIsCorrect() throws Exception {
        String projectName = "projectName2";
        setUpWithProjects();
        when(view.getProjectName()).thenReturn(projectName);

        presenter.checkProjectName();

        verify(wizardContext).putData(eq(WizardKeys.PROJECT_NAME), eq(projectName));
        verify(delegate).updateControls();
    }

    @Test
    public void testOnTechnologyIconClicked() throws Exception {
        setUp();

        int top = 100;
        int left = 100;

        presenter.onTechnologyIconClicked(left, top);

        verify(view).showPopup(anyString(), eq(left), eq(top));
        verify(constant).chooseTechnologyTooltip();
    }

    @Test
    public void testOnPaaSIconClicked() throws Exception {
        setUp();

        int top = 100;
        int left = 100;

        presenter.onPaaSIconClicked(left, top);

        verify(view).showPopup(anyString(), eq(left), eq(top));
        verify(constant).choosePaaSTooltip();
    }
}