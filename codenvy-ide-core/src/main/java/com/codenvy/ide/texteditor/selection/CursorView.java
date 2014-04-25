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

package com.codenvy.ide.texteditor.selection;

import elemental.css.CSSStyleDeclaration;
import elemental.dom.Element;

import com.codenvy.ide.mvp.CompositeView;
import com.codenvy.ide.mvp.UiComponent;
import com.codenvy.ide.util.CssUtils;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;


/** A presenter and anchor renderer for a cursor. */
public class CursorView extends UiComponent<CursorView.View> {

    /** Static factory method for obtaining an instance of the CursorView. */
    public static CursorView create(Resources resources, boolean isLocal) {
        View view = new View(resources, isLocal);
        return new CursorView(view, isLocal);
    }

    public interface Css extends CssResource {
        String caret();

        String root();

        String block();
    }

    public interface Resources extends ClientBundle {
        @Source({"CursorView.css", "com/codenvy/ide/api/ui/style.css"})
        Css workspaceEditorCursorCss();
    }

    static class View extends CompositeView<ViewEvents> {
        private final Css css;

        private Element caret;

        private View(Resources res, boolean isLocal) {
            this.css = res.workspaceEditorCursorCss();
            setElement(createElement(isLocal));
        }

        private Element createElement(boolean isLocal) {
            caret = Elements.createDivElement(css.caret());

            Element root = Elements.createDivElement(css.root());
            root.appendChild(caret);
            root.getStyle().setZIndex(isLocal ? 1 : 0);

            return root;
        }

        private boolean isCaretVisible() {
            return caret.getStyle().getVisibility().equals(CSSStyleDeclaration.Visibility.VISIBLE);
        }

        private void setCaretVisible(boolean visible) {
            caret.getStyle().setVisibility(
                    visible ? CSSStyleDeclaration.Visibility.VISIBLE : CSSStyleDeclaration.Visibility.HIDDEN);
        }

        private void setColor(String color) {
            caret.getStyle().setBackgroundColor(color);
        }

        private void setBlockMode(boolean isBlockMode) {
            if (isBlockMode) {
                Elements.addClassName(css.block(), caret);
            } else {
                Elements.removeClassName(css.block(), caret);
            }
        }
    }

    interface ViewEvents {
        // TODO: onHover, so we can show the label
    }

    private static final int CARET_BLINK_PERIOD_MS = 500;

    private final Timer caretBlinker = new Timer() {
        @Override
        public void run() {
            getView().setCaretVisible(!getView().isCaretVisible());
        }
    };

    private final boolean isLocal;

    private boolean isVisible = true;

    private CursorView(View view, boolean isLocal) {
        super(view);

        this.isLocal = isLocal;
    }

    public Element getElement() {
        return getView().getElement();
    }

    public void setVisibility(boolean isVisible) {
        this.isVisible = isVisible;

      /*
       * Use display-based visibility since visibility-based visibility is used
       * for blinking
       */
        CssUtils.setDisplayVisibility2(getView().getElement(), isVisible);

        if (isLocal) {
            // Blink the local cursor
            if (isVisible) {
                caretBlinker.scheduleRepeating(CARET_BLINK_PERIOD_MS);
            } else {
                caretBlinker.cancel();
            }
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setColor(String color) {
        getView().setColor(color);
    }

    /**
     * @param isBlockMode
     *         If true, change the cursor into a block that covers the entire
     *         character.
     */
    public void setBlockMode(boolean isBlockMode) {
        getView().setBlockMode(isBlockMode);
    }

    void forceSolidBlinkState() {

        if (!isLocal) {
            return;
        }

        getView().setCaretVisible(true);
        caretBlinker.scheduleRepeating(CARET_BLINK_PERIOD_MS);
    }
}
