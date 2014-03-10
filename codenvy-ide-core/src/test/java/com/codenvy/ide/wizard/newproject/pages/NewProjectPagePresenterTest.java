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

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.wizard.newproject.PaaSAgentImpl;
import com.codenvy.ide.wizard.newproject.pages.start.NewProjectPagePresenter;
import com.codenvy.ide.wizard.newproject.pages.start.NewProjectPageView;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.codenvy.ide.api.ui.wizard.Wizard.UpdateDelegate;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PAAS;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private NewProjectPageView            view;
    @Mock
    private Resources                     resources;
    @Mock
    private ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry;
    @Mock
    private PaaSAgentImpl                 paasAgent;
    @Mock
    private CoreLocalizationConstant      constant;
    @Mock
    private WizardContext                 wizardContext;
    @Mock
    private ProjectTypeDescriptor         projectTypeDescriptor;
    @Mock
    private PaaS                          paas;
    @Mock
    private UpdateDelegate                delegate;
    @Mock
    private DtoFactory                    dtoFactory;
    private NewProjectPagePresenter       presenter;
    @Mock
    private ProjectServiceClient          projectServiceClient;
    @Mock
    private DtoUnmarshallerFactory        dtoUnmarshallerFactory;

    /** Prepare test when project list is come. */
    private void setUpWithProjects() {
        ProjectReference item = mock(ProjectReference.class);
        final Array<ProjectReference> itemList = Collections.createArray();
        itemList.add(item);
        when(item.getName()).thenReturn(PROJECT_NAME);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncRequestCallback<Array<ProjectReference>> callback = (AsyncRequestCallback<Array<ProjectReference>>)arguments[0];
                Method onSuccess = GwtReflectionUtils.getMethod(callback.getClass(), "onSuccess");
                onSuccess.invoke(callback, itemList);
                return callback;
            }
        }).when(projectServiceClient).getProjects((AsyncRequestCallback<Array<ProjectReference>>)anyObject());

        setUp();
    }

    /** Prepare test when project list is not come. */
    private void setUp() {
        ProjectReference item = mock(ProjectReference.class);
        final Array<ProjectReference> itemList = Collections.createArray();
        itemList.add(item);
        when(item.getName()).thenReturn(PROJECT_NAME);

        List<ProjectTemplateDescriptor> templates = new ArrayList<>(0);
        templates.add(mock(ProjectTemplateDescriptor.class));
        when(projectTypeDescriptor.getTemplates()).thenReturn(templates);

        Array<ProjectTypeDescriptor> projectTypes = Collections.createArray(projectTypeDescriptor);
        when(projectTypeDescriptorRegistry.getDescriptors()).thenReturn(projectTypes);

        Array<PaaS> paases = Collections.createArray(paas);
        when(paasAgent.getPaaSes()).thenReturn(paases);

        presenter = new NewProjectPagePresenter(view, resources, projectTypeDescriptorRegistry, constant, projectServiceClient,
                                                dtoUnmarshallerFactory);
        presenter.setContext(wizardContext);
        presenter.setUpdateDelegate(delegate);
    }

    @Test
    public void testIsCompletedWhenHaveNotProjectName() throws Exception {
        setUp();
        when(wizardContext.getData(NewProjectWizard.PROJECT_NAME)).thenReturn(null);

        assertEquals(presenter.isCompleted(), IS_NOT_COMPLETED);
    }

    @Test
    public void testIsCompletedWhenHaveNotProjectType() throws Exception {
        setUp();
        wizardContext.putData(NewProjectWizard.PROJECT_NAME, PROJECT_NAME);

        assertEquals(presenter.isCompleted(), IS_NOT_COMPLETED);
    }

    @Test
    public void testIsCompletedWhenHaveNotPaaS() throws Exception {
        setUp();
        when(wizardContext.getData(NewProjectWizard.PROJECT_NAME)).thenReturn(PROJECT_NAME);
        when(wizardContext.getData(PROJECT_TYPE)).thenReturn(mock(ProjectTypeDescriptor.class));

        assertEquals(presenter.isCompleted(), IS_NOT_COMPLETED);
    }

    @Test
    public void testIsCompleted() throws Exception {
        setUpWithProjects();
        when(wizardContext.getData(NewProjectWizard.PROJECT_NAME)).thenReturn(PROJECT_NAME);
        when(wizardContext.getData(PROJECT_TYPE)).thenReturn(mock(ProjectTypeDescriptor.class));
        when(wizardContext.getData(PAAS)).thenReturn(mock(PaaS.class));

        assertEquals(presenter.isCompleted(), IS_COMPLETED);
    }

    @Test
    public void testFocusComponent() throws Exception {
        setUp();
        when(paas.isAvailable(anyString())).thenReturn(AVAILABLE);

        presenter.focusComponent();

        verify(view).focusProjectName();
        verify(view).selectProjectType(0);
        verify(delegate, times(1)).updateControls();
        verify(wizardContext).putData(eq(PROJECT_TYPE), eq(projectTypeDescriptor));
    }

    @Test
    public void testRemoveOptions() throws Exception {
        setUp();

        presenter.removeOptions();

        verify(wizardContext).removeData(eq(PROJECT_TYPE));
        verify(wizardContext).removeData(eq(PAAS));
        verify(wizardContext).removeData(eq(NewProjectWizard.PROJECT_NAME));
    }

    @Test
    public void testGetNoticeWhenProjectNameIsEmpty() throws Exception {
        setUp();
        when(constant.enteringProjectName()).thenReturn(PROJECT_NAME);
        when(view.getProjectName()).thenReturn("");

        assertEquals(presenter.getNotice(), PROJECT_NAME);
        verify(constant).enteringProjectName();
    }

    @Test
    public void testGetNoticeWhenProjectListIsNotCome() throws Exception {
        setUp();
        when(constant.checkingProjectsList()).thenReturn(PROJECT_NAME);
        when(view.getProjectName()).thenReturn(PROJECT_NAME);

        assertEquals(presenter.getNotice(), PROJECT_NAME);
        verify(constant).checkingProjectsList();
    }

    @Test
    public void testGetNoticeWhenProjectWithSameNameIsExist() throws Exception {
        setUpWithProjects();
        when(constant.createProjectFromTemplateProjectExists(anyString())).thenReturn(PROJECT_NAME);
        when(view.getProjectName()).thenReturn(PROJECT_NAME);

        presenter.checkProjectName();

        assertEquals(PROJECT_NAME, presenter.getNotice());
        verify(constant).createProjectFromTemplateProjectExists(anyString());
    }

    @Test
    public void testGetNoticeWhenProjectNameIsIncorrect() throws Exception {
        setUpWithProjects();
        when(constant.noIncorrectProjectNameMessage()).thenReturn(PROJECT_NAME);
        when(view.getProjectName()).thenReturn("project!");
        presenter.checkProjectName();

        assertEquals(presenter.getNotice(), PROJECT_NAME);
        verify(constant).noIncorrectProjectNameMessage();
    }

    @Test
    public void testGetNoticeWhenProjectTypeIsNotSelected() throws Exception {
        setUpWithProjects();
        when(view.getProjectName()).thenReturn("projectName2");
        when(constant.noTechnologyMessage()).thenReturn(PROJECT_NAME);
        presenter.checkProjectName();

        assertEquals(presenter.getNotice(), PROJECT_NAME);
        verify(constant).noTechnologyMessage();
    }

    @Test
    public void testGetNoticeWhenPaasIsNotSelected() throws Exception {
        setUpWithProjects();
        when(view.getProjectName()).thenReturn("projectName2");
        when(constant.choosePaaS()).thenReturn(PROJECT_NAME);
        when(wizardContext.getData(eq(PROJECT_TYPE))).thenReturn(projectTypeDescriptor);
        presenter.checkProjectName();

        assertEquals(presenter.getNotice(), PROJECT_NAME);
    }

    @Test
    public void testGetNotice() throws Exception {
        setUpWithProjects();
        when(view.getProjectName()).thenReturn("projectName2");
        when(wizardContext.getData(eq(PROJECT_TYPE))).thenReturn(projectTypeDescriptor);
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
        when(paas.isAvailable(anyString())).thenReturn(AVAILABLE);

        presenter.focusComponent();
        reset(view);
        reset(delegate);
        reset(wizardContext);
        presenter.onProjectTypeSelected(0);

        verify(view).selectProjectType(0);
        verify(delegate, times(1)).updateControls();
        verify(wizardContext).putData(eq(PROJECT_TYPE), eq(projectTypeDescriptor));
    }

    @Test
    public void testCheckProjectNameWhenProjectNameIsIncorrect() throws Exception {
        setUpWithProjects();
        when(view.getProjectName()).thenReturn("projectName!");

        presenter.checkProjectName();

        verify(wizardContext).removeData(eq(NewProjectWizard.PROJECT_NAME));
        verify(delegate).updateControls();
    }

    @Test
    public void testCheckProjectNameWhenProjectWithSameNameIsExist() throws Exception {
        setUpWithProjects();
        when(view.getProjectName()).thenReturn(PROJECT_NAME);

        presenter.checkProjectName();

        verify(wizardContext).removeData(eq(NewProjectWizard.PROJECT_NAME));
        verify(delegate).updateControls();
    }

    @Test
    public void testCheckProjectNameWhenProjectListIsNotCome() throws Exception {
        setUp();
        when(view.getProjectName()).thenReturn(PROJECT_NAME);

        presenter.checkProjectName();

        verify(wizardContext).removeData(eq(NewProjectWizard.PROJECT_NAME));
        verify(delegate).updateControls();
    }

    @Test
    public void testCheckProjectNameWhenProjectNameIsEmpty() throws Exception {
        setUpWithProjects();
        when(view.getProjectName()).thenReturn("");

        presenter.checkProjectName();

        verify(wizardContext).removeData(eq(NewProjectWizard.PROJECT_NAME));
        verify(delegate).updateControls();
    }

    @Test
    public void testCheckProjectNameWhenProjectNameIsCorrect() throws Exception {
        String projectName = "projectName2";
        setUpWithProjects();
        when(view.getProjectName()).thenReturn(projectName);

        presenter.checkProjectName();

        verify(wizardContext).putData(eq(NewProjectWizard.PROJECT_NAME), eq(projectName));
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
}