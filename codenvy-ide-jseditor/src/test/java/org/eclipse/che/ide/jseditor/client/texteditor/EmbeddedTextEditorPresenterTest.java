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
package org.eclipse.che.ide.jseditor.client.texteditor;

import org.eclipse.che.ide.api.editor.EditorInitException;
import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.project.tree.VirtualFile;
import org.eclipse.che.ide.api.texteditor.TextEditorOperations;
import org.eclipse.che.ide.collections.StringMap;
import org.eclipse.che.ide.jseditor.client.JsEditorConstants;
import org.eclipse.che.ide.jseditor.client.codeassist.CodeAssistProcessor;
import org.eclipse.che.ide.jseditor.client.document.DocumentStorage;
import org.eclipse.che.ide.jseditor.client.document.EmbeddedDocument;
import org.eclipse.che.ide.jseditor.client.editorconfig.TextEditorConfiguration;
import org.eclipse.che.ide.jseditor.client.formatter.ContentFormatter;
import org.eclipse.che.ide.jseditor.client.quickfix.QuickAssistantFactory;
import com.google.gwt.core.client.Scheduler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.web.bindery.event.shared.EventBus;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * @author Andrienko Alexander
 */
@RunWith(GwtMockitoTestRunner.class)
public class EmbeddedTextEditorPresenterTest {

    @Mock
    private DocumentStorage                   documentStorage;
    @Mock
    private JsEditorConstants                 constant;
    @Mock
    private EditorWidgetFactory<EditorWidget> editorWidgetFactory;
    @Mock
    private EditorModule<EditorWidget>        editorModule;
    @Mock
    private EmbeddedTextEditorPartView        editorView;
    @Mock
    private EventBus                          eventBus;
    @Mock
    private QuickAssistantFactory             quickAssistantFactory;
    @Mock
    private EditorInput                       editorInput;
    @Mock
    private EditorWidget                      editorWidget;
    @Mock
    private EmbeddedDocument                  document;
    @Mock
    private TextEditorConfiguration           configuration;
    @Mock
    private NotificationManager               notificationManager;

    @Mock
    private ContentFormatter               contentFormatter;
    @Mock
    private StringMap<CodeAssistProcessor> codeAssistProcessors;

    @InjectMocks
    private EmbeddedTextEditorPresenter<EditorWidget> embeddedTextEditorPresenter;


    /*
     * Todo operations editorWidget.setFocus() and editorWidget.refresh() temporary situated inside Timer.
     * That's why we ignore this test.
     */
    @Ignore
    @Test
    public void activateEditorIfEditorWidgetNotNull() throws EditorInitException {
        initializeAndInitEditor();
        embeddedTextEditorPresenter.activate();

        verify(editorWidget).refresh();
        verify(editorWidget).setFocus();
    }

    @Test
    public void activateEditorIfEditorWidgetNull() throws Exception {
        reset(editorView, eventBus);

        embeddedTextEditorPresenter.activate();

        Field delayedFocus = EmbeddedTextEditorPresenter.class.getDeclaredField("delayedFocus");
        delayedFocus.setAccessible(true);
        boolean fieldValue = (boolean)delayedFocus.get(embeddedTextEditorPresenter);

        assertTrue(fieldValue);
    }

    @Test
    public void shouldFormatOperation() throws EditorInitException {
        doReturn(contentFormatter).when(configuration).getContentFormatter();
        initializeAndInitEditor();

        embeddedTextEditorPresenter.doOperation(TextEditorOperations.FORMAT);

        verify(contentFormatter).format(document);
    }

    @Test
    public void shouldFormatOperationWhenDocumentAndFormatterAreNull() throws EditorInitException {
        embeddedTextEditorPresenter.initialize(configuration, notificationManager);
        embeddedTextEditorPresenter.doOperation(TextEditorOperations.FORMAT);

        verify(contentFormatter, never()).format(document);
    }

    @Test
    public void shouldFormatOperationWhenFormatterIsNotNullButDocumentIsNull() throws EditorInitException {
        doReturn(contentFormatter).when(configuration).getContentFormatter();

        embeddedTextEditorPresenter.initialize(configuration, notificationManager);
        embeddedTextEditorPresenter.doOperation(TextEditorOperations.FORMAT);

        verify(contentFormatter, never()).format(document);
    }

    @Test
    public void shouldFormatOperationWhenDocumentIsNotNullButFormatterIsNull() throws EditorInitException {
        doReturn(null).when(configuration).getContentFormatter();
        initializeAndInitEditor();

        embeddedTextEditorPresenter.doOperation(TextEditorOperations.FORMAT);

        verify(contentFormatter, never()).format(document);
    }

    @Test
    public void shouldCanDoOperationCodeAssistProposal() throws EditorInitException {
        doReturn(codeAssistProcessors).when(configuration).getContentAssistantProcessors();
        doReturn(false).when(codeAssistProcessors).isEmpty();
        initializeAndInitEditor();

        assertTrue(embeddedTextEditorPresenter.canDoOperation(TextEditorOperations.CODEASSIST_PROPOSALS));
    }

    @Test
    public void shouldNOtCanDoOperationCodeAssistProposalBecauseProcessorsDontExistInMap() throws EditorInitException {
        doReturn(codeAssistProcessors).when(configuration).getContentAssistantProcessors();
        doReturn(true).when(codeAssistProcessors).isEmpty();
        initializeAndInitEditor();

        assertFalse(embeddedTextEditorPresenter.canDoOperation(TextEditorOperations.CODEASSIST_PROPOSALS));
    }

    @Test
    public void shouldNOtCanDoOperationCodeAssistProposalBecauseMapOfProcessorsIsNull() throws EditorInitException {
        doReturn(null).when(configuration).getContentAssistantProcessors();
        initializeAndInitEditor();

        assertFalse(embeddedTextEditorPresenter.canDoOperation(TextEditorOperations.CODEASSIST_PROPOSALS));
    }

    @Test
    public void shouldCanDoOperationFormat() throws EditorInitException {
        doReturn(contentFormatter).when(configuration).getContentFormatter();
        initializeAndInitEditor();

        assertTrue(embeddedTextEditorPresenter.canDoOperation(TextEditorOperations.FORMAT));
    }

    @Test
    public void shouldNotCanDoOperationFormat() throws EditorInitException {
        doReturn(null).when(configuration).getContentFormatter();
        initializeAndInitEditor();

        assertFalse(embeddedTextEditorPresenter.canDoOperation(TextEditorOperations.FORMAT));
    }

    /**
     * This method initialize EmbeddedTextEditorPresenter for testing
     * @throws EditorInitException
     */
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
}
