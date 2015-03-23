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
package org.eclipse.che.ide.ext.ssh.client.manage;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.java.JsonArrayListAdapter;
import org.eclipse.che.ide.commons.exception.ExceptionThrownEvent;
import org.eclipse.che.ide.ext.ssh.client.SshKeyService;
import org.eclipse.che.ide.ext.ssh.client.SshLocalizationConstant;
import org.eclipse.che.ide.ext.ssh.client.SshResources;
import org.eclipse.che.ide.ext.ssh.client.upload.UploadSshKeyPresenter;
import org.eclipse.che.ide.ext.ssh.dto.KeyItem;
import org.eclipse.che.ide.ext.ssh.dto.PublicKey;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.AsyncRequestLoader;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.ui.dialogs.CancelCallback;
import org.eclipse.che.ide.ui.dialogs.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialog;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialog;
import org.eclipse.che.test.GwtReflectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link SshKeyManagerPresenter} functionality.
 *
 * @author Roman Nikitenko
 */
@RunWith(GwtMockitoTestRunner.class)
public class SshKeyManagerPresenterTest {
    public static final String GITHUB_HOST = "github.com";

    @Captor
    private ArgumentCaptor<AsyncRequestCallback<PublicKey>> publicKeyCallbackCaptor;

    @Captor
    private ArgumentCaptor<AsyncRequestCallback<Void>> asyncRequestCallbackCaptor;

    @Captor
    private ArgumentCaptor<AsyncRequestCallback<Array<KeyItem>>> getAllKeysCallbackCaptor;

    @Captor
    private ArgumentCaptor<AsyncCallback<Void>> asyncCallbackCaptor;

    @Captor
    private ArgumentCaptor<ConfirmCallback> confirmCallbackCaptor;

    @Captor
    private ArgumentCaptor<CancelCallback> cancelCallbackCaptor;

    @Mock
    private AppContext              appContext;
    @Mock
    private DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    @Mock
    private DialogFactory           dialogFactory;
    @Mock
    private SshKeyManagerView       view;
    @Mock
    private SshKeyService           service;
    @Mock
    private SshLocalizationConstant constant;
    @Mock
    private SshResources            resources;
    @Mock
    private EventBus                eventBus;
    @Mock
    private AsyncRequestLoader      loader;
    @Mock
    private UploadSshKeyPresenter   uploadSshKeyPresenter;
    @Mock
    private NotificationManager     notificationManager;
    @InjectMocks
    private SshKeyManagerPresenter  presenter;

    @Test
    public void testGo() {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        presenter.go(container);

        verify(service).getAllKeys(Matchers.<AsyncRequestCallback<Array<KeyItem>>>anyObject());
        verify(container).setWidget(eq(view));
    }

    @Test
    public void testOnViewClickedWhenGetPublicKeyIsSuccess() {
        KeyItem keyItem = mock(KeyItem.class);
        PublicKey publicKey = mock(PublicKey.class);
        MessageDialog messageDialog = mock(MessageDialog.class);
        when(dialogFactory.createMessageDialog(anyString(), anyString(), (ConfirmCallback)anyObject())).thenReturn(messageDialog);

        presenter.onViewClicked(keyItem);

        verify(service).getPublicKey((KeyItem)anyObject(), publicKeyCallbackCaptor.capture());
        AsyncRequestCallback<PublicKey> publicKeyCallback = publicKeyCallbackCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(publicKeyCallback, publicKey);

        verify(loader).hide(anyString());
        verify(dialogFactory).createMessageDialog(anyString(), anyString(), (ConfirmCallback)anyObject());
        verify(messageDialog).show();
    }

    @Test
    public void testOnViewClickedWhenGetPublicKeyIsFailure() {
        KeyItem keyItem = mock(KeyItem.class);
        MessageDialog messageDialog = mock(MessageDialog.class);
        when(dialogFactory.createMessageDialog(anyString(), anyString(), (ConfirmCallback)anyObject())).thenReturn(messageDialog);

        presenter.onViewClicked(keyItem);

        verify(service).getPublicKey((KeyItem)anyObject(), publicKeyCallbackCaptor.capture());
        AsyncRequestCallback<PublicKey> publicKeyCallback = publicKeyCallbackCaptor.getValue();
        GwtReflectionUtils.callOnFailure(publicKeyCallback, new Exception(""));

        verify(loader).hide(anyString());
        verify(dialogFactory, never()).createMessageDialog(anyString(), anyString(), (ConfirmCallback)anyObject());
        verify(messageDialog, never()).show();
        verify(notificationManager).showError(anyString());
        verify(eventBus).fireEvent(Matchers.<ExceptionThrownEvent>anyObject());
    }

    @Test
    public void testOnDeleteClickedWhenDeleteKeyConfirmed() {
        KeyItem keyItem = mock(KeyItem.class);
        SafeHtml safeHtml = mock(SafeHtml.class);
        ConfirmDialog confirmDialog = mock(ConfirmDialog.class);
        when(keyItem.getHost()).thenReturn(GITHUB_HOST);
        when(constant.deleteSshKeyQuestion(anyString())).thenReturn(safeHtml);
        when(safeHtml.asString()).thenReturn("");
        when(dialogFactory.createConfirmDialog(anyString(), anyString(), (ConfirmCallback)anyObject(), (CancelCallback)anyObject()))
                .thenReturn(confirmDialog);

        presenter.onDeleteClicked(keyItem);

        verify(dialogFactory).createConfirmDialog(anyString(), anyString(), confirmCallbackCaptor.capture(), (CancelCallback)anyObject());
        ConfirmCallback confirmCallback = confirmCallbackCaptor.getValue();
        confirmCallback.accepted();

        verify(confirmDialog).show();
        verify(service).deleteKey((KeyItem)anyObject(), Matchers.<AsyncRequestCallback<Void>>anyObject());
    }

    @Test
    public void testOnDeleteClickedWhenDeleteKeyCanceled() {
        KeyItem keyItem = mock(KeyItem.class);
        SafeHtml safeHtml = mock(SafeHtml.class);
        ConfirmDialog confirmDialog = mock(ConfirmDialog.class);
        when(keyItem.getHost()).thenReturn(GITHUB_HOST);
        when(constant.deleteSshKeyQuestion(anyString())).thenReturn(safeHtml);
        when(safeHtml.asString()).thenReturn("");
        when(dialogFactory.createConfirmDialog(anyString(), anyString(), (ConfirmCallback)anyObject(), (CancelCallback)anyObject()))
                .thenReturn(confirmDialog);

        presenter.onDeleteClicked(keyItem);

        verify(dialogFactory).createConfirmDialog(anyString(), anyString(), (ConfirmCallback)anyObject(), cancelCallbackCaptor.capture());
        CancelCallback cancelCallback = cancelCallbackCaptor.getValue();
        cancelCallback.cancelled();

        verify(confirmDialog).show();
        verify(service, never()).deleteKey((KeyItem)anyObject(), Matchers.<AsyncRequestCallback<Void>>anyObject());
    }

    @Test
    public void testOnDeleteClickedWhenDeleteKeyIsSuccess() {
        KeyItem keyItem = mock(KeyItem.class);
        SafeHtml safeHtml = mock(SafeHtml.class);
        ConfirmDialog confirmDialog = mock(ConfirmDialog.class);
        when(keyItem.getHost()).thenReturn(GITHUB_HOST);
        when(constant.deleteSshKeyQuestion(anyString())).thenReturn(safeHtml);
        when(safeHtml.asString()).thenReturn("");
        when(dialogFactory.createConfirmDialog(anyString(), anyString(), (ConfirmCallback)anyObject(), (CancelCallback)anyObject()))
                .thenReturn(confirmDialog);

        presenter.onDeleteClicked(keyItem);

        verify(dialogFactory).createConfirmDialog(anyString(), anyString(), confirmCallbackCaptor.capture(), (CancelCallback)anyObject());
        ConfirmCallback confirmCallback = confirmCallbackCaptor.getValue();
        confirmCallback.accepted();

        verify(service).deleteKey((KeyItem)anyObject(), asyncRequestCallbackCaptor.capture());
        AsyncRequestCallback asyncRequestCallback = asyncRequestCallbackCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(asyncRequestCallback, (Void)null);

        verify(confirmDialog).show();
        verify(service).deleteKey((KeyItem)anyObject(), Matchers.<AsyncRequestCallback<Void>>anyObject());
        verify(loader).hide(anyString());
        verify(service).getAllKeys(Matchers.<AsyncRequestCallback<Array<KeyItem>>>anyObject());
    }

    @Test
    public void testOnDeleteClickedWhenDeleteKeyIsFailure() {
        KeyItem keyItem = mock(KeyItem.class);
        SafeHtml safeHtml = mock(SafeHtml.class);
        ConfirmDialog confirmDialog = mock(ConfirmDialog.class);
        when(keyItem.getHost()).thenReturn(GITHUB_HOST);
        when(constant.deleteSshKeyQuestion(anyString())).thenReturn(safeHtml);
        when(safeHtml.asString()).thenReturn("");
        when(dialogFactory.createConfirmDialog(anyString(), anyString(), (ConfirmCallback)anyObject(), (CancelCallback)anyObject()))
                .thenReturn(confirmDialog);

        presenter.onDeleteClicked(keyItem);

        verify(dialogFactory).createConfirmDialog(anyString(), anyString(), confirmCallbackCaptor.capture(), (CancelCallback)anyObject());
        ConfirmCallback confirmCallback = confirmCallbackCaptor.getValue();
        confirmCallback.accepted();

        verify(service).deleteKey((KeyItem)anyObject(), asyncRequestCallbackCaptor.capture());
        AsyncRequestCallback asyncRequestCallback = asyncRequestCallbackCaptor.getValue();
        GwtReflectionUtils.callOnFailure(asyncRequestCallback, new Exception(""));

        verify(confirmDialog).show();
        verify(service).deleteKey((KeyItem)anyObject(), Matchers.<AsyncRequestCallback<Void>>anyObject());
        verify(loader).hide(anyString());
        verify(notificationManager).showError(anyString());
        verify(service, never()).getAllKeys(Matchers.<AsyncRequestCallback<Array<KeyItem>>>anyObject());
    }

    @Test
    public void testShouldRefreshKeysAfterSuccessfulDeleteKey() {
        KeyItem keyItem = mock(KeyItem.class);
        SafeHtml safeHtml = mock(SafeHtml.class);
        ConfirmDialog confirmDialog = mock(ConfirmDialog.class);
        Array<KeyItem> keyItemArray = new JsonArrayListAdapter<KeyItem>(new ArrayList<KeyItem>());
        when(keyItem.getHost()).thenReturn(GITHUB_HOST);
        when(constant.deleteSshKeyQuestion(anyString())).thenReturn(safeHtml);
        when(safeHtml.asString()).thenReturn("");
        when(dialogFactory.createConfirmDialog(anyString(), anyString(), (ConfirmCallback)anyObject(), (CancelCallback)anyObject()))
                .thenReturn(confirmDialog);

        presenter.onDeleteClicked(keyItem);

        verify(dialogFactory).createConfirmDialog(anyString(), anyString(), confirmCallbackCaptor.capture(), (CancelCallback)anyObject());
        ConfirmCallback confirmCallback = confirmCallbackCaptor.getValue();
        confirmCallback.accepted();

        verify(service).deleteKey((KeyItem)anyObject(), asyncRequestCallbackCaptor.capture());
        AsyncRequestCallback asyncRequestCallback = asyncRequestCallbackCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(asyncRequestCallback, (Void)null);

        verify(service).getAllKeys(getAllKeysCallbackCaptor.capture());
        AsyncRequestCallback<Array<KeyItem>> getAllKeysCallback = getAllKeysCallbackCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(getAllKeysCallback, keyItemArray);

        verify(confirmDialog).show();
        verify(service).deleteKey((KeyItem)anyObject(), Matchers.<AsyncRequestCallback<Void>>anyObject());
        verify(loader, times(2)).hide(anyString());
        verify(service).getAllKeys(Matchers.<AsyncRequestCallback<Array<KeyItem>>>anyObject());
        verify(view).setKeys(eq(keyItemArray));
    }

    @Test
    public void testFailedRefreshKeysAfterSuccessfulDeleteKey() {
        KeyItem keyItem = mock(KeyItem.class);
        SafeHtml safeHtml = mock(SafeHtml.class);
        ConfirmDialog confirmDialog = mock(ConfirmDialog.class);
        Array<KeyItem> keyItemArray = new JsonArrayListAdapter<KeyItem>(new ArrayList<KeyItem>());
        when(keyItem.getHost()).thenReturn(GITHUB_HOST);
        when(constant.deleteSshKeyQuestion(anyString())).thenReturn(safeHtml);
        when(safeHtml.asString()).thenReturn("");
        when(dialogFactory.createConfirmDialog(anyString(), anyString(), (ConfirmCallback)anyObject(), (CancelCallback)anyObject()))
                .thenReturn(confirmDialog);

        presenter.onDeleteClicked(keyItem);

        verify(dialogFactory).createConfirmDialog(anyString(), anyString(), confirmCallbackCaptor.capture(), (CancelCallback)anyObject());
        ConfirmCallback confirmCallback = confirmCallbackCaptor.getValue();
        confirmCallback.accepted();

        verify(service).deleteKey((KeyItem)anyObject(), asyncRequestCallbackCaptor.capture());
        AsyncRequestCallback asyncRequestCallback = asyncRequestCallbackCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(asyncRequestCallback, (Void)null);

        verify(service).getAllKeys(getAllKeysCallbackCaptor.capture());
        AsyncRequestCallback<Array<KeyItem>> getAllKeysCallback = getAllKeysCallbackCaptor.getValue();
        GwtReflectionUtils.callOnFailure(getAllKeysCallback, new Exception(""));

        verify(confirmDialog).show();
        verify(service).deleteKey((KeyItem)anyObject(), Matchers.<AsyncRequestCallback<Void>>anyObject());
        verify(loader, times(2)).hide(anyString());
        verify(service).getAllKeys(Matchers.<AsyncRequestCallback<Array<KeyItem>>>anyObject());
        verify(view, never()).setKeys(eq(keyItemArray));
        verify(notificationManager).showError(anyString());
        verify(eventBus).fireEvent(Matchers.<ExceptionThrownEvent>anyObject());
    }

    @Test
    public void testShouldRefreshKeysAfterSuccessfulUploadKey() {
        Array<KeyItem> keyItemArray = new JsonArrayListAdapter<KeyItem>(new ArrayList<KeyItem>());

        presenter.onUploadClicked();

        verify(uploadSshKeyPresenter).showDialog(asyncCallbackCaptor.capture());
        AsyncCallback<Void> asyncCallback = asyncCallbackCaptor.getValue();
        asyncCallback.onSuccess(null);

        verify(service).getAllKeys(getAllKeysCallbackCaptor.capture());
        AsyncRequestCallback<Array<KeyItem>> getAllKeysCallback = getAllKeysCallbackCaptor.getValue();
        GwtReflectionUtils.callOnSuccess(getAllKeysCallback, keyItemArray);

        verify(loader).hide(anyString());
        verify(view).setKeys(eq(keyItemArray));
    }

    @Test
    public void testFailedRefreshKeysAfterSuccessfulUploadKey() {
        Array<KeyItem> keyItemArray = new JsonArrayListAdapter<KeyItem>(new ArrayList<KeyItem>());

        presenter.onUploadClicked();

        verify(uploadSshKeyPresenter).showDialog(asyncCallbackCaptor.capture());
        AsyncCallback<Void> asyncCallback = asyncCallbackCaptor.getValue();
        asyncCallback.onSuccess(null);

        verify(service).getAllKeys(getAllKeysCallbackCaptor.capture());
        AsyncRequestCallback<Array<KeyItem>> getAllKeysCallback = getAllKeysCallbackCaptor.getValue();
        GwtReflectionUtils.callOnFailure(getAllKeysCallback, new Exception(""));

        verify(loader).hide(anyString());
        verify(view, never()).setKeys(eq(keyItemArray));
        verify(eventBus).fireEvent(Matchers.<ExceptionThrownEvent>anyObject());
    }

}
