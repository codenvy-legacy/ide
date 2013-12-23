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

package com.codenvy.ide.texteditor.renderer;

import elemental.css.CSSStyleDeclaration;
import elemental.html.Element;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.js.JsoIntegerMap;
import com.codenvy.ide.debug.BreakpointGutterManager;
import com.codenvy.ide.text.store.LineInfo;
import com.codenvy.ide.texteditor.Buffer;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.ViewportModel;
import com.codenvy.ide.texteditor.gutter.Gutter;
import com.codenvy.ide.texteditor.selection.SelectionModel;
import com.codenvy.ide.util.ListenerRegistrar;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;


/** A renderer for the line numbers in the left gutter. */
public class LineNumberRenderer {

    private static final int NONE = -1;

    private final Buffer buffer;

    private final Gutter leftGutter;

    /**
     * Current editor instance.
     * <p/>
     * Used to track if current file can be edited (i.e. is not readonly).
     * <p/>
     * TODO: add new abstraction to avoid editor passing.
     */
    private final TextEditorViewImpl editor;

    private int previousBottomLineNumber = -1;

    private int previousTopLineNumber = -1;

    private JsoIntegerMap<Element> lineNumberToElementCache;

    private final ViewportModel viewport;

    private final Css css;

    private Resources resources;

    private int activeLineNumber = NONE;

    private int renderedActiveLineNumber = NONE;

    private BreakpointGutterManager breakpointGutterManager;

    private final Array<ListenerRegistrar.Remover> listenerRemovers = Collections.createArray();

    private final SelectionModel.CursorListener cursorListener = new SelectionModel.CursorListener() {
        @Override
        public void onCursorChange(LineInfo lineInfo, int column, boolean isExplicitChange) {
            activeLineNumber = lineInfo.number();
            updateActiveLine();
        }
    };

    private TextEditorViewImpl.ReadOnlyListener readonlyListener = new TextEditorViewImpl.ReadOnlyListener() {
        @Override
        public void onReadOnlyChanged(boolean isReadOnly) {
            updateActiveLine();
        }
    };

    private void updateActiveLine() {
        int lineNumber = this.activeLineNumber;
        if (editor.isReadOnly()) {
            lineNumber = NONE;
        }
        if (lineNumber == renderedActiveLineNumber) {
            return;
        }

        if (renderedActiveLineNumber != NONE) {
            Element renderedActiveLine = lineNumberToElementCache.get(renderedActiveLineNumber);
            if (renderedActiveLine != null) {
                renderedActiveLine.removeClassName(css.activeLineNumber());
                renderedActiveLineNumber = NONE;
            }
        }
        Element newActiveLine = lineNumberToElementCache.get(lineNumber);
        // Add class if it's in the viewport.
        if (newActiveLine != null) {
            newActiveLine.addClassName(css.activeLineNumber());
            renderedActiveLineNumber = lineNumber;
        }
    }

    public void teardown() {
        for (int i = 0, n = listenerRemovers.size(); i < n; i++) {
            listenerRemovers.get(i).remove();
        }
    }

    /** Line number CSS. */
    public interface Css extends TextEditorViewImpl.EditorSharedCss {
        String lineNumber();

        String activeLineNumber();

        String activeline();
    }

    /** Line number resources. */
    public interface Resources extends ClientBundle {
        @Source({"com/codenvy/ide/common/constants.css", "LineNumberRenderer.css", "com/codenvy/ide/api/ui/style.css"})
        Css lineNumberRendererCss();

        @Source("breakpoint.png")
        ImageResource breakpoint();

        @Source("breakpoint-current.gif")
        ImageResource currentBreakpoint();
    }

    LineNumberRenderer(Buffer buffer, Resources res, Gutter leftGutter, ViewportModel viewport, SelectionModel selection,
                       TextEditorViewImpl editor, BreakpointGutterManager breakpointGutterManager) {
        this.buffer = buffer;
        this.leftGutter = leftGutter;
        this.editor = editor;
        this.lineNumberToElementCache = JsoIntegerMap.create();
        this.viewport = viewport;
        this.breakpointGutterManager = breakpointGutterManager;
        this.breakpointGutterManager.setBreakPointRenderer(this);
        this.resources = res;
        this.css = res.lineNumberRendererCss();
        listenerRemovers.add(selection.getCursorListenerRegistrar().add(cursorListener));
        listenerRemovers.add(editor.getReadOnlyListenerRegistrar().add(readonlyListener));
        this.leftGutter.getClickListenerRegistrar().add(new Gutter.ClickListener() {
            @Override
            public void onClick(int y) {
                final int lineNumber = LineNumberRenderer.this.buffer.convertYToLineNumber(y, true);
                LineNumberRenderer.this.breakpointGutterManager.changeBreakPoint(lineNumber);
            }
        });
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

    public void fillOrUpdateLines(int beginLineNumber, int endLineNumber) {
        for (int i = beginLineNumber; i <= endLineNumber; i++) {
            Element lineElement = lineNumberToElementCache.get(i);
            if (lineElement != null) {
                lineNumberToElementCache.erase(i);
                leftGutter.removeUnmanagedElement(lineElement);
            }

            Element element = createElement(i);
            lineNumberToElementCache.put(i, element);
            leftGutter.addUnmanagedElement(element);
        }
    }

    private Element createElement(int lineNumber) {
        Element element;
        if (breakpointGutterManager.isMarkedLine(lineNumber)) {
            Image i = new Image(resources.currentBreakpoint());
            element = (Element)i.getElement();
            element.getStyle().setHeight(buffer.getEditorLineHeight() + CSSStyleDeclaration.Unit.PX);
            element.getStyle().setPosition("absolute");
            element.getStyle().setTop(buffer.convertLineNumberToY(lineNumber) + 2, CSSStyleDeclaration.Unit.PX);
            element.getStyle().setLeft(9, CSSStyleDeclaration.Unit.PX);
            element.setId("breakpoit-toggle-" + (lineNumber + 1));
        } else {
            if (!breakpointGutterManager.isBreakPointExist(lineNumber)) {
                element = Elements.createDivElement(css.lineNumber());
                // Line 0 will be rendered as Line 1
                element.setTextContent(String.valueOf(lineNumber + 1));
                element.getStyle().setTop(buffer.calculateLineTop(lineNumber), CSSStyleDeclaration.Unit.PX);
                if (lineNumber == activeLineNumber) {
                    element.addClassName(css.activeLineNumber());
                    renderedActiveLineNumber = activeLineNumber;
                }
            } else {
                Image i = new Image(resources.breakpoint());
                element = (Element)i.getElement();
                element.getStyle().setHeight(buffer.getEditorLineHeight() + CSSStyleDeclaration.Unit.PX);
                element.getStyle().setPosition("absolute");
                element.getStyle().setTop(buffer.convertLineNumberToY(lineNumber), CSSStyleDeclaration.Unit.PX);
                element.getStyle().setLeft(7, CSSStyleDeclaration.Unit.PX);
                element.setId("breakpoit-toggle-" + (lineNumber + 1));
            }
        }
        return element;
    }

    private void garbageCollectLines(int beginLineNumber, int endLineNumber) {
        for (int i = beginLineNumber; i <= endLineNumber; i++) {
            Element lineElement = lineNumberToElementCache.get(i);
            if (lineElement != null) {
                leftGutter.removeUnmanagedElement(lineElement);
                lineNumberToElementCache.erase(i);
            } else {
                throw new IndexOutOfBoundsException("Tried to garbage collect line number " + i
                                                    + " when it does not exist.");
            }
        }
        if (beginLineNumber <= renderedActiveLineNumber && renderedActiveLineNumber <= endLineNumber) {
            renderedActiveLineNumber = NONE;
        }
    }
}
