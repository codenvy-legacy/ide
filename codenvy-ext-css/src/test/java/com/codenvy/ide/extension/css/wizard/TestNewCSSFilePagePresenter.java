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
package com.codenvy.ide.extension.css.wizard;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter.WizardUpdateDelegate;
import com.codenvy.ide.api.ui.wizard.newfile.NewGenericFilePageView;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testing {@link NewCSSFilePagePresenter} functionality
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class TestNewCSSFilePagePresenter {
    private static final boolean IS_COMPLITED = true;

    private static final boolean IS_FILE = true;

    private NewCSSFilePagePresenter presenter;

    private NewGenericFilePageView view;

    private Project project;

    @Before
    public void disarm() {
        // don't throw an exception if GWT.create() invoked
        GWTMockUtilities.disarm();

        setUp();
    }

    /** Create general components for all test. */
    private void setUp() {
        ResourceProvider resourceProvider = mock(ResourceProvider.class);
        project = mock(Project.class);
        when(resourceProvider.getActiveProject()).thenReturn(project);

        view = mock(NewGenericFilePageView.class);
        presenter = new NewCSSFilePagePresenter((ImageResource)null, view, resourceProvider, mock(SelectionAgent.class));
        presenter.setUpdateDelegate(mock(WizardUpdateDelegate.class));
    }

    @After
    public void restore() {
        GWTMockUtilities.restore();
    }

    /** If file name is empty then must be showed message about this situation. */
    @Test
    public void shouldBeEnterFileNameMessage() {
        when(view.getFileName()).thenReturn("");
        when(project.getChildren()).thenReturn(JsonCollections.<Resource>createArray());

        presenter.onValueChanged();

        assertEquals(presenter.getNotice(), "The file name can't be empty.");
        assertEquals(presenter.isCompleted(), !IS_COMPLITED);
    }

    /** If file name has incorrect symbol then must be showed message about this situation. */
    @Test
    public void shouldBeInvalidNameMessage() {
        when(view.getFileName()).thenReturn("test*");
        when(project.getChildren()).thenReturn(JsonCollections.<Resource>createArray());

        presenter.onValueChanged();

        assertEquals(presenter.getNotice(), "The file name has incorrect symbol.");
        assertEquals(presenter.isCompleted(), !IS_COMPLITED);
    }

    /** If file name has incorrect extension then must be showed message about this situation. */
    @Test
    public void shouldBeInvalidExtensionMessage() {
        checkIfIncorrectExtension("test.c");
        checkIfIncorrectExtension("test.");
        checkIfIncorrectExtension("test.ccs");
    }

    /**
     * Check situation when file name has incorrect extension.
     *
     * @param fileName
     */
    private void checkIfIncorrectExtension(String fileName) {
        when(view.getFileName()).thenReturn(fileName);
        when(project.getChildren()).thenReturn(JsonCollections.<Resource>createArray());

        presenter.onValueChanged();

        assertEquals(presenter.getNotice(), "The file name must end in one of the following extensions [css].");
        assertEquals(presenter.isCompleted(), !IS_COMPLITED);
    }

    /** If file with entered name and extension exists then must be showed message about this situation. */
    @Test
    public void shouldBeFileExistMessage() {
        checkIfFileExist("test", "test.css");
        checkIfFileExist("test.css", "test.css");
    }

    /**
     * Check situation when file with entered name and extension exists.
     *
     * @param newFileName
     * @param existFileName
     */
    private void checkIfFileExist(String newFileName, String existFileName) {
        Resource file = mock(File.class);
        when(file.getName()).thenReturn(existFileName);
        when(file.isFile()).thenReturn(IS_FILE);

        JsonArray<Resource> children = JsonCollections.createArray();
        children.add(file);

        when(view.getFileName()).thenReturn(newFileName);
        when(project.getChildren()).thenReturn(children);

        presenter.onValueChanged();

        assertEquals(presenter.getNotice(), "The file with same name already exists.");
        assertEquals(presenter.isCompleted(), !IS_COMPLITED);
    }

    /** If file name is correct then must be not showing any message. */
    @Test
    public void shouldBeCorrectName() {
        checkIfCorrectName("test");
        checkIfCorrectName("test.css");
    }

    /**
     * Check situation when file can be created.
     *
     * @param fileName
     */
    private void checkIfCorrectName(String fileName) {
        when(view.getFileName()).thenReturn(fileName);
        when(project.getChildren()).thenReturn(JsonCollections.<Resource>createArray());

        presenter.onValueChanged();

        assertEquals(presenter.getNotice(), null);
        assertEquals(presenter.isCompleted(), IS_COMPLITED);
    }

    /** If calls doFinish method then must be call createFile method. */
    @Test
    @SuppressWarnings("unchecked")
    public void shouldBeCallCreateFolder() {
        presenter.doFinish();

        verify(project).createFile((Folder)anyObject(), anyString(), anyString(), anyString(),
                                   ((AsyncCallback<File>)anyObject()));
    }
}