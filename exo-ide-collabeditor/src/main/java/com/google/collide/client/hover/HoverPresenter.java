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
package com.google.collide.client.hover;

import elemental.html.Element;

import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.json.client.JsoStringMap;
import com.google.collide.client.CollabEditor;
import com.google.collide.client.code.popup.EditorPopupController.PopupRenderer;
import com.google.collide.client.code.popup.EditorPopupController.Remover;
import com.google.collide.client.editor.Editor;
import com.google.collide.client.editor.MouseHoverManager.MouseHoverListener;
import com.google.collide.client.ui.menu.PositionController.VerticalAlign;
import com.google.collide.shared.document.LineInfo;

import org.exoplatform.ide.editor.client.hover.TextHover;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IRegion;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class HoverPresenter {

    private JsoStringMap<TextHover> hovers = JsoStringMap.create();

    private final IDocument document;

    private final CollabEditor collabEditor;

    private IRegion currentRegion;

    private Remover currentPopup;

    /** @param collabEditor */
    public HoverPresenter(CollabEditor collabEditor, Editor editor, IDocument document) {
        this.collabEditor = collabEditor;
        this.document = document;
        editor.getMouseHoverManager().addMouseHoverListener(new MouseHoverListener() {

            @Override
            public void onMouseHover(int x, int y, LineInfo lineInfo, int column) {
                computateInformation(lineInfo, column);
            }
        });
    }

    /**
     * @param lineInfo
     * @param column
     */
    private void computateInformation(LineInfo lineInfo, int column) {
        int offset = 0;
        String contentType = null;
        try {
            offset = document.getLineOffset(lineInfo.number()) + column;
            contentType = document.getContentType(offset);
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
            return;
        }
        TextHover hover = hovers.get(contentType);
        if (hover == null)
            return;
        showHover(hover, offset, lineInfo);
    }

    /** @param hover */
    private void showHover(TextHover hover, int offset, LineInfo lineInfo) {
        IRegion hoverRegion = hover.getHoverRegion(collabEditor, offset);
        if (hoverRegion == null)
            return;

        if (hoverRegion.equals(currentRegion)) {
            if (currentPopup != null && currentPopup.isVisibleOrPending())
                return;
        }

        currentRegion = hoverRegion;
        if (currentPopup != null)
            currentPopup.remove();

        com.google.gwt.user.client.Element element = hover.getHoverInfo(collabEditor, hoverRegion);
        if (element == null)
            return;
        int lineOffset;
        try {
            lineOffset = document.getLineOffset(lineInfo.number());
            currentPopup =
                    collabEditor
                            .getEditorBundle()
                            .getEditorPopupController()
                            .showPopup(lineInfo, hoverRegion.getOffset() - lineOffset,
                                       (hoverRegion.getOffset() + hoverRegion.getLength()) - lineOffset, null,
                                       new PopupRendererImpl((Element)element), null, VerticalAlign.BOTTOM, true, 400);
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }

    }

    public void addHover(String contentType, TextHover hover) {
        hovers.put(contentType, hover);
    }

    class PopupRendererImpl implements PopupRenderer {

        private Element element;

        /** @param element */
        public PopupRendererImpl(Element element) {
            this.element = element;
        }

        /** @see com.google.collide.client.code.popup.EditorPopupController.PopupRenderer#renderDom() */
        @Override
        public Element renderDom() {
            return element;
        }

    }
}
