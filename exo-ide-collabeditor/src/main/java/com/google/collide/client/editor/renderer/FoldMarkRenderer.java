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
package com.google.collide.client.editor.renderer;

import elemental.css.CSSStyleDeclaration;
import elemental.dom.Node;
import elemental.dom.NodeList;
import elemental.html.Element;

import com.codenvy.ide.client.util.Elements;
import com.codenvy.ide.json.client.JsIntegerMap;
import com.google.collide.client.editor.Buffer;
import com.google.collide.client.editor.ViewportModel;
import com.google.collide.client.editor.folding.FoldMarker;
import com.google.collide.client.editor.folding.FoldingManager;
import com.google.collide.client.editor.gutter.Gutter;


/**
 * A renderer for the fold markers in the left gutter.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FoldMarkRenderer.java Mar 12, 2013 2:14:05 PM azatsarynnyy $
 */
public class FoldMarkRenderer {
    private final Buffer buffer;

    private final Gutter gutter;

    private int previousBottomLineNumber = -1;

    private int previousTopLineNumber = -1;

    private JsIntegerMap<Element> lineNumberToElementCache;

    private final ViewportModel viewport;

    private final FoldingManager foldingManager;

    public FoldMarkRenderer(Buffer buffer, Gutter gutter, ViewportModel viewport, FoldingManager foldingManager) {
        this.buffer = buffer;
        this.gutter = gutter;
        this.lineNumberToElementCache = JsIntegerMap.create();
        this.viewport = viewport;
        this.foldingManager = foldingManager;
    }

    void renderImpl(int updateBeginLineNumber) {
        int topLineNumber = viewport.getTopLineNumber();
        int bottomLineNumber = viewport.getBottomLineNumber();

        if (previousBottomLineNumber == -1 || topLineNumber > previousBottomLineNumber
            || bottomLineNumber < previousTopLineNumber) {

            if (previousBottomLineNumber > -1) {
                garbageCollectLines(previousTopLineNumber, previousBottomLineNumber);
            }

            fillOrUpdateLines(topLineNumber, bottomLineNumber);
        } else {
         /*
          * The viewport was shifted and part of the old viewport will be in the
          * new viewport.
          */
            // first garbage collect any lines that have gone off the screen
            if (previousTopLineNumber < topLineNumber) {
                // off the top
                garbageCollectLines(previousTopLineNumber, topLineNumber - 1);
            }

            if (previousBottomLineNumber > bottomLineNumber) {
                // off the bottom
                garbageCollectLines(bottomLineNumber + 1, previousBottomLineNumber);
            }

         /*
          * Re-create any line numbers that are now visible or have had their
          * positions shifted.
          */
            if (previousTopLineNumber > topLineNumber) {
                // new lines at the top
                fillOrUpdateLines(topLineNumber, previousTopLineNumber - 1);
            }

            if (updateBeginLineNumber >= 0 && updateBeginLineNumber <= bottomLineNumber) {
                // lines updated in the middle; redraw everything below
                fillOrUpdateLines(updateBeginLineNumber, bottomLineNumber);
            } else {
                // only check new lines scrolled in from the bottom
                if (previousBottomLineNumber < bottomLineNumber) {
                    fillOrUpdateLines(previousBottomLineNumber, bottomLineNumber);
                }
            }
        }

        previousTopLineNumber = viewport.getTopLineNumber();
        previousBottomLineNumber = viewport.getBottomLineNumber();
    }

    void render() {
        renderImpl(-1);
    }

    /**
     * Re-render all line numbers including and after lineNumber to account for
     * spacer movement.
     */
    void renderLineAndFollowing(int lineNumber) {
        renderImpl(lineNumber);
    }

    private void fillOrUpdateLines(int beginLineNumber, int endLineNumber) {
        for (int i = beginLineNumber; i <= endLineNumber; i++) {
            if (buffer.modelLine2VisibleLine(i) < 0) {
                garbageCollectLines(i, i);
                continue;
            }

            FoldMarker foldMarker = foldingManager.getFoldMarkerOfLine(i, false);
            if (foldMarker == null) {
                garbageCollectLines(i, i);
                continue;
            }
            if (!foldMarker.isCollapsed()) {
                foldMarker = foldingManager.getFoldMarkerOfLine(i, true);
                if (foldMarker == null) {
                    garbageCollectLines(i, i);
                    continue;
                }
            }

            Element lineElement = lineNumberToElementCache.get(i);
            if (lineElement != null) {
                updateElementPosition(lineElement, i, foldMarker);
            } else {
                Element element = createElement(i, foldMarker);
                lineNumberToElementCache.put(i, element);
                gutter.addUnmanagedElement(element);
            }
        }
    }

    private void updateElementPosition(Element foldMarkElement, int lineNumber, FoldMarker foldMarker) {
        final int lineHeight = buffer.getEditorLineHeight();
        final int elementHeight = 9;
        final int additionalTop = (lineHeight - elementHeight) / 2;
        foldMarkElement.getStyle().setTop(buffer.calculateLineTop(lineNumber) + additionalTop,
                                          CSSStyleDeclaration.Unit.PX);

        NodeList childNodes = foldMarkElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            foldMarkElement.removeChild(childNodes.item(i));
        }
        foldMarkElement.appendChild((Node)foldMarker.getImage().getElement());

        setId(foldMarkElement, lineNumber, foldMarker);
    }

    private Element createElement(int lineNumber, FoldMarker foldMarker) {
        Element element = Elements.createDivElement();
        element.appendChild((Node)foldMarker.getImage().getElement());

        element.getStyle().setHeight(buffer.getEditorLineHeight() + "px");
        element.getStyle().setPosition("absolute");
        element.getStyle().setCursor("pointer");

        final int lineHeight = buffer.getEditorLineHeight();
        final int elementHeight = 9;
        final int freeSpaceAbove = (lineHeight - elementHeight) / 2;

        element.getStyle().setTop(buffer.calculateLineTop(lineNumber) + freeSpaceAbove, "px");

        setId(element, lineNumber, foldMarker);

        return element;
    }

    private void garbageCollectLines(int beginLineNumber, int endLineNumber) {
        for (int i = beginLineNumber; i <= endLineNumber; i++) {
            Element lineElement = lineNumberToElementCache.get(i);
            if (lineElement != null) {
                gutter.removeUnmanagedElement(lineElement);
                lineNumberToElementCache.erase(i);
            } else {
                // don't throws exception because line may be folded in this case
                continue;
                //            throw new IndexOutOfBoundsException("Tried to garbage collect line number " + i
                //               + " when it does not exist.");
            }
        }
    }

    private void setId(Element foldMarkElement, int lineNumber, FoldMarker foldMarker) {
        if (foldMarker.isCollapsed()) {
            foldMarkElement.setId("foldGutterExpandMarker_line:" + (lineNumber + 1));
        } else {
            foldMarkElement.setId("foldGutterCollapseMarker_line:" + (lineNumber + 1));
        }
    }

    /** Once torn down, this instance cannot be used again. */
    void teardown() {
    }

}
