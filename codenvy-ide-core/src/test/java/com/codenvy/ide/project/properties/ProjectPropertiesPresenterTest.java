/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.project.properties;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.project.properties.add.AddNewPropertyPresenter;
import com.codenvy.ide.project.properties.edit.EditPropertyPresenter;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Test for {@link ProjectPropertiesPresenter}.
 * 
 * @author Ann Shumilova
 */
public class ProjectPropertiesPresenterTest extends PropertiesBaseTest {
    @Mock
    private ProjectPropertiesView      view;
    @Mock
    private EditPropertyPresenter      editPropertyPresenter;
    @Mock
    private AddNewPropertyPresenter    addNewPropertyPresenter;
    @Mock
    private Property                   selectedProperty;

    private ProjectPropertiesPresenter presenter;


    /** {@inheritDoc} */
    @Override
    public void disarm() {
        super.disarm();

        presenter =
                    new ProjectPropertiesPresenter(view, resourceProvider, localization, notificationManager, editPropertyPresenter,
                                                   addNewPropertyPresenter);
        when(selectedProperty.getName()).thenReturn(PROPERTY_MIMETYPE);
    }

    @Test
    public void testShowDialogWhenSuccess() {
        refreshPropertiesSuccess();
        presenter.showProperties();
        verify(resourceProvider).getActiveProject();
        verify(project).refreshProperties((AsyncCallback<Project>)anyObject());
        verify(view).setSaveButtonEnabled(eq(DISABLE_BUTTON));
        verify(view).setDeleteButtonEnabled(eq(DISABLE_BUTTON));
        verify(view).setEditButtonEnabled(eq(DISABLE_BUTTON));
        verify(view).setProperties((Array<Property>)anyObject());
        verify(view).showDialog();
        verify(localization, never()).getProjectPropertiesFailed();
        verify(notificationManager,
               never()).showNotification((Notification)anyObject());
    }


    @Test
    public void testShowDialogWhenFail() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[0];
                callback.onFailure(mock(Throwable.class));
                return callback;
            }
        }).when(project).refreshProperties((AsyncCallback<Project>)anyObject());

        presenter.showProperties();

        verify(resourceProvider).getActiveProject();
        verify(project).refreshProperties((AsyncCallback<Project>)anyObject());
        verify(localization).getProjectPropertiesFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
        verify(view, never()).showDialog();
    }

    @Test
    public void testOnCancelClicked() {
        presenter.onCancelClicked();

        verify(view).close();
    }

    @Test
    public void testOnSelectedProperty() {
        presenter.selectedProperty(mock(Property.class));

        verify(view).setDeleteButtonEnabled(ENABLE_BUTTON);
        verify(view).setEditButtonEnabled(ENABLE_BUTTON);

        presenter.selectedProperty((Property)null);
        verify(view).setDeleteButtonEnabled(DISABLE_BUTTON);
        verify(view).setEditButtonEnabled(DISABLE_BUTTON);
    }

    @Test
    public void testOnDeleteClicked() {
        presenter.selectedProperty(selectedProperty);
        presenter.onDeleteClicked();

        verify(localization).deletePropertyQuestion(anyString());

        verify(view).setSaveButtonEnabled(ENABLE_BUTTON);
        verify(selectedProperty).setValue((Array<String>)null);
        verify(view).setProperties((Array<Property>)anyObject());
    }

    @Test
    public void testOnAddClicked() {
        refreshPropertiesSuccess();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Property> callback = (AsyncCallback<Property>)arguments[0];
                callback.onSuccess(mock(Property.class));
                return callback;
            }
        }).when(addNewPropertyPresenter).addNewProperty((AsyncCallback<Property>)anyObject());

        presenter.showProperties();
        presenter.onAddClicked();

        verify(addNewPropertyPresenter).addNewProperty((AsyncCallback<Property>)anyObject());
        verify(view).setSaveButtonEnabled(eq(ENABLE_BUTTON));
        verify(view, times(2)).setProperties((Array<Property>)anyObject());
    }

    @Test
    public void testOnAddExisting() {
        refreshPropertiesSuccess();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Property> callback = (AsyncCallback<Property>)arguments[0];
                callback.onSuccess(selectedProperty);
                return callback;
            }
        }).when(addNewPropertyPresenter).addNewProperty((AsyncCallback<Property>)anyObject());

        presenter.showProperties();
        presenter.onAddClicked();

        verify(addNewPropertyPresenter).addNewProperty((AsyncCallback<Property>)anyObject());
        verify(localization).addPropertyFailed(anyString());
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    @Test
    public void testOnEditClicked() {
        refreshPropertiesSuccess();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Property> callback = (AsyncCallback<Property>)arguments[1];
                callback.onSuccess(selectedProperty);
                return callback;
            }
        }).when(editPropertyPresenter).editProperty((Property)anyObject(), (AsyncCallback<Property>)anyObject());

        presenter.showProperties();
        presenter.selectedProperty(selectedProperty);
        presenter.onEditClicked();

        verify(editPropertyPresenter).editProperty((Property)anyObject(), (AsyncCallback<Property>)anyObject());
        verify(view).setSaveButtonEnabled(eq(ENABLE_BUTTON));
        verify(view, times(2)).setProperties((Array<Property>)anyObject());
    }

    @Test
    public void testSaveClick() {
        when(resourceProvider.getActiveProject().getProperties()).thenReturn(properties);

        refreshPropertiesSuccess();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[0];
                callback.onSuccess(project);
                return callback;
            }
        }).when(project).flushProjectProperties((AsyncCallback<Project>)anyObject());

        presenter.showProperties();
        presenter.onSaveClicked();

        verify(project).flushProjectProperties((AsyncCallback<Project>)anyObject());
        verify(view).close();
    }

    @Test
    public void testSaveWithFail() {
        when(resourceProvider.getActiveProject().getProperties()).thenReturn(properties);

        refreshPropertiesSuccess();

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[0];
                callback.onFailure(mock(Throwable.class));
                return callback;
            }
        }).when(project).flushProjectProperties((AsyncCallback<Project>)anyObject());

        presenter.showProperties();
        presenter.onSaveClicked();

        verify(project).flushProjectProperties((AsyncCallback<Project>)anyObject());
        verify(localization).saveProjectPropertiesFailed();
        verify(notificationManager).showNotification((Notification)anyObject());
    }

    protected void refreshPropertiesSuccess() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                AsyncCallback<Project> callback = (AsyncCallback<Project>)arguments[0];
                callback.onSuccess(project);
                return callback;
            }
        }).when(project).refreshProperties((AsyncCallback<Project>)anyObject());
    }
}
