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
package com.codenvy.ide.ext.extensions.client.template;

import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.api.resources.CreateProjectClientService;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.template.Template;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.ext.extensions.client.UnzipTemplateClientService;
import com.codenvy.ide.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static com.codenvy.ide.api.ui.wizard.WizardPage.CommitCallback;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PAAS;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.PROJECT;
import static com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard.TEMPLATE;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Base test for creating codenvy extension from template page.
 *
 * @author Andrey Plotnikov
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseCreateExtensionTest {
    public static final String  PROJECT_NAME     = "projectName";
    public static final String  TEMPLATE_ID      = "templateID";
    public static final boolean PROVIDE_TEMPLATE = true;
    public static final boolean IN_CONTEXT       = true;
    @Mock
    protected ResourceProvider              resourceProvider;
    @Mock
    protected CommitCallback                callback;
    @Mock
    protected Project                       project;
    @Mock
    protected WizardContext                 wizardContext;
    @Mock
    protected Throwable                     throwable;
    @Mock
    protected PaaS                          paas;
    @Mock
    protected Template                      template;
    protected AbstractTemplatePage          page;
    @Mock
    protected UnzipTemplateClientService    unzipTemplateClientService;
    @Mock
    protected CreateProjectClientService    createProjectClientService;
    @Mock
    protected ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry;

    @Before
    public void setUp() {
        when(wizardContext.getData(NewProjectWizard.PROJECT_NAME)).thenReturn(PROJECT_NAME);
        when(wizardContext.getData(PAAS)).thenReturn(paas);
        when(wizardContext.getData(TEMPLATE)).thenReturn(template);
        when(template.getId()).thenReturn(TEMPLATE_ID);
    }

    @Ignore
    @Test
    public void testCreateWhenGetProjectRequestIsSuccessful() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[1];
                callback.onSuccess(project);
                return callback;
            }
        }).when(resourceProvider).getProject(anyString(), (AsyncCallback<Project>)anyObject());

        page.commit(callback);

        verify(resourceProvider).getProject(eq(PROJECT_NAME), (AsyncCallback<Project>)anyObject());
        verify(wizardContext).putData(eq(PROJECT), eq(project));
        verify(callback).onSuccess();
    }

    @Ignore
    @Test
    public void testCreateWhenCreateTutorialRequestIsFailed() throws Exception {
        page.commit(callback);

        verify(callback).onFailure(eq(throwable));
    }

    @Ignore
    @Test
    public void testCreateWhenGetProjectRequestIsFailed() throws Exception {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[1];
                callback.onFailure(throwable);
                return callback;
            }
        }).when(resourceProvider).getProject(anyString(), (AsyncCallback<Project>)anyObject());

        page.commit(callback);

        verify(resourceProvider).getProject(eq(PROJECT_NAME), (AsyncCallback<Project>)anyObject());
        verify(callback).onFailure(eq(throwable));
    }

    @Ignore
    @Test
    public void testCreateWhenRequestExceptionHappened() throws Exception {
        page.commit(callback);

        verify(callback).onFailure((Throwable)anyObject());
    }

    @Test
    public void testInContextWhenPaasProvideTemplate() {
        when(paas.isProvideTemplate()).thenReturn(PROVIDE_TEMPLATE);

        assertEquals(page.inContext(), !IN_CONTEXT);
    }

    @Test
    public void testInContextWhenThisPaasIsNotChosen() {
        when(paas.isProvideTemplate()).thenReturn(!PROVIDE_TEMPLATE);
        when(template.getId()).thenReturn(PROJECT_NAME);

        assertEquals(page.inContext(), !IN_CONTEXT);
    }

    @Test
    public void testInContext() {
        when(paas.isProvideTemplate()).thenReturn(!PROVIDE_TEMPLATE);

        assertEquals(page.inContext(), IN_CONTEXT);
    }
}