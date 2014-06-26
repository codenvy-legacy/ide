/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.texteditor.embeddedimpl.codemirror.jso;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class CMDocumentOverlay extends JavaScriptObject {

    protected CMDocumentOverlay() {
    }

    // history

    public final native void undo() /*-{
        return this.undo();
    }-*/;

    public final native void redo() /*-{
        return this.redo();
    }-*/;

    public final native void clearHistory() /*-{
        return this.clearHistory();
    }-*/;

    public final native void undoSelection() /*-{
        return this.undoSelection();
    }-*/;

    public final native void redoSelection() /*-{
        return this.redoSelection();
    }-*/;

    public final native int historyUndoSize() /*-{
        return this.historySize().undo;
    }-*/;

    public final native int historyRedoSize() /*-{
        return this.historySize().redo;
    }-*/;

    // content

    public final native String getValue() /*-{
        return this.getValue();
    }-*/;

    public final native String getValue(String separator) /*-{
        return this.getValue(separator);
    }-*/;

    public final native void setValue(String newContent) /*-{
        this.setValue(newContent);
    }-*/;

    public final native String getRange(int fromLine, int fromChar, int toLine, int toChar) /*-{
        return this.getRange({ line : fromLine, ch : fromChar }, { line : toLine, ch : toChar });
    }-*/;

    public final native String getRange(int fromLine, int fromChar, int toLine, int toChar, String separator) /*-{
        return this.getRange({ line : fromLine, ch : fromChar }, { line : toLine, ch : toChar }, separator);
    }-*/;

    public final native void replaceRange(String replacement, int fromLine, int fromChar, int toLine, int toChar) /*-{
        return this.replaceRange(replacement, { line : fromLine, ch : fromChar }, { line : toLine, ch : toChar });
    }-*/;

    public final native void replaceRange(String replacement, int fromLine, int fromChar) /*-{
        return this.replaceRange(replacement, { line : fromLine, ch : fromChar });
    }-*/;

    public final native String getLine(int index) /*-{
        return this.getLine(index);
    }-*/;

    public final native int getFirstLine() /*-{
        return this.getLastLine();
    }-*/;

    public final native int getLastLine() /*-{
        return this.getLastLine();
    }-*/;


    public final native int lineCount() /*-{
        return this.lineCount();
    }-*/;

    // position

    public final native CMPositionOverlay getCursor() /*-{
        return this.getCursor();
    }-*/;

    /**
     * Returns the position one end of the primary selection.
     * 
     * @param whichEndOfSelection "from", "to", "head" or "anchor"
     * @return one end
     */
    public final native CMPositionOverlay getCursor(String whichEndOfSelection) /*-{
        return this.getCursor(whichEndOfSelection);
    }-*/;

    public final CMPositionOverlay getCursorFrom() {
        return this.getCursor("from");
    }

    public final CMPositionOverlay getCursorTo() {
        return this.getCursor("to");
    }

    public final CMPositionOverlay getCursorHead() {
        return this.getCursor("head");
    }

    public final CMPositionOverlay getCursorAnchor() {
        return this.getCursor("anchor");
    }

    public final native void setCursor(CMPositionOverlay position)/*-{
        this.setCursor(position);
    }-*/;

    public final native void setCursor(int line, int ch)/*-{
        this.setCursor(line, ch);
    }-*/;

    public final native void setCursor(CMPositionOverlay position, CMSetSelectionOptions options)/*-{
        this.setCursor(position, options);
    }-*/;

    public final native void setCursor(int line, int ch, CMSetSelectionOptions options)/*-{
        this.setCursor(line, ch, options);
    }-*/;

    public final native CMPositionOverlay posFromIndex(int index) /*-{
        return this.posFromIndex(index);
    }-*/;

    public final native int indexFromPos(CMPositionOverlay position) /*-{
        return this.indexFromPos(position);
    }-*/;

    // editor

    public final native CMEditorOverlay getEditor() /*-{
        return this.getEditor();
    }-*/;

    // selection

    public final native String getSelection() /*-{
        return this.getSelection();
    }-*/;

    public final native String getSelection(String lineSep) /*-{
        return this.getSelection(lineSep);
    }-*/;

    public final native JsArrayString getSelections(String lineSep) /*-{
        return this.getSelections(lineSep);
    }-*/;

    public final native boolean somethingSelected() /*-{
        return this.somethingSelected();
    }-*/;

    public final native JsArray<CMSelectionOverlay> listSelections() /*-{
        return this.listSelection();
    }-*/;

    public final native void replaceSelection(String replacement) /*-{
        this.replaceSelection(replacement);
    }-*/;

    /**
     * Replace selection(s) with replacement.
     * 
     * @param replacement the replacement string
     * @param selectAfter if "around", the new text is selected, if "start", the selection is at the beginning of the new text
     * @return
     */
    public final native void replaceSelection(String replacement, String selectAfter) /*-{
        this.replaceSelection(replacement, selectAfter);
    }-*/;

    // line operations

    public final native CMLineHandleOverlay getLineHandle(int lineIndex) /*-{
        return this.getLineHandle(lineIndex);
    }-*/;

    public final native int getLineNumber(CMLineHandleOverlay linehandle) /*-{
        return this.getLineNumber(lineHandle);
    }-*/;

    public final native void eachline(LineOperation lineOperation) /*-{
        this.eachLine(
            lineOperation.@com.codenvy.ide.texteditor.embeddedimpl.codemirror.jso.CMDocumentOverlay.LineOperation::processLine(Lcom/codenvy/ide/texteditor/embeddedimpl/codemirror/jso/CMLineHandleOverlay;)
        );
    }-*/;

    public final native void eachline(int start, int end, LineOperation lineOperation) /*-{
        this.eachLine(
            start, end,
            lineOperation.@com.codenvy.ide.texteditor.embeddedimpl.codemirror.jso.CMDocumentOverlay.LineOperation::processLine(Lcom/codenvy/ide/texteditor/embeddedimpl/codemirror/jso/CMLineHandleOverlay;)
        );
    }-*/;

    public interface LineOperation {
        void processLine(CMLineHandleOverlay lineHandle);
    }
}
