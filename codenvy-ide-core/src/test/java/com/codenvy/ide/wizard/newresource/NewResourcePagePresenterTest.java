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
package com.codenvy.ide.wizard.newresource;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.wizard.Wizard;
import com.codenvy.ide.api.ui.wizard.WizardContext;
import com.codenvy.ide.api.ui.wizard.WizardPage;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceProvider;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceWizardKeys;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.wizard.NewResourceAgentImpl;
import com.codenvy.ide.wizard.newresource.page.NewResourcePagePresenter;
import com.codenvy.ide.wizard.newresource.page.NewResourcePageView;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static com.codenvy.ide.api.ui.wizard.newresource.NewResourceWizardKeys.NEW_RESOURCE_PROVIDER;
import static com.codenvy.ide.api.ui.wizard.newresource.NewResourceWizardKeys.PARENT;
import static com.codenvy.ide.api.ui.wizard.newresource.NewResourceWizardKeys.PROJECT;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link NewResourcePagePresenter} functionality.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class NewResourcePagePresenterTest {
    public static final boolean COMPLETED     = true;
    public static final String  RESOURCE_NAME = "resourceName";
    @Mock
    private Resources                                      resources;
    @Mock
    private NewResourcePageView                            view;
    @Mock
    private NewResourceAgentImpl                           newResourceAgent;
    @Mock
    private com.codenvy.ide.api.resources.ResourceProvider resourceProvider;
    @Mock
    private SelectionAgent                                 selectionAgent;
    @Mock
    private EditorAgent                                    editorAgent;
    @Mock
    private CoreLocalizationConstant                       constant;
    @Mock
    private Wizard.UpdateDelegate                          delegate;
    @Mock
    private Project                                        project;
    @Mock
    private Folder                                         folder;
    @Mock
    private File                                           file;
    @Mock
    private Loader                                         loader;
    @Mock
    private EventBus                                       eventBus;
    @Mock
    private AsyncRequestFactory                            asyncRequestFactory;
    @Mock
    private NewResourceProvider                            selectedResource;
    @Mock
    private WizardContext                                  wizardContext;
    private NewResourcePagePresenter                       presenter;

    @SuppressWarnings("unchecked")
    private void setUp(Resource resource, Array<NewResourceProvider> resourceDatas) {
        if (resource != null) {
            Selection selection = mock(Selection.class);
            when(selection.getFirstElement()).thenReturn(resource);
            when(selectionAgent.getSelection()).thenReturn(selection);
        }

        Resource child = mock(Resource.class);
        when(child.getName()).thenReturn(RESOURCE_NAME);
        when(project.getChildren()).thenReturn(Collections.createArray(child));
        when(project.getPath()).thenReturn("/TestProject");

        when(file.isFile()).thenReturn(true);
        when(folder.isFile()).thenReturn(false);

        when(newResourceAgent.getResources()).thenReturn(resourceDatas);
        when(selectedResource.inContext()).thenReturn(true);

        when(resourceProvider.getActiveProject()).thenReturn(project);

        presenter = new NewResourcePagePresenter(resources, constant, view, newResourceAgent, resourceProvider, selectionAgent, editorAgent,
                                                 loader, eventBus, asyncRequestFactory);
        presenter.setContext(wizardContext);
        presenter.setUpdateDelegate(delegate);
    }

    @Test
    public void testIsCompletedUseCase1() throws Exception {
        prepareTestCase1();

        assertEquals(presenter.isCompleted(), !COMPLETED);
    }

    @Test
    public void testGetNoticeUseCase1() throws Exception {
        prepareTestCase1();
        when(constant.enteringResourceName()).thenReturn(RESOURCE_NAME);

        assertEquals(presenter.getNotice(), RESOURCE_NAME);
        verify(constant).enteringResourceName();
    }

    /** In case resource name is empty. */
    private void prepareTestCase1() {
        setUp(project, Collections.<NewResourceProvider>createArray());
        when(view.getResourceName()).thenReturn("");
    }

    @Test
    public void testIsCompletedUseCase2() throws Exception {
        prepareTestCase2();

        assertEquals(presenter.isCompleted(), !COMPLETED);
    }

    @Test
    public void testGetNoticeUseCase2() throws Exception {
        prepareTestCase2();
        when(constant.noIncorrectResourceName()).thenReturn(RESOURCE_NAME);

        assertEquals(presenter.getNotice(), RESOURCE_NAME);
        verify(constant).noIncorrectResourceName();
    }

    /** In case resource name is invalid. */
    private void prepareTestCase2() {
        setUp(project, Collections.<NewResourceProvider>createArray(selectedResource));
        when(view.getResourceName()).thenReturn("resourceName!");

        presenter.onResourceNameChanged();
    }

    @Test
    public void testIsCompletedUseCase3() throws Exception {
        prepareTestCase3();

        assertEquals(presenter.isCompleted(), !COMPLETED);
    }

    @Test
    public void testGetNoticeUseCase3() throws Exception {
        prepareTestCase3();
        when(constant.resourceExists(anyString())).thenReturn(RESOURCE_NAME);

        assertEquals(presenter.getNotice(), RESOURCE_NAME);
        verify(constant).resourceExists(eq(RESOURCE_NAME));
    }

    /** In case a resource with same name exist. */
    private void prepareTestCase3() {
        setUp(project, Collections.<NewResourceProvider>createArray(selectedResource));
        when(view.getResourceName()).thenReturn(RESOURCE_NAME);

        presenter.onResourceNameChanged();
    }

    @Test
    public void testIsCompletedUseCase4() throws Exception {
        prepareTestCase4();

        assertEquals(presenter.isCompleted(), !COMPLETED);
    }

    @Test
    public void testGetNoticeUseCase4() throws Exception {
        prepareTestCase4();
        when(constant.chooseResourceType()).thenReturn(RESOURCE_NAME);

        assertEquals(presenter.getNotice(), RESOURCE_NAME);
        verify(constant).chooseResourceType();
    }

    /** In case no resource type is selected.. */
    private void prepareTestCase4() {
        setUp(project, Collections.<NewResourceProvider>createArray());
        when(view.getResourceName()).thenReturn("someResource");

        presenter.onResourceNameChanged();
    }

    @Test
    public void testIsCompletedUseCase5() throws Exception {
        prepareTestCase5();

        assertEquals(presenter.isCompleted(), COMPLETED);
    }

    @Test
    public void testGetNoticeUseCase5() throws Exception {
        prepareTestCase5();

        assertNull(presenter.getNotice());
    }

    /** In case all fields are fulled. */
    private void prepareTestCase5() {
        setUp(project, Collections.<NewResourceProvider>createArray(selectedResource));
        when(view.getResourceName()).thenReturn("someResource");

        presenter.onResourceNameChanged();
    }

    @Test
    public void testFocusComponent() throws Exception {
        setUp(project, Collections.<NewResourceProvider>createArray());

        presenter.focusComponent();

        verify(view).focusResourceName();
        verify(wizardContext).putData(eq(NEW_RESOURCE_PROVIDER), (NewResourceProvider)anyObject());
        verify(wizardContext).putData(eq(PROJECT), (Project)anyObject());
        verify(wizardContext).putData(eq(PARENT), (Folder)anyObject());
    }

    @Test
    public void testRemoveOptions() throws Exception {
        setUp(project, Collections.<NewResourceProvider>createArray());

        presenter.removeOptions();

        verify(wizardContext).removeData(eq(NEW_RESOURCE_PROVIDER));
        verify(wizardContext).removeData(eq(PROJECT));
        verify(wizardContext).removeData(eq(PARENT));
        verify(wizardContext).removeData(eq(NewResourceWizardKeys.RESOURCE_NAME));
    }

    @Test
    public void testGo() throws Exception {
        setUp(project, Collections.<NewResourceProvider>createArray());
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        presenter.go(container);

        verify(container).setWidget(eq(view));
    }

    @Test
    public void testOnResourceTypeSelected() throws Exception {
        prepareTestCase5();
        reset(view);
        reset(delegate);

        presenter.onResourceTypeSelected(selectedResource);

        verify(view).selectResourceType(eq(selectedResource));
        verify(wizardContext).putData(eq(NEW_RESOURCE_PROVIDER), (NewResourceProvider)anyObject());
        verify(delegate, times(2)).updateControls();
    }

    @Test
    public void testOnResourceNameChanged() throws Exception {
        prepareTestCase5();

        verify(view).getResourceName();
        verify(delegate).updateControls();
        verify(selectedResource).getExtension();
        verify(project).getChildren();
        verify(wizardContext).putData(eq(NewResourceWizardKeys.RESOURCE_NAME), anyString());
    }

    @Test
    public void testOnResourceNameChangedWhenSelectedResourceIsEmpty() throws Exception {
        prepareTestCase4();

        verify(view).getResourceName();
        verify(delegate).updateControls();
        verify(selectedResource, never()).getExtension();
        verify(project, never()).getChildren();
        verify(wizardContext).putData(eq(NewResourceWizardKeys.RESOURCE_NAME), anyString());
    }

    @Test
    public void testCommitWhenFileIsSelected() throws Exception {
        // Needs custom implementation of File class because getParent() method is final. That's why mockito can't override its method.
        TestFile file = new TestFile();
        file.setParent(folder);
        setUp(file, Collections.<NewResourceProvider>createArray(selectedResource));
        when(view.getResourceName()).thenReturn(RESOURCE_NAME);

        presenter.commit(mock(WizardPage.CommitCallback.class));

        verify(selectedResource).create(eq(RESOURCE_NAME), eq(folder), eq(project), (AsyncCallback<Resource>)anyObject());
    }

    @Test
    public void testCommitWhenFolderIsSelected() throws Exception {
        setUp(folder, Collections.<NewResourceProvider>createArray(selectedResource));
        when(view.getResourceName()).thenReturn(RESOURCE_NAME);

        presenter.commit(mock(WizardPage.CommitCallback.class));

        verify(selectedResource).create(eq(RESOURCE_NAME), eq(folder), eq(project), (AsyncCallback<Resource>)anyObject());
    }

    @Test
    public void testCommitWhenNothingIsSelected() throws Exception {
        setUp(null, Collections.<NewResourceProvider>createArray(selectedResource));
        when(view.getResourceName()).thenReturn(RESOURCE_NAME);

        presenter.commit(mock(WizardPage.CommitCallback.class));

        verify(selectedResource).create(eq(RESOURCE_NAME), eq(project), eq(project), (AsyncCallback<Resource>)anyObject());
    }

    @Test
    public void testCommitWhenFolderIsCreated() throws Exception {
        setUp(project, Collections.<NewResourceProvider>createArray(selectedResource));
        when(view.getResourceName()).thenReturn(RESOURCE_NAME);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                AsyncCallback<Resource> callback = (AsyncCallback<Resource>)arguments[3];
                callback.onSuccess(folder);
                return null;
            }
        }).when(selectedResource).create(anyString(), (Folder)anyObject(), (Project)anyObject(), (AsyncCallback<Resource>)anyObject());

        presenter.commit(mock(WizardPage.CommitCallback.class));

        verify(editorAgent, never()).openEditor((File)anyObject());
    }

    @Test
    public void testCommitWhenFileIsCreated() throws Exception {
        setUp(project, Collections.<NewResourceProvider>createArray(selectedResource));
        when(view.getResourceName()).thenReturn(RESOURCE_NAME);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Object[] arguments = invocationOnMock.getArguments();
                AsyncCallback<Resource> callback = (AsyncCallback<Resource>)arguments[3];
                callback.onSuccess(file);
                return null;
            }
        }).when(selectedResource).create(anyString(), (Folder)anyObject(), (Project)anyObject(), (AsyncCallback<Resource>)anyObject());

        presenter.commit(mock(WizardPage.CommitCallback.class));

        verify(editorAgent).openEditor((File)anyObject());
    }
}