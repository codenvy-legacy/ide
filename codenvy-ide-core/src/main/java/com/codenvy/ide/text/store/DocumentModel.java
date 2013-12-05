// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.codenvy.ide.text.store;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.text.store.anchor.Anchor;
import com.codenvy.ide.text.store.anchor.AnchorManager;
import com.codenvy.ide.texteditor.api.UndoManager;
import com.codenvy.ide.util.ListenerManager;
import com.codenvy.ide.util.ListenerManager.Dispatcher;
import com.codenvy.ide.util.ListenerRegistrar;
import com.codenvy.ide.util.StringUtils;


/**
 * Document model for the code editor.
 * <p/>
 * The document is modeled using a linked list of lines. (This allows for very fast line insertions
 * and still good performance for other common editor operations.)
 * <p/>
 * During a text change, listeners will be called in this order:
 * <ul>
 * <li>{@link Anchor.ShiftListener}</li>
 * <li>{@link Anchor.RemoveListener}</li>
 * <li>{@link LineCountListener}</li>
 * <li>{@link LineListener}</li>
 * <li>{@link TextListener}</li>
 * </ul>
 */
public class DocumentModel implements TextStoreMutator {

    /**
     * A listener that is called when the number of lines in the document changes.
     * <p/>
     * See the callback ordering documented in {@link DocumentModel}.
     */
    public interface LineCountListener {
        void onLineCountChanged(DocumentModel document, int lineCount);
    }

    /**
     * A listener that is called when a line is added or removed from the
     * document.
     * <p/>
     * Note: In the case of a multiline insertion/deletion, this will be called
     * once.
     * <p/>
     * See the callback ordering documented in {@link DocumentModel}.
     */
    public interface LineListener {
        /**
         * @param lineNumber
         *         the line number of the first item in {@code addedLines}
         * @param addedLines
         *         a contiguous list of lines that were added
         */
        void onLineAdded(DocumentModel document, int lineNumber, Array<Line> addedLines);

        /**
         * @param lineNumber
         *         the previous line number of the first item in
         *         {@code removedLines}
         * @param removedLines
         *         a contiguous list of (now detached) lines that were
         *         removed
         */
        void onLineRemoved(DocumentModel document, int lineNumber, Array<Line> removedLines);
    }

    /**
     * A listener that is called when a text change occurs within a document.
     * <p/>
     * See the callback ordering documented in {@link DocumentModel}.
     */
    public interface TextListener {
        /**
         * Note: You should not mutate the document within this callback, as this is
         * not supported yet and can lead to other clients having stale position
         * information inside the {@code textChanges}.
         * <p/>
         * Note: The {@link TextChange} contains a reference to the live
         * {@link Line} from the document model. If you hold on to a reference after
         * {@link #onTextChange} returns, beware that the contents of the
         * {@link Line} could change, invalidating some of the state in the
         * {@link TextChange}.
         */
        void onTextChange(DocumentModel document, Array<TextChange> textChanges);
    }

    /**
     * A listener which is called before any changes are actually made to the
     * document and any anchors are moved.
     */
    public interface PreTextListener {
        /**
         * Note: You should not mutate the document within this callback, as this is
         * not supported yet and can lead to other clients having stale position
         * information inside the {@code textChanges}.
         * <p/>
         * <p/>
         * This callback is called synchronously with document mutations, the less
         * work you can do the better.
         *
         * @param line
         *         The line the text change will take place on.
         * @param lineNumber
         *         The line number of the line.
         * @param column
         *         The column the text change will start at.
         * @param text
         *         The text which is either being inserted or deleted.
         * @param type
         *         The type of {@link TextChange} that will be occurring.
         */
        void onPreTextChange(DocumentModel document, TextChange.Type type, Line line, int lineNumber, int column,
                             String text);
    }

    public static DocumentModel createEmpty() {
        return new DocumentModel();
    }

    public static DocumentModel createFromString(String contents) {
        DocumentModel doc = createEmpty();
        doc.insertText(doc.getFirstLine(), 0, 0, contents);

        return doc;
    }

    private static int idCounter = 0;

    private final AnchorManager anchorManager;

    private Line firstLine;

    private Line lastLine;

    private int lineCount = 1;

    private final ListenerManager<LineListener> lineListenerManager;

    private final ListenerManager<LineCountListener> lineCountListenerManager;

    private final LineFinder lineFinder;

    private final DocumentMutatorImpl documentMutator;

    private final ListenerManager<TextListener> textListenerManager;

    private final ListenerManager<PreTextListener> preTextListenerManager;

    private final int id = idCounter++;

    private final StringMap<Object> tags = Collections.createStringMap();

    protected DocumentModel() {
        firstLine = lastLine = Line.create(this, "");
        firstLine.setAttached(true);

        anchorManager = new AnchorManager();

        documentMutator = new DocumentMutatorImpl(this);

        lineListenerManager = ListenerManager.create();

        lineCountListenerManager = ListenerManager.create();

        lineFinder = new LineFinder(this);

        textListenerManager = ListenerManager.create();

        preTextListenerManager = ListenerManager.create();
    }

    public String asText() {
        StringBuilder sb = new StringBuilder();
        for (Line line = firstLine; line != null; line = line.getNextLine()) {
            sb.append(line.getText());
        }

        return sb.toString();
    }

    @Override
    public TextChange deleteText(Line line, int column, int deleteCount) {
        return documentMutator.deleteText(line, column, deleteCount);
    }

    @Override
    public TextChange deleteText(Line line, int lineNumber, int column, int deleteCount) {
        return documentMutator.deleteText(line, lineNumber, column, deleteCount);
    }

    public AnchorManager getAnchorManager() {
        return anchorManager;
    }

    public Line getFirstLine() {
        return firstLine;
    }

    public LineInfo getFirstLineInfo() {
        return new LineInfo(firstLine, 0);
    }

    public Line getLastLine() {
        return lastLine;
    }

    public LineInfo getLastLineInfo() {
        return new LineInfo(lastLine, getLastLineNumber());
    }

    public int getLastLineNumber() {
        return lineCount - 1;
    }

    public int getLineCount() {
        return lineCount;
    }

    public ListenerRegistrar<LineCountListener> getLineCountListenerRegistrar() {
        return lineCountListenerManager;
    }

    public LineFinder getLineFinder() {
        return lineFinder;
    }

    public ListenerRegistrar<LineListener> getLineListenerRegistrar() {
        return lineListenerManager;
    }

    public String getText(Line line, int column, int count) {
        if (column > line.getText().length())
            throw new IndexOutOfBoundsException();

        StringBuilder s = new StringBuilder(StringUtils.substringGuarded(line.getText(), column, count));
        int remainingCount = count - s.length();
        line = line.getNextLine();

        while (remainingCount > 0 && line != null) {
            String capturedLineText = StringUtils.substringGuarded(line.getText(), 0, remainingCount);
            s.append(capturedLineText);
            remainingCount -= capturedLineText.length();

            line = line.getNextLine();
        }

        return s.toString();
    }

    public ListenerRegistrar<TextListener> getTextListenerRegistrar() {
        return textListenerManager;
    }

    public ListenerRegistrar<PreTextListener> getPreTextListenerRegistrar() {
        return preTextListenerManager;
    }

    @Override
    public TextChange insertText(Line line, int column, String text) {
        return documentMutator.insertText(line, column, text);
    }

    @Override
    public TextChange insertText(Line line, int lineNumber, int column, String text) {
        return documentMutator.insertText(line, lineNumber, column, text);
    }

    @Override
    public TextChange insertText(Line line, int lineNumber, int column, String text, boolean canReplaceSelection) {
        return documentMutator.insertText(line, lineNumber, column, text, canReplaceSelection);
    }

    @Override
    public String toString() {
        return asText();
    }

    public String asDebugString() {
        StringBuilder sb = new StringBuilder("Line count: " + getLineCount() + "\n");
        for (Line line = firstLine; line != null; line = line.getNextLine()) {
            sb.append(line.getText()).append("---\n");
        }

        return sb.toString();
    }

    public int getId() {
        return id;
    }

    /** @see Line#putTag(String, Object) */
    public <T> void putTag(String key, T value) {
        tags.put(key, value);
    }

    /** @see Line#getTag(String) */
    @SuppressWarnings("unchecked")
    public <T> T getTag(String key) {
        return (T)tags.get(key);
    }

    void commitLineCountChange(int lineCountDelta) {
        if (lineCountDelta != 0) {
            lineCount += lineCountDelta;
            lineCountListenerManager.dispatch(new Dispatcher<DocumentModel.LineCountListener>() {
                @Override
                public void dispatch(LineCountListener listener) {
                    listener.onLineCountChanged(DocumentModel.this, lineCount);
                }
            });
        }
    }

    void dispatchLineAdded(final int lineNumber, final Array<Line> addedLines) {
        lineListenerManager.dispatch(new Dispatcher<DocumentModel.LineListener>() {
            @Override
            public void dispatch(LineListener listener) {
                listener.onLineAdded(DocumentModel.this, lineNumber, addedLines);
            }
        });
    }

    void dispatchLineRemoved(final int lineNumber, final Array<Line> removedLines) {
        lineListenerManager.dispatch(new Dispatcher<DocumentModel.LineListener>() {
            @Override
            public void dispatch(LineListener listener) {
                listener.onLineRemoved(DocumentModel.this, lineNumber, removedLines);
            }
        });
    }

    void dispatchTextChange(final Array<TextChange> textChanges) {
        textListenerManager.dispatch(new Dispatcher<DocumentModel.TextListener>() {
            @Override
            public void dispatch(TextListener listener) {
                listener.onTextChange(DocumentModel.this, textChanges);
            }
        });
    }

    void dispatchPreTextChange(final TextChange.Type type, final Line line, final int lineNumber, final int column,
                               final String text) {
        preTextListenerManager.dispatch(new Dispatcher<DocumentModel.PreTextListener>() {
            @Override
            public void dispatch(PreTextListener listener) {
                listener.onPreTextChange(DocumentModel.this, type, line, lineNumber, column, text);
            }
        });
    }

    void setFirstLine(Line line) {
        assert line != null : "Line cannot be null";
        firstLine = line;
    }

    void setLastLine(Line line) {
        assert line != null : "Line cannot be null";
        lastLine = line;
    }

    /** @see com.codenvy.ide.text.store.TextStoreMutator#getUndoManager() */
    @Override
    public UndoManager getUndoManager() {
        return null;
    }
}
