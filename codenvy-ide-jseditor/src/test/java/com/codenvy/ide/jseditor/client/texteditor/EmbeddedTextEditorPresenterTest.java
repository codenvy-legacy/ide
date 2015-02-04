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
package com.codenvy.ide.jseditor.client.texteditor;

import com.codenvy.ide.api.editor.EditorInitException;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttree.VirtualFile;
import com.codenvy.ide.api.texteditor.TextEditorOperations;
import com.codenvy.ide.jseditor.client.JsEditorConstants;
import com.codenvy.ide.jseditor.client.document.DocumentStorage;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument;
import com.codenvy.ide.jseditor.client.editorconfig.TextEditorConfiguration;
import com.codenvy.ide.jseditor.client.formatter.ContentFormatter;
import com.codenvy.ide.jseditor.client.quickfix.QuickAssistantFactory;
import com.google.gwt.core.client.Scheduler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class EmbeddedTextEditorPresenterTest {

    @Mock
    private DocumentStorage documentStorage;
    @Mock
    private JsEditorConstants constant;
    @Mock
    private EditorWidgetFactory<EditorWidget> editorWidgetFactory;
    @Mock
    private EditorModule<EditorWidget> editorModule;
    @Mock
    private EmbeddedTextEditorPartView editorView;
    @Mock
    private EventBus eventBus;
    @Mock
    private QuickAssistantFactory quickAssistantFactory;
    @Mock
    private EditorInput editorInput;
    @Mock
    private EditorWidget editorWidget;
    @Mock
    private EmbeddedDocument document;
    @Mock
    private TextEditorConfiguration configuration;
    @Mock
    private NotificationManager notificationManager;

    @Mock
    private ContentFormatter contentFormatter;

    @InjectMocks
    private EmbeddedTextEditorPresenter<EditorWidget> embeddedTextEditorPresenter;

    @Test
    public void activateEditorIfEditorWidgetNotNullTest() throws EditorInitException {
        initializeAndInitEditor();
        embeddedTextEditorPresenter.activate();

        //verify(editorWidget).setFocus();
    }

    @Test
    public void activateEditorIfEditorWidgetNullTest() throws EditorInitException, NoSuchFieldException, IllegalAccessException {
        reset(editorView, eventBus);

        embeddedTextEditorPresenter.activate();

        Field delayedFocus = EmbeddedTextEditorPresenter.class.getDeclaredField("delayedFocus");
        delayedFocus.setAccessible(true);
        boolean fieldValue = (boolean) delayedFocus.get(embeddedTextEditorPresenter);

        assertTrue(fieldValue);
    }

    @Test
    public void doFormatOperationTest() throws EditorInitException {
        doReturn(contentFormatter).when(configuration).getContentFormatter();
        initializeAndInitEditor();

        embeddedTextEditorPresenter.doOperation(TextEditorOperations.FORMAT);

        verify(contentFormatter).format(document);
    }

    @Test
    public void doFormatOperationWhenDocumentAndFormatterAreNullTest() throws EditorInitException {
        embeddedTextEditorPresenter.initialize(configuration, notificationManager);
        embeddedTextEditorPresenter.doOperation(TextEditorOperations.FORMAT);

        verify(contentFormatter, never()).format(document);
    }

    @Test
    public void doFormatOperationWhenFormatterIsNotNullButDocumentIsNullTest() throws EditorInitException {
        doReturn(contentFormatter).when(configuration).getContentFormatter();

        embeddedTextEditorPresenter.initialize(configuration, notificationManager);
        embeddedTextEditorPresenter.doOperation(TextEditorOperations.FORMAT);

        verify(contentFormatter, never()).format(document);
    }

    @Test
    public void doFormatOperationWhenDocumentIsNotNullButFormatterIsNullTest() throws EditorInitException {
        //formatter is null
        doReturn(null).when(configuration).getContentFormatter();
        initializeAndInitEditor();

        embeddedTextEditorPresenter.doOperation(TextEditorOperations.FORMAT);

        verify(contentFormatter, never()).format(document);
    }

    public void initializeAndInitEditor() throws EditorInitException {
        reset(Scheduler.get());
        ArgumentCaptor<Scheduler.ScheduledCommand> commandCaptor =
                ArgumentCaptor.forClass(Scheduler.ScheduledCommand.class);

        ArgumentCaptor<EditorInitCallback> callBackCaptor =
                ArgumentCaptor.forClass(EditorInitCallback.class);

        doReturn(editorWidget).when(editorWidgetFactory).createEditorWidget(Matchers.<List<String>>anyObject());
        doReturn(document).when(editorWidget).getDocument();

        embeddedTextEditorPresenter.initialize(configuration, notificationManager);
        embeddedTextEditorPresenter.init(editorInput);

        verify(Scheduler.get()).scheduleDeferred(commandCaptor.capture());

        Scheduler.ScheduledCommand sheScheduledCommand = commandCaptor.getValue();
        sheScheduledCommand.execute();

        verify(documentStorage).getDocument(any(VirtualFile.class), callBackCaptor.capture());

        EditorInitCallback editorInitCallBack = callBackCaptor.getValue();
        editorInitCallBack.onReady("test");
    }

    @Test
    public void canDoOperationFormatTest() {
        assertTrue(embeddedTextEditorPresenter.canDoOperation(TextEditorOperations.CODEASSIST_PROPOSALS));
    }

    @Test
    public void canDoOperationCodeAssistantProposalTest() {
        assertTrue(embeddedTextEditorPresenter.canDoOperation(TextEditorOperations.FORMAT));
    }
}
