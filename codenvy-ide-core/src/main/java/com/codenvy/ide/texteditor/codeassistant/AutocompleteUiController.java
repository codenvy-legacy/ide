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

package com.codenvy.ide.texteditor.codeassistant;

import elemental.css.CSSStyleDeclaration;
import elemental.dom.Node;
import elemental.html.ClientRect;
import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.text.store.anchor.ReadOnlyAnchor;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.api.codeassistant.CompletionProposal;
import com.codenvy.ide.ui.Popup;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.list.SimpleList.View;
import com.codenvy.ide.ui.menu.AutoHideComponent.AutoHideHandler;
import com.codenvy.ide.ui.menu.AutoHideController;
import com.codenvy.ide.ui.menu.PositionController.HorizontalAlign;
import com.codenvy.ide.ui.menu.PositionController.Position;
import com.codenvy.ide.ui.menu.PositionController.Positioner;
import com.codenvy.ide.ui.menu.PositionController.PositionerBuilder;
import com.codenvy.ide.ui.menu.PositionController.VerticalAlign;
import com.codenvy.ide.util.CssUtils;
import com.codenvy.ide.util.dom.DomUtils;
import com.codenvy.ide.util.dom.Elements;
import com.codenvy.ide.util.input.SignalEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;


/** A controller for managing the UI for showing autocomplete proposals. */
public class AutocompleteUiController implements AutocompleteBox {

    /**
     *
     */
    private static final int                                             DELAY_MILLIS     = 2000;
    private final        SimpleList.ListItemRenderer<CompletionProposal> listItemRenderer =
            new SimpleList.ListItemRenderer<CompletionProposal>() {
                @Override
                public void render(Element itemElement, CompletionProposal itemData) {
                    TableCellElement icon = Elements.createTDElement(css.proposalIcon());
                    TableCellElement label = Elements.createTDElement(css.proposalLabel());
                    TableCellElement group = Elements.createTDElement(css.proposalGroup());
                    if (itemData.getImage() != null) {
                        icon.appendChild((Node)itemData.getImage().getElement());
                    }
                    label.setInnerHTML(itemData.getDisplayString());
                    itemElement.appendChild(icon);
                    itemElement.appendChild(label);
                    itemElement.appendChild(group);
                }

                @Override
                public Element createElement() {
                    return Elements.createTRElement();
                }
            };
    private final AutoHideController             autoHideController;
    private final Css                            css;
    private final SimpleList<CompletionProposal> list;
    private final TextEditorViewImpl             editor;
    private final Element                        box;
    private final Element                        container;
    private final Element                        hint;
    private final Resources                      resources;
    private Timer infoTimer = new Timer() {

        @Override
        public void run() {
            if (list.getSelectionModel().getSelectedItem() != null) {
                CompletionProposal item = list.getSelectionModel().getSelectedItem();
                Widget widget = item.getAdditionalProposalInfo();
                showPopup(widget);
            }
        }
    };
    private Events delegate;
    private final SimpleList.ListEventDelegate<CompletionProposal> listDelegate =
            new SimpleList.ListEventDelegate<CompletionProposal>() {


                @Override
                public void onListItemClicked(Element itemElement, CompletionProposal itemData) {
                    Assert.isNotNull(delegate);
                    list.getSelectionModel().setSelectedItem(itemData);
                    infoTimer.cancel();
                    infoTimer.schedule(DELAY_MILLIS);
                }

                @Override
                public void onListItemDoubleClicked(Element listItemBase, CompletionProposal itemData) {
                    Assert.isNotNull(delegate);
                    infoTimer.cancel();
                    delegate.onSelect(itemData);
                }
            };
    /** Will be non-null when the popup is showing */
    private ReadOnlyAnchor anchor;
    /**
     * True to force the layout above the anchor, false to layout below. This
     * should be set when showing from the hidden state. It's used to keep
     * the position consistent while the box is visible.
     */
    private boolean        positionAbove;
    private Positioner     positioner;
    private Popup          infoPopup;

    public AutocompleteUiController(TextEditorViewImpl editor, Resources res) {
        this.editor = editor;
        this.resources = res;
        this.css = res.autocompleteComponentCss();

        box = Elements.createDivElement();
        // Prevent our mouse events from going to the editor
        DomUtils.stopMousePropagation(box);

        TableElement tableElement = Elements.createTableElement();
        tableElement.setClassName(css.items());

        container = Elements.createDivElement(css.container());
        DomUtils.preventExcessiveScrollingPropagation(container);
        container.appendChild(tableElement);
        box.appendChild(container);

        hint = Elements.createDivElement(css.hint());
        CssUtils.setDisplayVisibility2(hint, false);
        box.appendChild(hint);

        list =
                SimpleList.create((View)box, container, tableElement, res.defaultSimpleListCss(), listItemRenderer,
                                  listDelegate);

        autoHideController = AutoHideController.create(box);
        autoHideController.setCaptureOutsideClickOnClose(false);
        autoHideController.setDelay(-1);
        autoHideController.setAutoHideHandler(new AutoHideHandler() {

            @Override
            public void onShow() {
            }

            @Override
            public void onHide() {
                infoTimer.cancel();
            }
        });
        positioner =
                new PositionerBuilder().setPosition(Position.NO_OVERLAP).setVerticalAlign(VerticalAlign.TOP_TOP)
                                       .setHorizontalAlign(HorizontalAlign.RIGHT).buildAnchorPositioner(box);
        infoPopup = Popup.create(resources);
        infoPopup.addPartner(box);
        infoPopup.addPartnerClickTargets(box);
        infoPopup.setDelay(-1);
        infoPopup.setAutoHideHandler(new AutoHideHandler() {

            @Override
            public void onShow() {
            }

            @Override
            public void onHide() {
                autoHideController.removePartner(infoPopup.getView().getElement());
                autoHideController.removePartnerClickTargets(infoPopup.getView().getElement());
            }
        });
    }

    /** @param widget */
    private void showPopup(Widget widget) {
        if (widget == null) {
            infoPopup.hide();
            return;
        }
        if (infoPopup.isShowing()) {
            infoPopup.hide();
        }
        infoPopup.setContentElement((Element)widget.getElement());
        infoPopup.getView().getElement().getStyle().setHeight(box.getClientHeight() + "px");
        infoPopup.show(positioner);
        autoHideController.addPartner(infoPopup.getView().getElement());
        autoHideController.addPartnerClickTargets(infoPopup.getView().getElement());
    }

    @Override
    public boolean isShowing() {
        return autoHideController.isShowing();
    }

    @Override
    public boolean consumeKeySignal(SignalEventEssence signal) {
        Assert.isTrue(isShowing());
        Assert.isNotNull(delegate);

        if ((signal.keyCode == KeyCodes.KEY_TAB) || (signal.keyCode == KeyCodes.KEY_ENTER)) {
            delegate.onSelect(list.getSelectionModel().getSelectedItem());
            return true;
        }

        if (signal.keyCode == KeyCodes.KEY_ESCAPE) {
            delegate.onCancel();
            return true;
        }

        if (signal.type != SignalEvent.KeySignalType.NAVIGATION) {
            return false;
        }

        if ((signal.keyCode == KeyCodes.KEY_DOWN)) {
            if (list.getSelectionModel().getSelectedIndex() == list.getSelectionModel().size() - 1) {
                list.getSelectionModel().setSelectedItem(0);
            } else {
                list.getSelectionModel().selectNext();
            }
            infoTimer.cancel();
            infoTimer.schedule(DELAY_MILLIS);
            return true;
        }

        if (signal.keyCode == KeyCodes.KEY_UP) {
            if (list.getSelectionModel().getSelectedIndex() == 0) {
                list.getSelectionModel().setSelectedItem(list.getSelectionModel().size() - 1);
            } else {
                list.getSelectionModel().selectPrevious();
            }
            infoTimer.cancel();
            infoTimer.schedule(DELAY_MILLIS);
            return true;
        }

        if ((signal.keyCode == KeyCodes.KEY_LEFT) || (signal.keyCode == KeyCodes.KEY_RIGHT)) {
            delegate.onCancel();
            return true;
        }

        if (signal.keyCode == KeyCodes.KEY_PAGEUP) {
            list.getSelectionModel().selectPreviousPage();
            infoTimer.cancel();
            infoTimer.schedule(DELAY_MILLIS);
            return true;
        }

        if (signal.keyCode == KeyCodes.KEY_PAGEDOWN) {
            list.getSelectionModel().selectNextPage();
            infoTimer.cancel();
            infoTimer.schedule(DELAY_MILLIS);
            return true;
        }

        return false;
    }

    @Override
    public void setDelegate(Events delegate) {
        this.delegate = delegate;
    }

    @Override
    public void dismiss() {
        infoTimer.cancel();
        boolean hadFocus = list.hasFocus();
        autoHideController.hide();

        if (anchor != null) {
            editor.getBuffer().removeAnchoredElement(anchor, autoHideController.getView().getElement());
            anchor = null;
        }

        com.codenvy.ide.texteditor.api.FocusManager focusManager = editor.getFocusManager();
        if (hadFocus && !focusManager.hasFocus()) {
            focusManager.focus();
        }
        infoPopup.destroy();
    }

    @Override
    public void positionAndShow(CompletionProposal[] items) {
        this.anchor = editor.getSelection().getCursorAnchor();

        boolean showingFromHidden = !autoHideController.isShowing();
        if (showingFromHidden) {
            list.getSelectionModel().clearSelection();
        }

        final Array<CompletionProposal> itemsToDisplay = JsoArray.<CompletionProposal>create();
        String hintText = null;//items.getHint();
        if (items != null && items.length != 0) {
            for (int i = 0; i < items.length; i++) {
                itemsToDisplay.add(items[i]);
            }
        } else {
            hintText = "No proposals";
        }
        list.render(itemsToDisplay);

        if (list.getSelectionModel().getSelectedItem() == null) {
            list.getSelectionModel().setSelectedItem(0);
            infoTimer.cancel();
            infoTimer.schedule(DELAY_MILLIS);
        }

        if (hintText == null) {
            hint.setTextContent("");
            CssUtils.setDisplayVisibility2(hint, false);
            CssUtils.setDisplayVisibility2(container, true);
        } else {
            hint.setTextContent(hintText);
            CssUtils.setDisplayVisibility2(hint, true);
            CssUtils.setDisplayVisibility2(container, false);
        }

        autoHideController.show();

        editor.getBuffer().addAnchoredElement(anchor, box);

        ensureRootElementWillBeOnScreen(showingFromHidden);
    }

    @Override
    public void showLoader() {
        this.anchor = editor.getSelection().getCursorAnchor();

        boolean showingFromHidden = !autoHideController.isShowing();
        if (showingFromHidden) {
            list.getSelectionModel().clearSelection();
        }

        final Array<CompletionProposal> itemsToDisplay = JsoArray.<CompletionProposal>create();
        String hintText = "Processing...";//items.getHint();

        list.render(itemsToDisplay);

        if (list.getSelectionModel().getSelectedItem() == null) {
            list.getSelectionModel().setSelectedItem(0);
            infoTimer.cancel();
            infoTimer.schedule(DELAY_MILLIS);
        }

        hint.setTextContent(hintText);
        CssUtils.setDisplayVisibility2(hint, true);
        CssUtils.setDisplayVisibility2(container, false);

        autoHideController.show();

        editor.getBuffer().addAnchoredElement(anchor, box);

        ensureRootElementWillBeOnScreen(showingFromHidden);
    }

    private void ensureRootElementWillBeOnScreen(boolean showingFromHidden) {
        // Remove any max-heights so we can get its desired height
        container.getStyle().removeProperty("max-height");
        ClientRect bounds = box.getBoundingClientRect();
        int height = (int)bounds.getHeight();
        int delta = height - (int)container.getBoundingClientRect().getHeight();

        ClientRect bufferBounds = editor.getBuffer().getBoundingClientRect();
        int lineHeight = editor.getBuffer().getEditorLineHeight();
        int lineTop = (int)bounds.getTop() - CssUtils.parsePixels(box.getStyle().getMarginTop());

        int spaceAbove = lineTop - (int)bufferBounds.getTop();
        int spaceBelow = (int)bufferBounds.getBottom() - lineTop - lineHeight;

        if (showingFromHidden) {
            // If it was already showing, we don't adjust the positioning.
            positionAbove = spaceAbove >= css.maxHeight() && spaceBelow < css.maxHeight();
        }

        // Get available height.
        int maxHeight = positionAbove ? spaceAbove : spaceBelow;

        // Restrict to specified height.
        maxHeight = Math.min(maxHeight, css.maxHeight());

        // Fit to content size.
        maxHeight = Math.min(maxHeight, height);

        container.getStyle().setProperty("max-height", (maxHeight - delta) + CSSStyleDeclaration.Unit.PX);

        int marginTop = positionAbove ? -maxHeight : lineHeight;
        box.getStyle().setMarginTop(marginTop, CSSStyleDeclaration.Unit.PX);

        if (showingFromHidden) {
            // Adjust the box horizontal position if it's out of the editor's right bound.
            // If box was already showing, we don't adjust the horizontal positioning to avoid flickering.
            int editorScrollLeft = editor.getBuffer().getScrollLeft();
            int boxLeftPosition = CssUtils.parsePixels(box.getStyle().getLeft()) - editorScrollLeft;
            int boxWidth = (int)bounds.getWidth();
            int editorWidth = editor.getBuffer().getWidth();
            int boxRightOffset = 8; // need for better visibility
            if ((boxLeftPosition + boxWidth) > editorWidth - boxRightOffset) {
                if (editorWidth > boxWidth) {
                    box.getStyle().setLeft(editorWidth + editorScrollLeft - boxWidth - boxRightOffset,
                                           CSSStyleDeclaration.Unit.PX);
                }
            }
        }
    }

    SimpleList<CompletionProposal> getList() {
        return list;
    }

    public interface Resources extends SimpleList.Resources, Popup.Resources {
        @Source("AutocompleteComponent.css")
        Css autocompleteComponentCss();
    }

    public interface Css extends CssResource {
        String cappedProposalLabel();

        String proposalIcon();

        String proposalLabel();

        String proposalGroup();

        String container();

        String items();

        String hint();

        int maxHeight();
    }
}
