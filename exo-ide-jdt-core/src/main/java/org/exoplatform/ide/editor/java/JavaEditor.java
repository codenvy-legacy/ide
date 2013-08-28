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
package org.exoplatform.ide.editor.java;

import com.google.collide.client.CollabEditor;
import com.google.collide.client.CollabEditorExtension;
import com.google.collide.client.editor.Editor.DocumentListener;
import com.google.collide.client.editor.gutter.Gutter;
import com.google.collide.client.editor.gutter.Gutter.Position;

import org.eclipse.jdt.client.JavaContentAssistProcessor;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.client.api.EditorCapability;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;
import org.exoplatform.ide.editor.java.hover.JavaTypeHover;
import org.exoplatform.ide.editor.shared.text.Document;
import org.exoplatform.ide.editor.shared.text.IDocument;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaEditor extends CollabEditor {

    private BreakpointGutterManager breakPointManager;

    /** @param mimeType */
    public JavaEditor(String mimeType) {
        super(mimeType);
        editorBundle.getAutocompleter().addLanguageSpecificAutocompleter(new JavaAutocompleter());
        editorBundle.getAutocompleter().addContentAssitProcessor(IDocument.DEFAULT_CONTENT_TYPE,
                                                                 new JavaContentAssistProcessor());
        editor.getDocumentListenerRegistrar().add(new DocumentListener() {
            @Override
            public void onDocumentChanged(com.google.collide.shared.document.Document oldDocument,
                                          com.google.collide.shared.document.Document newDocument) {
                if (newDocument != null) {
                    final Gutter gutter =
                            editor.createGutter(false, Position.LEFT, CollabEditorExtension.get().getContext().getResources()
                                                                                           .workspaceEditorCss().leftGutterBase());
                    breakPointManager =
                            new BreakpointGutterManager(gutter, editor.getBuffer(), editor.getViewport(), editor.getRenderer(),
                                                        JavaClientBundle.INSTANCE);
                    breakPointManager.render();
                }
            }
        });
        getEditor().getFoldingManager().setFoldFinder(new JavaFoldOccurrencesFinder());
    }

    /** @return the breakPointManager */
    public BreakpointGutterManager getBreakPointManager() {
        return breakPointManager;
    }

    /** @see com.google.collide.client.CollabEditor#setText(java.lang.String) */
    @Override
    public void setText(String text) {
        super.setText(text);
        getHoverPresenter().addHover(Document.DEFAULT_CONTENT_TYPE, new JavaTypeHover(IDE.eventBus()));
    }

    /** @see com.google.collide.client.CollabEditor#getCursorOffsetLeft() */
    @Override
    public int getCursorOffsetLeft() {
        return super.getCursorOffsetLeft() + breakPointManager.getGutter().getWidth();
    }

    /** @see com.google.collide.client.CollabEditor#isCapable(org.exoplatform.ide.editor.client.api.EditorCapability) */
    @Override
    public boolean isCapable(EditorCapability capability) {
        if (capability == EditorCapability.CODE_FOLDING) {
            return true;
        } else {
            return super.isCapable(capability);
        }
    }

}
