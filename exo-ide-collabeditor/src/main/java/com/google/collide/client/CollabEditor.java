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
package com.google.collide.client;

import com.codenvy.ide.client.util.PathUtil;
import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.commons.shared.StringUtils;
import com.codenvy.ide.commons.shared.TextUtils;
import com.codenvy.ide.json.shared.JsonArray;
import com.google.collide.client.code.EditableContentArea;
import com.google.collide.client.code.EditorBundle;
import com.google.collide.client.code.errorrenderer.EditorErrorListener;
import com.google.collide.client.code.popup.EditorPopupController.PopupRenderer;
import com.google.collide.client.code.popup.EditorPopupController.Remover;
import com.google.collide.client.collaboration.CollaborationPropertiesUtil;
import com.google.collide.client.document.DocumentManager;
import com.google.collide.client.document.DocumentMetadata;
import com.google.collide.client.documentparser.DocumentParser;
import com.google.collide.client.documentparser.ExternalParser;
import com.google.collide.client.editor.Buffer.ContextMenuListener;
import com.google.collide.client.editor.EditorDocumentMutator;
import com.google.collide.client.editor.FocusManager.FocusListener;
import com.google.collide.client.editor.folding.FoldMarker;
import com.google.collide.client.editor.gutter.NotificationManager;
import com.google.collide.client.editor.search.SearchModel.SearchProgressListener;
import com.google.collide.client.editor.selection.SelectionModel;
import com.google.collide.client.editor.selection.SelectionModel.CursorListener;
import com.google.collide.client.hover.HoverPresenter;
import com.google.collide.client.ui.menu.PositionController.VerticalAlign;
import com.google.collide.dto.FileContents;
import com.google.collide.shared.document.Document;
import com.google.collide.shared.document.Document.TextListener;
import com.google.collide.shared.document.Line;
import com.google.collide.shared.document.LineFinder;
import com.google.collide.shared.document.LineInfo;
import com.google.collide.shared.document.Position;
import com.google.collide.shared.document.TextChange;
import com.google.collide.shared.document.util.LineUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.EditorCapability;
import org.exoplatform.ide.editor.client.api.FileContentLoader;
import org.exoplatform.ide.editor.client.api.SelectionRange;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistant;
import org.exoplatform.ide.editor.client.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.client.api.event.EditorContentChangedHandler;
import org.exoplatform.ide.editor.client.api.event.EditorContextMenuEvent;
import org.exoplatform.ide.editor.client.api.event.EditorContextMenuHandler;
import org.exoplatform.ide.editor.client.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.client.api.event.EditorCursorActivityHandler;
import org.exoplatform.ide.editor.client.api.event.EditorFocusReceivedEvent;
import org.exoplatform.ide.editor.client.api.event.EditorFocusReceivedHandler;
import org.exoplatform.ide.editor.client.api.event.EditorHotKeyPressedEvent;
import org.exoplatform.ide.editor.client.api.event.EditorHotKeyPressedHandler;
import org.exoplatform.ide.editor.client.api.event.EditorInitializedEvent;
import org.exoplatform.ide.editor.client.api.event.EditorInitializedHandler;
import org.exoplatform.ide.editor.client.api.event.SearchCompleteCallback;
import org.exoplatform.ide.editor.client.marking.EditorLineNumberContextMenuEvent;
import org.exoplatform.ide.editor.client.marking.EditorLineNumberContextMenuHandler;
import org.exoplatform.ide.editor.client.marking.EditorLineNumberDoubleClickHandler;
import org.exoplatform.ide.editor.client.marking.Markable;
import org.exoplatform.ide.editor.client.marking.Marker;
import org.exoplatform.ide.editor.client.marking.ProblemClickHandler;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IRegion;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CollabEditor extends Widget implements Editor, Markable, RequiresResize {

    protected final EditorBundle                            editorBundle;
    protected final com.google.collide.client.editor.Editor editor;
    protected       IDocument                               document;
    protected       NotificationManager                     notificationManager;
    protected       DocumentAdaptor                         documentAdaptor;
    protected       ExternalParser                          extParser;
    private         String                                  mimeType;
    private         String                                  id;
    private         HoverPresenter                          hoverPresenter;
    private         boolean                                 initialized;
    private         ContentAssistant                        contentAssistant;
    private         String                                  searchQuery;
    private         boolean                                 caseSensitive;

    public CollabEditor(String mimeType) {
        this.mimeType = mimeType;

        id = "CollabEditor - " + hashCode();
        editorBundle =
                EditorBundle.create(CollabEditorExtension.get().getContext(), CollabEditorExtension.get().getManager(),
                                    EditorErrorListener.NOOP_ERROR_RECEIVER, this);
        contentAssistant = editorBundle.getAutocompleter().getContentAssistant();
        editor = editorBundle.getEditor();
        EditableContentArea.View v =
                new EditableContentArea.View(CollabEditorExtension.get().getContext().getResources());
        EditableContentArea contentArea =
                EditableContentArea.create(v, CollabEditorExtension.get().getContext(), editorBundle);
        contentArea.setContent(editorBundle);
        notificationManager = editor.getLeftGutterNotificationManager();
        notificationManager.setErrorListener(editorBundle.getErrorListener());
        setElement((Element)v.getElement());
        documentAdaptor = new DocumentAdaptor();
        editor.getFocusManager().getFocusListenerRegistrar().add(new FocusListener() {

            @Override
            public void onFocusChange(boolean hasFocus) {
                if (hasFocus)
                    fireEvent(new EditorFocusReceivedEvent(CollabEditor.this));
            }
        });

    }

    /**
     * Updates the specified document range with the given <code>text</code>.
     *
     * @param offset
     *         the document offset
     * @param length
     *         the length of the specified range
     * @param text
     *         the substitution text
     * @throws BadLocationException
     *         if the offset is invalid in this document
     */
    private void updateDocument(int offset, int length, String text) throws BadLocationException {
        document.removeDocumentListener(documentAdaptor);
        document.replace(offset, length, text);
        document.addDocumentListener(documentAdaptor);
    }

    /** @see com.google.gwt.user.client.ui.Widget#onLoad() */
    @Override
    protected void onLoad() {
        fireEvent(new EditorInitializedEvent(this));
        super.onLoad();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getMimeType() */
    @Override
    public String getMimeType() {
        return mimeType;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getId() */
    @Override
    public String getId() {
        return id;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getText() */
    @Override
    public String getText() {
        return editor.getDocument().asText();
    }

    public void setText(final String text) {
        document = new org.exoplatform.ide.editor.shared.text.Document(text);
        hoverPresenter = new HoverPresenter(this, editor, document);
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                initialized = true;
                Document editorDocument = Document.createFromString(text);
                editorDocument.putTag("IDocument", document);
                TextListenerImpl textListener = new TextListenerImpl();
                editorDocument.getTextListenerRegistrar().add(textListener);
                CollabEditorExtension.get().getManager().addDocument(editorDocument);
                editorBundle.setDocument(editorDocument, mimeType, "");
                documentAdaptor.setDocument(editorDocument, editor.getEditorDocumentMutator(), textListener,
                                            CollabEditor.this);

                // IMPORTANT!
                // Add 'documentAdaptor' as listener for the 'CollabEditor.this.document' there, because
                // 'ProjectionDocument' must be the first listener for the 'CollabEditor.this.document'.
                // 'ProjectionDocument' added as listener for the 'CollabEditor.this.document' in 'Editor.setDocument(Document)'.
                CollabEditor.this.document.addDocumentListener(documentAdaptor);

                editor.getSelection().getCursorListenerRegistrar().add(new CursorListener() {
                    @Override
                    public void onCursorChange(LineInfo lineInfo, int column, boolean isExplicitChange) {
                        fireEvent(new EditorCursorActivityEvent(CollabEditor.this, lineInfo.number() + 1, column + 1));
                    }
                });
                editor.getBuffer().getContenxtMenuListenerRegistrar().add(new ContextMenuListener() {
                    @Override
                    public void onContextMenu(int x, int y) {
                        fireEvent(new EditorContextMenuEvent(CollabEditor.this, x, y));
                    }
                });
            }
        });
    }

    @Override
    public void setFile(final FileModel file) {
        if (CollaborationPropertiesUtil.isCollaborationEnabled(file.getProject()) && !file.getMimeType().equals(MimeType.TEXT_HTML)) {

            PathUtil pathUtil = new PathUtil(file.getPath());
            pathUtil.setWorkspaceId(VirtualFileSystem.getInstance().getInfo().getId());
            CollabEditorExtension.get().getManager().getDocument(pathUtil, new DocumentManager.GetDocumentCallback() {
                @Override
                public void onDocumentReceived(Document document) {
                    setDocument(document);
                }

                @Override
                public void onUneditableFileContentsReceived(FileContents contents) {
                    Log.error(CollabEditor.class, "UnEditable File received " + contents.getPath());
                }

                @Override
                public void onFileNotFoundReceived() {
                    Log.error(CollabEditor.class, "File not found " + file.getPath());
                }
            });
        } else {

            FileContentLoader.getFileContent(file, new FileContentLoader.ContentCallback() {
                @Override
                public void onContentReceived(String content) {
                    setText(content);
                }
            });

        }
    }

    private void checkPermission(FileModel file) {
        Set<String> permissions = file.getProject().getPermissions();
        if (permissions != null) {
            setReadOnly(!(permissions.contains("write") || permissions.contains("all")));
        }
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getDocument() */
    @Override
    public IDocument getDocument() {
        return document;
    }

    public void setDocument(final Document document) {
        this.document = new org.exoplatform.ide.editor.shared.text.Document(document.asText());
        hoverPresenter = new HoverPresenter(this, editor, this.document);
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                initialized = true;
                document.putTag("IDocument", CollabEditor.this.document);
                TextListenerImpl textListener = new TextListenerImpl();
                document.getTextListenerRegistrar().add(textListener);
                editorBundle.setDocument(document, mimeType, DocumentMetadata.getFileEditSessionKey(document));
                documentAdaptor.setDocument(document, editor.getEditorDocumentMutator(), textListener, CollabEditor.this);

                // IMPORTANT!
                // Add 'documentAdaptor' as listener for the 'CollabEditor.this.document' there, because
                // 'ProjectionDocument' must be the first listener for the 'CollabEditor.this.document'.
                // 'ProjectionDocument' added as listener for the 'CollabEditor.this.document' in 'Editor.setDocument(Document)'.
                CollabEditor.this.document.addDocumentListener(documentAdaptor);

                editor.getSelection().getCursorListenerRegistrar().add(new CursorListener() {
                    @Override
                    public void onCursorChange(LineInfo lineInfo, int column, boolean isExplicitChange) {
                        fireEvent(new EditorCursorActivityEvent(CollabEditor.this, lineInfo.number() + 1, column + 1));
                    }
                });
                editor.getBuffer().getContenxtMenuListenerRegistrar().add(new ContextMenuListener() {
                    @Override
                    public void onContextMenu(int x, int y) {
                        fireEvent(new EditorContextMenuEvent(CollabEditor.this, x, y));
                    }
                });
            }
        });
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#isCapable(org.exoplatform.ide.editor.client.api.EditorCapability) */
    @Override
    public boolean isCapable(EditorCapability capability) {
        switch (capability) {
            case AUTOCOMPLETION:
            case OUTLINE:
            case VALIDATION:
            case FIND_AND_REPLACE:
            case DELETE_LINES:
            case FORMAT_SOURCE:
            case SET_CURSOR_POSITION:
            case COMMENT_SOURCE:
                return true;

            default:
                return false;
        }
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#formatSource() */
    @Override
    public void formatSource() {
        LineFinder lineFinder = editor.getDocument().getLineFinder();
        DocumentParser parser = getEditorBundle().getParser();
        EditorDocumentMutator editorDocumentMutator = editor.getEditorDocumentMutator();
        LineInfo findLine = lineFinder.findLine(0);
        Line line = null;
        do {
            line = findLine.line();
            int indentation = parser.getIndentation(line);
            if (indentation < 0) {
                continue;
            }
            int oldIndentation = TextUtils.countWhitespacesAtTheBeginningOfLine(line.getText());
            if (indentation == oldIndentation) {
                continue;
            }
            if (indentation < oldIndentation) {
                editorDocumentMutator.deleteText(line, 0, oldIndentation - indentation);
            } else {
                String addend = StringUtils.getSpaces(indentation - oldIndentation);
                editorDocumentMutator.insertText(line, 0, addend);
            }
        }
        while (findLine.moveToNext());
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#showLineNumbers(boolean) */
    @Override
    public void showLineNumbers(boolean showLineNumbers) {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#setFocus() */
    @Override
    public void setFocus() {
        editor.getFocusManager().focus();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#setCursorPosition(int, int) */
    @Override
    public void setCursorPosition(final int row, final int column) {
        if (initialized) {
            LineInfo lineInfo = editor.getDocument().getLineFinder().findLine(row - 1);
            editor.getSelection().setCursorPosition(lineInfo, column - 1);
        } else
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    setCursorPosition(row, column);
                }
            });
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#deleteCurrentLine() */
    @Override
    public void deleteCurrentLine() {
        SelectionModel selection = editor.getSelection();
        final int selectionBeginLineNumber = selection.getSelectionBeginLineNumber();
        Line selectionBeginLine = editor.getDocument().getLineFinder().findLine(selectionBeginLineNumber).line();
        final int selectionEndLineNumber = selection.getSelectionEndLineNumber();
        Line selectionEndLine = editor.getDocument().getLineFinder().findLine(selectionEndLineNumber).line();

        FoldMarker foldMarker = editor.getFoldingManager().getFoldMarkerOfLine(selectionEndLineNumber, false);
        if (foldMarker != null && foldMarker.isCollapsed()) {
            editor.getFoldingManager().expand(foldMarker);
        }

        final int deleteCount =
                LineUtils.getTextCount(selectionBeginLine, 0, selectionEndLine, selectionEndLine.getText().length() - 1);
        editor.getEditorDocumentMutator().deleteText(selectionBeginLine, 0, deleteCount);
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#hasUndoChanges() */
    @Override
    public boolean hasUndoChanges() {
        return true;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#undo() */
    @Override
    public void undo() {
        editor.undo();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#hasRedoChanges() */
    @Override
    public boolean hasRedoChanges() {
        return true;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#redo() */
    @Override
    public void redo() {
        editor.redo();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#isReadOnly() */
    @Override
    public boolean isReadOnly() {
        return editorBundle.isReadOnly();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#setReadOnly(boolean) */
    @Override
    public void setReadOnly(boolean readOnly) {
        editorBundle.setReadOnly(readOnly);
        editor.setReadOnly(readOnly);
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getCursorRow() */
    @Override
    public int getCursorRow() {
        return editor.getSelection().getCursorLineNumber() + 1;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getCursorColumn() */
    @Override
    public int getCursorColumn() {
        return editor.getSelection().getCursorColumn() + 1;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#replaceTextAtCurrentLine(java.lang.String, int) */
    @Override
    public void replaceTextAtCurrentLine(String line, int cursorPosition) {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getLineText(int) */
    @Override
    public String getLineText(int line) {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#setLineText(int, java.lang.String) */
    @Override
    public void setLineText(int line, String text) {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getNumberOfLines() */
    @Override
    public int getNumberOfLines() {
        return editor.getDocument().getLastLineNumber();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getSelectionRange() */
    @Override
    public SelectionRange getSelectionRange() {
        SelectionModel selection = editor.getSelection();
        if (selection == null) {
            return null;
        }
        return new SelectionRange(selection.getBaseLineNumber() + 1, selection.getBaseColumn(),
                                  selection.getCursorLineNumber() + 1, selection.getCursorColumn());
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#selectRange(int, int, int, int) */
    @Override
    public void selectRange(int startLine, int startChar, int endLine, int endChar) {
        LineFinder lineFinder = editor.getDocument().getLineFinder();
        LineInfo baseLineInfo = lineFinder.findLine(startLine - 1);
        LineInfo cursorLineInfo = lineFinder.findLine(endLine - 1);
        editor.getSelection().setSelection(baseLineInfo, startChar, cursorLineInfo, endChar);
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#selectAll() */
    @Override
    public void selectAll() {
        editor.getSelection().selectAll();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#cut() */
    @Override
    public void cut() {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#copy() */
    @Override
    public void copy() {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#paste() */
    @Override
    public void paste() {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#delete() */
    @Override
    public void delete() {
        editor.getSelection().deleteSelection(editor.getEditorDocumentMutator());
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#collapse() */
    @Override
    public void collapse() {
        final int cursorLineNumber = editor.getSelection().getCursorLineNumber();
        FoldMarker foldMarker = editor.getFoldingManager().getFoldMarkerOfLine(cursorLineNumber, false);
        if (foldMarker != null) {
            editor.getFoldingManager().collapse(foldMarker);
        }
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#expand() */
    @Override
    public void expand() {
        final int cursorLineNumber = editor.getSelection().getCursorLineNumber();
        FoldMarker foldMarker = editor.getFoldingManager().getFoldMarkerOfLine(cursorLineNumber, false);
        if (foldMarker != null) {
            editor.getFoldingManager().expand(foldMarker);
        }
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#collapseAll() */
    @Override
    public void collapseAll() {
        editor.getFoldingManager().collapseAll();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#expandAll() */
    @Override
    public void expandAll() {
        editor.getFoldingManager().expandAll();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#foldSelection() */
    @Override
    public void foldSelection() {
        SelectionModel selection = editor.getSelection();
        // for now available only folding of entire lines
        if (!selection.hasMultilineSelection()) {
            return;
        }

        int selectionBeginLineNumber = selection.getSelectionBeginLineNumber();
        int selectionEndLineNumber = selection.getSelectionEndLineNumber();
        try {
            int offset = document.getLineOffset(selectionBeginLineNumber);
            int length = document.getLineOffset(selectionEndLineNumber) + document.getLineLength(selectionEndLineNumber) - offset;
            editor.getFoldingManager().foldCustomRegion(offset, length);
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getName() */
    @Override
    public String getName() {
        return "Source";
    }

    /** @see org.exoplatform.ide.editor.client.marking.Markable#markProblem(org.exoplatform.ide.editor.client.marking.Marker) */
    @Override
    public void markProblem(Marker problem) {
        notificationManager.addProblem(problem);
    }

    /** @see org.exoplatform.ide.editor.client.marking.Markable#unmarkProblem(org.exoplatform.ide.editor.client.marking.Marker) */
    @Override
    public void unmarkProblem(Marker problem) {
        notificationManager.unmarkProblem(problem);
    }

    /** @see org.exoplatform.ide.editor.client.marking.Markable#unmarkAllProblems() */
    @Override
    public void unmarkAllProblems() {
        notificationManager.clear();
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addProblemClickHandler(ProblemClickHandler handler) {
        return editor.getLeftGutterNotificationManager().addProblemClickHandler(handler);
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addLineNumberDoubleClickHandler(EditorLineNumberDoubleClickHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addLineNumberContextMenuHandler(EditorLineNumberContextMenuHandler handler) {
        return addHandler(handler, EditorLineNumberContextMenuEvent.TYPE);
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getCursorOffsetLeft() */
    @Override
    public int getCursorOffsetLeft() {
        int scrollLeft = editor.getBuffer().getScrollLeft();
        Position position = editor.getSelection().getCursorPosition();
        final int foldingGutterWidth = editor.getFoldingGutter() == null ? 0 : editor.getFoldingGutter().getWidth();
        int offsetLeft =
                getElement().getAbsoluteLeft() + editor.getLeftGutter().getWidth() + foldingGutterWidth
                + editor.getLeftGutterNotificationManager().getLeftGutter().getWidth()
                + editor.getBuffer().convertColumnToX(position.getLine(), position.getColumn());

        return offsetLeft - scrollLeft + 2;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getCursorOffsetTop() */
    @Override
    public int getCursorOffsetTop() {
        int scrollTop = editor.getBuffer().getScrollTop();
        Position position = editor.getSelection().getCursorPosition();
        int offsetTop = getElement().getAbsoluteTop() + editor.getBuffer().convertLineNumberToY(position.getLineNumber());
        return offsetTop - scrollTop + 1;
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addContentChangedHandler(EditorContentChangedHandler handler) {
        return addHandler(handler, EditorContentChangedEvent.TYPE);
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addContextMenuHandler(EditorContextMenuHandler handler) {
        return addHandler(handler, EditorContextMenuEvent.TYPE);
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addCursorActivityHandler(EditorCursorActivityHandler handler) {
        return addHandler(handler, EditorCursorActivityEvent.TYPE);
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addFocusReceivedHandler(EditorFocusReceivedHandler handler) {
        return addHandler(handler, EditorFocusReceivedEvent.TYPE);
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addHotKeyPressedHandler(EditorHotKeyPressedHandler handler) {
        return addHandler(handler, EditorHotKeyPressedEvent.TYPE);
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addInitializedHandler(EditorInitializedHandler handler) {
        return addHandler(handler, EditorInitializedEvent.TYPE);
    }

    /** @see org.exoplatform.ide.editor.client.marking.Markable#addProblems(org.exoplatform.ide.editor.client.marking.Marker[]) */
    @Override
    public void addProblems(Marker[] problems) {
        notificationManager.addProblems(problems);
    }

    /** @return the hoverPresenter */
    public HoverPresenter getHoverPresenter() {
        return hoverPresenter;
    }

    /** @return the editorBundle */
    public EditorBundle getEditorBundle() {
        return editorBundle;
    }

    /** @return  */
    public ContentAssistant getCodeassistant() {
        return contentAssistant;
    }

    /** {@inheritDoc} */
    public void search(String query, boolean caseSensitive, final SearchCompleteCallback searchCompleteCallback) {
        if (searchCompleteCallback == null) {
            return;
        }

        if (query == null || query.isEmpty()) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    searchCompleteCallback.onSearchComplete(false);
                }
            });

            return;
        }

        if (searchQuery == null || !searchQuery.equals(query) || this.caseSensitive != caseSensitive) {
            searchQuery = query;
            this.caseSensitive = caseSensitive;

            editor.getSearchModel().setQuery(query, caseSensitive, new SearchProgressListener() {
                @Override
                public void onSearchProgress() {
                }

                @Override
                public void onSearchDone() {
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            int matches = editor.getSearchModel().getMatchManager().getTotalMatches();
                            searchCompleteCallback.onSearchComplete(matches > 0);
                        }
                    });
                }

                @Override
                public void onSearchBegin() {
                }
            });
        } else {
            //editor.getSearchModel().getMatchManager().selectNextMatch();
            final Position position = editor.getSearchModel().getMatchManager().selectNextMatchToTheEnd();

            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    if (position == null) {
                        searchCompleteCallback.onSearchComplete(false);
                    }

                    if (editor.getSelection().hasSelection()) {
                        searchCompleteCallback.onSearchComplete(true);
                    } else {
                        searchCompleteCallback.onSearchComplete(false);
                    }
                }
            });

        }
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#replaceMatch(java.lang.String) */
    @Override
    public void replaceMatch(String replacement) {
        if (editor.getSelection().hasSelection()) {
            //editor.getSearchModel().getMatchManager().replaceMatch(replacement)
            editor.getSearchModel().getMatchManager().replaceMatch(replacement);
        }
    }

    public Remover showPopup(IRegion region, Element content) {
        try {
            int line = document.getLineOfOffset(region.getOffset());
            LineInfo findLine = editor.getDocument().getLineFinder().findLine(line);
            int lineOffset = document.getLineOffset(line);
            int startColumn = region.getOffset() - lineOffset;
            return editorBundle.getEditorPopupController().showPopup(findLine, startColumn,
                                                                     startColumn + region.getLength(), null, new RendererImpl(content),
                                                                     null, VerticalAlign.BOTTOM, true, 200);
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
        return null;
    }

    /** @see com.google.gwt.user.client.ui.RequiresResize#onResize() */
    @Override
    public void onResize() {
        editor.getBuffer().onResize();
    }

    public com.google.collide.client.editor.Editor getEditor() {
        return editor;
    }

    /**
     * Returns the token list for the document opened in this editor.
     *
     * @return {@link Token} list
     */
    public List<? extends Token> getTokenList() {
        JsonArray<? extends Token> tokenList = extParser.getTokenList(getText());
        ArrayList<Token> list = new ArrayList<Token>(tokenList.size());
        for (Token token : tokenList.asIterable()) {
            list.add(token);
        }
        return list;
    }

    /** Listener that updates an appropriate IDocument instance. */
    final class TextListenerImpl implements TextListener {
        private boolean ignoreTextChanges;

        /** {@inheritDoc} */
        @Override
        public void onTextChange(Document document, JsonArray<TextChange> textChanges) {
            if (ignoreTextChanges) {
                return;
            }

            fireEvent(new EditorContentChangedEvent(CollabEditor.this));
            try {
                for (TextChange textChange : textChanges.asIterable()) {
                    final int offset =
                            CollabEditor.this.document.getLineOffset(textChange.getLineNumber()) + textChange.getColumn();
                    int length = 0;
                    String text = "";
                    switch (textChange.getType()) {
                        case INSERT:
                            text = textChange.getText();
                            break;
                        case DELETE:
                            length = textChange.getText().length();
                            break;
                        default:
                            throw new UnsupportedOperationException("Unknown type of text change: " + textChange.getType());
                    }
                    updateDocument(offset, length, text);
                }
            } catch (BadLocationException e) {
                Log.error(getClass(), e);
            }
        }

        /**
         * This ensures that ignore text changes mode is enabled/disabled temporarily.
         *
         * @param ignoreTextChanges
         *         <code>true</code> to ignore any text changes, <code>false</code> disable ignore mode
         */
        void setIgnoreTextChanges(boolean ignoreTextChanges) {
            this.ignoreTextChanges = ignoreTextChanges;
        }
    }

    private final class RendererImpl implements PopupRenderer {

        private final Element element;

        /**
         *
         */
        public RendererImpl(Element element) {
            this.element = element;

        }

        /** @see com.google.collide.client.code.popup.EditorPopupController.PopupRenderer#renderDom() */
        @Override
        public elemental.html.Element renderDom() {
            return (elemental.html.Element)element;
        }

    }
}
