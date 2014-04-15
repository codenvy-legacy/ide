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

package com.codenvy.ide.texteditor.gutter;

import elemental.dom.Element;

import com.codenvy.ide.mvp.UiComponent;
import com.codenvy.ide.text.store.anchor.Anchor;
import com.codenvy.ide.texteditor.Buffer;
import com.codenvy.ide.texteditor.ElementManager;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.ViewportModel;
import com.codenvy.ide.texteditor.renderer.Renderer;
import com.codenvy.ide.util.ListenerManager;
import com.codenvy.ide.util.ListenerManager.Dispatcher;
import com.codenvy.ide.util.ListenerRegistrar;


/**
 * A gutter is a slim vertical region adjacent to the text buffer of the editor.
 * For example, line numbers are placed in a gutter to the left of the text
 * buffer.
 * <p/>
 * Overview mode is for gutters that typically are adjacent to the scrollbar.
 * These gutters do not have a one-to-one pixel mapping with the document,
 * instead the entire height of the gutter corresponds to the entire height of
 * the document. These are used to show an overview of some document state
 * independent of the editor's viewport, such as error markers or search
 * results.
 */
public class Gutter extends UiComponent<GutterView> {

    /** @see TextEditorViewImpl#createGutter(boolean, Position, String) */
    public static Gutter create(boolean overviewMode, Position position, String cssClassName, Buffer buffer) {

        GutterView view = new GutterView(overviewMode, position, cssClassName, buffer);
        return new Gutter(overviewMode, view, buffer);
    }

    /** Defines which side of the editor the gutter will be placed */
    public enum Position {
        LEFT, RIGHT
    }

    /** A listener that is called when there is a click in the gutter. */
    public interface ClickListener {
        void onClick(int y);
    }

    interface ViewDelegate {
        void onClick(int gutterY);
    }

    private final ListenerManager<ClickListener> clickListenerManager = ListenerManager.create();

    private final ElementManager elementManager;

    private final boolean overviewMode;

    private Gutter(boolean overviewMode, GutterView gutterView, Buffer buffer) {
        super(gutterView);

        this.overviewMode = overviewMode;

        elementManager = new ElementManager(getView().contentElement, buffer);

        gutterView.setDelegate(new ViewDelegate() {
            @Override
            public void onClick(final int gutterY) {
                clickListenerManager.dispatch(new Dispatcher<Gutter.ClickListener>() {
                    @Override
                    public void dispatch(ClickListener listener) {
                        listener.onClick(convertGutterYToY(gutterY));
                    }
                });
            }
        });
    }

    public void addAnchoredElement(Anchor anchor, Element element) {
        elementManager.addAnchoredElement(anchor, element);
    }

    public void removeAnchoredElement(Anchor anchor, Element element) {
        elementManager.removeAnchoredElement(anchor, element);
    }

    public void addUnmanagedElement(Element element) {
        elementManager.addUnmanagedElement(element);
    }

    public void removeUnmanagedElement(Element element) {
        elementManager.removeUnmanagedElement(element);
    }

    public Element getGutterElement() {
        return getView().getElement();
    }

    public int getWidth() {
        return getView().getWidth();
    }

    public void setWidth(int width) {
        getView().setWidth(width);
    }

    public ListenerRegistrar<ClickListener> getClickListenerRegistrar() {
        return clickListenerManager;
    }

    // not editor-public
    public void handleDocumentChanged(ViewportModel viewport, Renderer renderer) {
        getView().reset();
        elementManager.handleDocumentChanged(viewport, renderer);
    }

    private int convertYToGutterY(int y) {
        // TODO: implement overview mode
        return y;
    }

    private int convertGutterYToY(int gutterY) {
        // TODO: implement overview mode
        return gutterY;
    }
}
