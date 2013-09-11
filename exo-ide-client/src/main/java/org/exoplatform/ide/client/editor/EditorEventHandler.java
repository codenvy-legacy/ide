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
package org.exoplatform.ide.client.editor;

import com.google.gwt.event.shared.HandlerRegistration;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.editor.api.codeassitant.RunCodeAssistantEvent;
import org.exoplatform.ide.editor.api.codeassitant.RunCodeAssistantHandler;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.event.*;
import org.exoplatform.ide.editor.client.marking.EditorLineNumberContextMenuEvent;
import org.exoplatform.ide.editor.client.marking.EditorLineNumberContextMenuHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class EditorEventHandler implements EditorInitializedHandler, EditorLineNumberContextMenuHandler,
                                           EditorContentChangedHandler, EditorCursorActivityHandler, EditorHotKeyPressedHandler,
                                           EditorFocusReceivedHandler,
                                           EditorContextMenuHandler, RunCodeAssistantHandler {

    private Editor editor;

    private List<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();

    private boolean handleEvents = false;

    public EditorEventHandler(Editor editor) {
        this.editor = editor;

        handlers.add(editor.asWidget().addHandler(this, EditorInitializedEvent.TYPE));
        handlers.add(editor.asWidget().addHandler(this, EditorLineNumberContextMenuEvent.TYPE));
        handlers.add(editor.asWidget().addHandler(this, EditorContentChangedEvent.TYPE));
        handlers.add(editor.asWidget().addHandler(this, EditorCursorActivityEvent.TYPE));
        handlers.add(editor.asWidget().addHandler(this, EditorHotKeyPressedEvent.TYPE));
        handlers.add(editor.asWidget().addHandler(this, EditorFocusReceivedEvent.TYPE));
        handlers.add(editor.asWidget().addHandler(this, EditorContextMenuEvent.TYPE));
        handlers.add(editor.asWidget().addHandler(this, RunCodeAssistantEvent.TYPE));
    }

    public void removeHandlers() {
        for (HandlerRegistration handler : handlers) {
            handler.removeHandler();
        }

        handlers.clear();
    }

    public void enableHandling() {
        handleEvents = true;
    }

    public void disableHandling() {
        handleEvents = false;
    }

    @Override
    public void onEditorInitialized(EditorInitializedEvent event) {
        IDE.fireEvent(event);

        enableHandling();
    }

    @Override
    public void onEditorLineNumberContextMenu(EditorLineNumberContextMenuEvent event) {
        if (!handleEvents) {
            return;
        }

        IDE.fireEvent(event);
    }

    @Override
    public void onEditorContentChanged(EditorContentChangedEvent event) {
        if (!handleEvents) {
            return;
        }

        IDE.fireEvent(event);
    }

    @Override
    public void onEditorCursorActivity(EditorCursorActivityEvent event) {
        if (!handleEvents) {
            return;
        }

        IDE.fireEvent(event);
    }

    @Override
    public void onEditorHotKeyPressed(EditorHotKeyPressedEvent event) {
        if (!handleEvents) {
            return;
        }

        IDE.fireEvent(event);
    }

    @Override
    public void onEditorFocusReceived(EditorFocusReceivedEvent event) {
        if (!handleEvents) {
            return;
        }

        IDE.fireEvent(event);
    }

    @Override
    public void onEditorContextMenu(EditorContextMenuEvent event) {
        if (!handleEvents) {
            return;
        }

        IDE.fireEvent(event);
    }

    @Override
    public void onRunCodeAssistant(RunCodeAssistantEvent event) {
        if (!handleEvents) {
            return;
        }

        IDE.fireEvent(event);
    }

}
