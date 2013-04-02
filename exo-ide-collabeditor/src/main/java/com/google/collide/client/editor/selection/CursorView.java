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

package com.google.collide.client.editor.selection;

import elemental.css.CSSStyleDeclaration;
import elemental.html.Element;

import com.codenvy.ide.client.util.CssUtils;
import com.codenvy.ide.client.util.Elements;
import com.codenvy.ide.client.util.HoverController;
import com.google.collide.client.AppContext;
import com.google.collide.client.code.Participant;
import com.google.collide.mvp.CompositeView;
import com.google.collide.mvp.UiComponent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;

/** A presenter and anchor renderer for a cursor. */
public class CursorView extends UiComponent<CursorView.View> {

    /** Static factory method for obtaining an instance of the CursorView. */
    public static CursorView create(AppContext appContext, boolean isLocal) {
        View view = new View(appContext.getResources(), isLocal);
        return new CursorView(view, isLocal);
    }

    public interface Css extends CssResource {
        String caret();

        String root();

        String block();

        String name();

        String top();
    }

    public interface Resources extends ClientBundle {
        @Source({"CursorView.css", "com/google/collide/client/editor/constants.css"})
        Css workspaceEditorCursorCss();
    }

    static class View extends CompositeView<ViewEvents> {

        public static final int UNHOVER_DELAY = 5 * 1000;

        private final Css     css;
        private       Element caret;

        private Element header;

        private HoverController hoverController;

        private Element top;

        private View(Resources res, boolean isLocal) {
            this.css = res.workspaceEditorCursorCss();
            setElement(createElement(isLocal));
        }

        private Element createElement(boolean isLocal) {
            caret = Elements.createDivElement(css.caret());

            Element root = Elements.createDivElement(css.root());
            root.appendChild(caret);
            root.getStyle().setZIndex(isLocal ? 1 : 0);
            if (!isLocal) {
                hoverController = new HoverController();
                header = Elements.createDivElement(css.name());
                header.getStyle().setVisibility(CSSStyleDeclaration.Visibility.HIDDEN);
                top = Elements.createDivElement(css.top());
                root.appendChild(header);
                root.appendChild(top);
                hoverController.addPartner(header);
                hoverController.addPartner(root);
                hoverController.addPartner(top);
                hoverController.setUnhoverDelay(UNHOVER_DELAY);
                hoverController.setHoverListener(new HoverController.HoverListener() {
                    @Override
                    public void onHover() {
                        getDelegate().onHover(header);
                    }
                });
                hoverController.setUnhoverListener(new HoverController.UnhoverListener() {
                    @Override
                    public void onUnhover() {
                        getDelegate().onUnHover(header);
                    }
                });
            }
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
            if (header != null) {
                header.getStyle().setBackgroundColor(color);
                top.getStyle().setBackgroundColor(color);
            }
        }

        private void setBlockMode(boolean isBlockMode) {
            if (isBlockMode) {
                caret.addClassName(css.block());
            } else {
                caret.removeClassName(css.block());
            }
        }
    }

    interface ViewEvents {
        // TODO: onHover, so we can show the label
        void onHover(Element display);

        void onUnHover(Element display);
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

    public void setParticipant(final Participant participant) {
        getView().setDelegate(new ViewEvents() {
            @Override
            public void onHover(Element display) {
                display.setTextContent(participant.getDisplayName());
                display.getStyle().setVisibility(CSSStyleDeclaration.Visibility.VISIBLE);
            }

            @Override
            public void onUnHover(Element display) {
                display.setInnerHTML("");
                display.getStyle().setVisibility(CSSStyleDeclaration.Visibility.HIDDEN);
            }
        });
    }
}
