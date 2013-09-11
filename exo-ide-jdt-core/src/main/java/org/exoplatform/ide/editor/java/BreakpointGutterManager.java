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

import elemental.css.CSSStyleDeclaration;
import elemental.html.Element;

import com.codenvy.ide.client.util.Elements;
import com.codenvy.ide.commons.shared.ListenerRegistrar.Remover;
import com.codenvy.ide.json.client.JsIntegerMap;
import com.google.collide.client.CollabEditorExtension;
import com.google.collide.client.Resources;
import com.google.collide.client.editor.Buffer;
import com.google.collide.client.editor.ViewportModel;
import com.google.collide.client.editor.gutter.Gutter;
import com.google.collide.client.editor.gutter.Gutter.ClickListener;
import com.google.collide.client.editor.renderer.Renderer;
import com.google.collide.client.editor.renderer.Renderer.LineLifecycleListener;
import com.google.collide.shared.document.Line;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.editor.java.Breakpoint.Type;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class BreakpointGutterManager {

    private final JavaClientBundle bundle;

    private final Gutter gutter;

    private final Buffer buffer;

    private JsIntegerMap<Breakpoint> breakpoints = JsIntegerMap.create();

    private JsIntegerMap<Element> breakpointsElement = JsIntegerMap.create();

    private Element currentDebugLine;

    private Element currentDebugHighlighter;

    private int previousBottomLineNumber = -1;

    private int previousTopLineNumber = -1;

    private JsIntegerMap<Element> lineNumberToElementCache;

    private ViewportModel viewport;

    private final Renderer renderer;

    /**
     * @param gutter
     * @param viewportModel
     * @param buffer
     * @param renderer
     * @param instance
     */
    public BreakpointGutterManager(Gutter gutter, Buffer buffer, ViewportModel viewport, Renderer renderer,
                                   JavaClientBundle bundle) {
        this.gutter = gutter;
        this.lineNumberToElementCache = JsIntegerMap.create();
        this.buffer = buffer;
        this.viewport = viewport;
        this.renderer = renderer;
        this.renderer.getLineLifecycleListenerRegistrar().add(new LineLifecycleListener() {
            @Override
            public void onRenderedLineShifted(Line line, int lineNumber) {
                render();
            }

            @Override
            public void onRenderedLineGarbageCollected(Line line) {
            }

            @Override
            public void onRenderedLineCreated(Line line, int lineNumber) {
                render();
            }
        });
        this.bundle = bundle;
        gutter.setWidth(14);
        Resources resources = CollabEditorExtension.get().getContext().getResources();
        currentDebugHighlighter = Elements.createDivElement(resources.workspaceEditorBufferCss().line());
        currentDebugHighlighter.addClassName(resources.workspaceEditorBufferCss().currentLine());
        currentDebugHighlighter.getStyle().setBackgroundColor("#ffc8c8");
        currentDebugHighlighter.getStyle().setTop(0, "PX");
    }

    public Remover addLineClickListener(final ClickListener listener) {
        return gutter.getClickListenerRegistrar().add(new ClickListener() {

            @Override
            public void onClick(int y) {
                int lineNumber = buffer.convertYToLineNumber(y, true);
                listener.onClick(lineNumber + 1);
            }
        });
    }

    public void setBreakpoint(Breakpoint breakpoint) {
        if (breakpointsElement.hasKey(breakpoint.getLineNumber())) {
            gutter.removeUnmanagedElement(breakpointsElement.remove(breakpoint.getLineNumber()));
        }
        breakpoints.put(breakpoint.getLineNumber(), breakpoint);
        Image i = createImage(breakpoint.getType());
        Element element = (Element)i.getElement();
        element.getStyle().setHeight(buffer.getEditorLineHeight() + "px");
        element.getStyle().setPosition("absolute");
        element.getStyle().setTop(buffer.convertLineNumberToY(breakpoint.getLineNumber() - 1), "px");
        element.setId("breakpoit-toggle-" + breakpoint.getLineNumber());
        breakpointsElement.put(breakpoint.getLineNumber(), element);
        gutter.addUnmanagedElement(element);
    }

    public void setCurrentDebugLine(Breakpoint bp) {
        if (currentDebugLine != null)
            gutter.removeUnmanagedElement(currentDebugLine);
        Image i = createImage(bp.getType());

        currentDebugLine = (Element)i.getElement();
        currentDebugLine.getStyle().setHeight(buffer.getEditorLineHeight() + "px");
        currentDebugLine.getStyle().setPosition("absolute");
        int top = buffer.convertLineNumberToY(bp.getLineNumber() - 1);
        currentDebugLine.getStyle().setTop(top + 3, "px");
        currentDebugLine.getStyle().setRight("0px");
        currentDebugLine.getStyle().setWidth("10px");
        currentDebugLine.getStyle().setHeight("11px");
        currentDebugLine.setId("breakpoit-active-" + bp.getLineNumber());
        gutter.addUnmanagedElement(currentDebugLine);
        currentDebugHighlighter.getStyle().setTop(top, "px");
        buffer.addUnmanagedElement(currentDebugHighlighter);
    }

    /**
     * @param type
     * @return
     */
    private Image createImage(Type type) {
        ImageResource res;
        switch (type) {
            case BREAKPOINT:
                res = bundle.breakpoint();
                break;

            case CURRENT:
                res = bundle.breakpointCurrent();
                break;

            //TODO add images for other breakpoint type

            default:
                res = bundle.breakpoint();
                break;
        }
        return new Image(res);
    }

    public void removeBreakpoint(int line) {
        if (breakpointsElement.hasKey(line)) {
            gutter.removeUnmanagedElement(breakpointsElement.remove(line));
            breakpoints.remove(line);
        }
    }

    /** @param bp */
    public void removeCurrentDebugLine(Breakpoint bp) {
        if (currentDebugLine != null)
            gutter.removeUnmanagedElement(currentDebugLine);
        currentDebugLine = null;
        buffer.removeUnmanagedElement(currentDebugHighlighter);
    }

    /** @return the gutter */
    public Gutter getGutter() {
        return gutter;
    }

    private void fillOrUpdateLines(int beginLineNumber, int endLineNumber) {
        for (int i = beginLineNumber; i <= endLineNumber; i++) {
            if (buffer.modelLine2VisibleLine(i) == -1) {
                garbageCollectLines(i, i);
                continue;
            }
            Element lineElement = lineNumberToElementCache.get(i);
            if (lineElement != null) {
                updateElementPosition(lineElement, i);
            } else {
                Element element = createElement(i);
                lineNumberToElementCache.put(i, element);
                gutter.addUnmanagedElement(element);
            }
        }
    }

    private Element createElement(int lineNumber) {
        Element element = Elements.createDivElement();
        // Line 0 will be rendered as Line 1
        element.setId("breakpoint-place-" + String.valueOf(lineNumber + 1));
        element.setTextContent(" "); //Add space for use this div in Selenium test for clicking
        element.getStyle().setTop(buffer.calculateLineTop(lineNumber), CSSStyleDeclaration.Unit.PX);
        element.getStyle().setWidth(100, CSSStyleDeclaration.Unit.PCT);
        element.getStyle().setPosition("absolute");
        return element;
    }

    private void updateElementPosition(Element lineNumberElement, int lineNumber) {
        lineNumberElement.getStyle().setTop(buffer.calculateLineTop(lineNumber), CSSStyleDeclaration.Unit.PX);
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

}
