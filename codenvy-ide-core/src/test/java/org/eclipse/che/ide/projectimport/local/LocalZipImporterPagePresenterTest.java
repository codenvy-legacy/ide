/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.projectimport.local;

import com.google.gwt.user.client.ui.FormPanel;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ImportResponse;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.vfs.gwt.client.VfsServiceClient;
import org.eclipse.che.api.vfs.shared.dto.Item;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.event.OpenProjectEvent;
import org.eclipse.che.ide.api.project.wizard.ImportProjectNotificationSubscriber;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.ui.dialogs.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialog;
import org.eclipse.che.test.GwtReflectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link LocalZipImporterPagePresenter} functionality.
 *
 * @author Roman Nikitenko
 */
@RunWith(MockitoJUnitRunner.class)
public class LocalZipImporterPagePresenterTest {
    private static final String PROJECT_NAME    = "test";
    private static final String FILE_NAME       = "test.zip";
    private static final String RESPONSE        = "<pre style=\"word-wrap: break-word; white-space: pre-wrap;\">{\"projectDescriptor\":{}}</pre>";
    private static final String PARSED_RESPONSE = "{\"projectDescriptor\":{}}";

    @Captor
    private ArgumentCaptor<AsyncRequestCallback<Item>> callbackCaptorForItem;

    @Mock
    private ProjectServiceClient                projectServiceClient;
    @Mock
    private VfsServiceClient                    vfsServiceClient;
    @Mock
    private DtoFactory                          dtoFactory;
    @Mock
    private DialogFactory                       dialogFactory;
    @Mock
    private EventBus                            eventBus;
    @Mock
    private CoreLocalizationConstant            locale;
    @Mock
    private ImportProjectNotificationSubscriber importProjectNotificationSubscriber;
    @Mock
    private LocalZipImporterPageView            view;
    @InjectMocks
    private LocalZipImporterPagePresenter       presenter;

    @Test
    public void showDialogTest() {
        presenter.show();

        verify(view).setProjectName(eq(""));
        verify(view).setProjectDescription(eq(""));
        verify(view).setProjectVisibility(eq(true));
        verify(view).setSkipFirstLevel(eq(true));
        verify(view).showDialog();
    }

    @Test
    public void shouldCloseDialogTest() {
        presenter.onCancelClicked();

        verify(view).closeDialog();
    }

    @Test
    public void correctProjectNameEnteredWhenZipForUploadChoosedTest() {
        when(view.getProjectName()).thenReturn(PROJECT_NAME);
        when(view.getFileName()).thenReturn(FILE_NAME);

        presenter.projectNameChanged();

        verify(view).setEnabledImportButton(eq(true));
        verify(view).hideNameError();
        verify(view, never()).showNameError();
    }

    @Test
    public void incorrectFileForUploadChoosedTest() {
        String incorrectFileName = "test.txt"; //not zip
        when(view.getProjectName()).thenReturn(PROJECT_NAME);
        when(view.getFileName()).thenReturn(incorrectFileName);

        presenter.projectNameChanged();

        verify(view).setEnabledImportButton(eq(false));
        verify(view, never()).showNameError();
    }

    @Test
    public void emptyProjectNameEnteredTest() {
        String emptyName = "";
        when(view.getFileName()).thenReturn(FILE_NAME);
        when(view.getProjectName()).thenReturn(emptyName);

        presenter.projectNameChanged();

        verify(view).setEnabledImportButton(eq(false));
        verify(view).showNameError();
    }

    @Test
    public void incorrectProjectNameEnteredTest() {
        String incorrectName = "angularjs+";
        when(view.getFileName()).thenReturn(FILE_NAME);
        when(view.getProjectName()).thenReturn(incorrectName);

        presenter.projectNameChanged();

        verify(view).setEnabledImportButton(eq(false));
        verify(view).showNameError();
    }

    @Test
    public void fileNameChangedWhenCorrectFileForUploadChoosedTest() {
        when(view.getFileName()).thenReturn("fakepath\\test.zip");
        when(view.getProjectName()).thenReturn(PROJECT_NAME);

        presenter.fileNameChanged();

        verify(view).setProjectName(eq(PROJECT_NAME));
        verify(view).setEnabledImportButton(eq(true));
        verify(view).hideNameError();
        verify(view, never()).showNameError();
    }

    @Test
    public void fileNameChangedWhenIncorrectFileForUploadChoosedTest() {
        String parsedProjectName = "";
        String incorrectFileName = "fakepath\\angularjs.txt"; //not zip
        when(view.getFileName()).thenReturn(incorrectFileName);
        when(view.getProjectName()).thenReturn(parsedProjectName);

        presenter.fileNameChanged();

        verify(view, never()).setProjectName(eq(parsedProjectName));
        verify(view, never()).setEnabledImportButton(anyBoolean());
    }

    @Test
    public void extractFromHtmlFormatSubmitResultTest() {
        presenter.onSubmitComplete(RESPONSE);

        verify(dtoFactory).createDtoFromJson(PARSED_RESPONSE, ImportResponse.class);
    }

    @Test
    public void submitCompleteWhenImportIsSuccessTest() {
        reset(view);
        ImportResponse importResponse = mock(ImportResponse.class);
        ProjectDescriptor projectDescriptor = mock(ProjectDescriptor.class);
        when(dtoFactory.createDtoFromJson(anyString(), Matchers.<Class<ImportResponse>>anyObject())).thenReturn(importResponse);
        when(importResponse.getProjectDescriptor()).thenReturn(projectDescriptor);

        presenter.onSubmitComplete(RESPONSE);

        verify(view).setLoaderVisibility(eq(false));
        verify(view).setInputsEnableState(eq(true));
        verify(dtoFactory).createDtoFromJson(PARSED_RESPONSE, ImportResponse.class);
        verify(view).closeDialog();
        verify(importProjectNotificationSubscriber).onSuccess();
        verify(eventBus).fireEvent(Matchers.<Event<OpenProjectEvent>>anyObject());
        verify(importProjectNotificationSubscriber, never()).onFailure(anyString());
    }

    @Test
    public void submitCompleteWhenImportIsFailureTest() {
        reset(view);
        String response = "ERROR";
        when(view.getProjectName()).thenReturn(PROJECT_NAME);

        presenter.onSubmitComplete(response);

        verify(view).setLoaderVisibility(eq(false));
        verify(view).setInputsEnableState(eq(true));
        verify(dtoFactory).createDtoFromJson(response, ImportResponse.class);
        verify(view).closeDialog();
        verify(importProjectNotificationSubscriber, never()).onSuccess();
        verify(importProjectNotificationSubscriber).onFailure(anyString());
        verify(projectServiceClient).delete(eq(PROJECT_NAME), Matchers.<AsyncRequestCallback<Void>>anyObject());
    }

    @Test
    public void onImportClickedWhenProjectWithSameNameAlreadyExistsTest() {
        when(view.getProjectName()).thenReturn(PROJECT_NAME);
        MessageDialog dialog = mock(MessageDialog.class);
        when(dialogFactory.createMessageDialog(anyString(), anyString(), (ConfirmCallback)anyObject())).thenReturn(dialog);

        presenter.onImportClicked();

        verify(vfsServiceClient).getItemByPath(eq(PROJECT_NAME), callbackCaptorForItem.capture());
        AsyncRequestCallback<Item> callback = callbackCaptorForItem.getValue();
        GwtReflectionUtils.callOnSuccess(callback, mock(Item.class));

        verify(view).setEnabledImportButton(eq(false));
        verify(dialogFactory).createMessageDialog(anyString(), anyString(), (ConfirmCallback)anyObject());
        verify(dialog).show();
        verify(view, never()).submit();
        verify(view, never()).setLoaderVisibility(anyBoolean());
        verify(view, never()).setInputsEnableState(anyBoolean());
    }

    @Test
    public void onImportClickedWhenShouldImportAndOpenProjectTest() {
        when(view.getProjectName()).thenReturn(PROJECT_NAME);
        MessageDialog dialog = mock(MessageDialog.class);
        when(dialogFactory.createMessageDialog(anyString(), anyString(), (ConfirmCallback)anyObject())).thenReturn(dialog);

        presenter.onImportClicked();

        verify(vfsServiceClient).getItemByPath(eq(PROJECT_NAME), callbackCaptorForItem.capture());
        AsyncRequestCallback<Item> itemCallback = callbackCaptorForItem.getValue();
        GwtReflectionUtils.callOnFailure(itemCallback, mock(Throwable.class));

        verify(dialogFactory, never()).createMessageDialog(anyString(), anyString(), (ConfirmCallback)anyObject());
        verify(dialog, never()).show();
        verify(importProjectNotificationSubscriber).subscribe(eq(PROJECT_NAME));
        verify(view).setEncoding(eq(FormPanel.ENCODING_MULTIPART));
        verify(view).setAction(anyString());
        verify(view).submit();
        verify(view).setLoaderVisibility(eq(true));
        verify(view).setInputsEnableState(eq(false));
    }

}
