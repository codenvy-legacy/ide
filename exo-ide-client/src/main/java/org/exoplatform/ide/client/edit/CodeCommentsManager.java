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
package org.exoplatform.ide.client.edit;

import org.exoplatform.gwtframework.commons.util.Log;
import org.exoplatform.ide.client.framework.editor.AddCommentsModifierEvent;
import org.exoplatform.ide.client.framework.editor.AddCommentsModifierHandler;
import org.exoplatform.ide.client.framework.editor.CommentsModifier;
import org.exoplatform.ide.client.framework.editor.event.*;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.edits.MalformedTreeException;
import org.exoplatform.ide.editor.shared.text.edits.TextEdit;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 6, 2012 4:59:20 PM anya $
 */
public class CodeCommentsManager implements AddCommentsModifierHandler, EditorAddBlockCommentHandler,
                                            EditorRemoveBlockCommentHandler, EditorActiveFileChangedHandler, EditorToggleCommentHandler {

    private Map<String, CommentsModifier> commentModifiers = new HashMap<String, CommentsModifier>();

    private Editor editor;

    private FileModel activeFile;

    public CodeCommentsManager() {
        IDE.addHandler(AddCommentsModifierEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);

        IDE.addHandler(EditorAddBlockCommentEvent.TYPE, this);
        IDE.addHandler(EditorRemoveBlockCommentEvent.TYPE, this);
        IDE.addHandler(EditorToggleCommentEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorRemoveBlockCommentHandler#onEditorRemoveBlockComment(org.exoplatform
     * .ide.client.framework.editor.event.EditorRemoveBlockCommentEvent) */
    @Override
    public void onEditorRemoveBlockComment(EditorRemoveBlockCommentEvent event) {
        if (commentModifiers.containsKey(activeFile.getMimeType())) {
            CommentsModifier commentsModifier = commentModifiers.get(activeFile.getMimeType());
            TextEdit textEdit;
            try {
                do {
                    textEdit = commentsModifier.removeBlockComment(editor.getSelectionRange(), editor.getDocument());
                    textEdit.apply(editor.getDocument());
                } while (textEdit.getRegion().getLength() != 0); //try to find other block comments in selection range
            } catch (MalformedTreeException e) {
                Log.info(e.getMessage());
            } catch (BadLocationException e) {
                Log.info(e.getMessage());
            }
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorAddBlockCommentHandler#onEditorAddBlockComment(org.exoplatform.ide
     * .client.framework.editor.event.EditorAddBlockCommentEvent) */
    @Override
    public void onEditorAddBlockComment(EditorAddBlockCommentEvent event) {
        if (commentModifiers.containsKey(activeFile.getMimeType())) {
            CommentsModifier commentsModifier = commentModifiers.get(activeFile.getMimeType());
            TextEdit textEdit = commentsModifier.addBlockComment(editor.getSelectionRange(), editor.getDocument());
            try {
                textEdit.apply(editor.getDocument());
            } catch (MalformedTreeException e) {
                Log.info(e.getMessage());
            } catch (BadLocationException e) {
                Log.info(e.getMessage());
            }
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.AddCommentsModifierHandler#onAddCommentsModifier(org.exoplatform.ide.client
     * .framework.editor.AddCommentsModifierEvent) */
    @Override
    public void onAddCommentsModifier(AddCommentsModifierEvent event) {
        commentModifiers.put(event.getMimeType(), event.getCommentsModifier());
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        this.activeFile = event.getFile();
        this.editor = event.getEditor();
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorToggleCommentHandler#onEditorToggleComment(org.exoplatform.ide.client
     * .framework.editor.event.EditorToggleCommentEvent) */
    @Override
    public void onEditorToggleComment(EditorToggleCommentEvent event) {
        if (commentModifiers.containsKey(activeFile.getMimeType())) {
            CommentsModifier commentsModifier = commentModifiers.get(activeFile.getMimeType());
            TextEdit textEdit = commentsModifier.toggleSingleLineComment(editor.getSelectionRange(), editor.getDocument());
            try {
                textEdit.apply(editor.getDocument());
            } catch (MalformedTreeException e) {
                Log.info(e.getMessage());
            } catch (BadLocationException e) {
                Log.info(e.getMessage());
            }
        }
    }
}
