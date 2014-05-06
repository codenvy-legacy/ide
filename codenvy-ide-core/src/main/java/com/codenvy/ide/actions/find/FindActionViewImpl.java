/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.actions.find;

import elemental.dom.Element;
import elemental.dom.Node;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.Presentation;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.texteditor.codeassistant.AutocompleteUiController;
import com.codenvy.ide.texteditor.codeassistant.CodeAssistantImpl;
import com.codenvy.ide.toolbar.PresentationFactory;
import com.codenvy.ide.toolbar.Toolbar;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.util.dom.Elements;
import com.codenvy.ide.util.input.KeyMapUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;

import org.vectomatic.dom.svg.ui.SVGImage;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public class FindActionViewImpl extends PopupPanel implements FindActionView {
    private static FindActionViewImplUiBinder ourUiBinder = GWT.create(FindActionViewImplUiBinder.class);
    private final AutocompleteUiController.Css css;
    private final PresentationFactory          presentationFactory;
    private final SimpleList.ListEventDelegate<Action> eventDelegate    = new SimpleList.ListEventDelegate<Action>() {
        @Override
        public void onListItemClicked(Element listItemBase, Action itemData) {
            list.getSelectionModel().setSelectedItem(itemData);
        }

        @Override
        public void onListItemDoubleClicked(Element listItemBase, Action itemData) {
            delegate.onActionSelected(itemData);
        }
    };
    private final SimpleList.ListItemRenderer<Action>  listItemRenderer =
            new SimpleList.ListItemRenderer<Action>() {
                @Override
                public void render(Element itemElement, Action itemData) {
                    TableCellElement icon = Elements.createTDElement(css.proposalIcon());
                    TableCellElement label = Elements.createTDElement(css.proposalLabel());
                    TableCellElement group = Elements.createTDElement(css.proposalGroup());

                    Presentation presentation = presentationFactory.getPresentation(itemData);
                    itemData.update(new ActionEvent("find_action", presentation, actionManager, 0));
                    if (presentation.getIcon() != null) {
                        Image image = new Image(presentation.getIcon());
                        icon.appendChild((Node)image.getElement());
                    } else if (presentation.getSVGIcon() != null) {
                        SVGImage image = new SVGImage(presentation.getSVGIcon());
                        image.getElement().setAttribute("class", Toolbar.RESOURCES.toolbar().iconButtonIcon());
                        image.getElement().getStyle().setMargin(0, Style.Unit.PX);
                        icon.appendChild((Node)image.getElement());
                    }
                    String hotKey = KeyMapUtil.getShortcutText(keyBindingAgent.getKeyBinding(actionManager.getId(itemData)));
                    if (hotKey == null) {
                        hotKey = "&nbsp;";
                    } else {
                        hotKey =
                                "<nobr>&nbsp;[" + hotKey + "]&nbsp;</nobr>";
                    }
                    label.setInnerHTML(presentation.getText() + hotKey);
                    if (!presentation.isEnabled() || !presentation.isVisible()) {
                        itemElement.getStyle().setProperty("opacity", "0.6");
                    }
                    String groupName = actions.get(itemData);
                    if (groupName != null) {
                        group.setInnerHTML(groupName);
                    }
                    itemElement.appendChild(icon);
                    itemElement.appendChild(label);
                    itemElement.appendChild(group);
                }

                @Override
                public Element createElement() {
                    return Elements.createTRElement();
                }
            };
    @UiField
    TextBox  nameField;
    @UiField
    CheckBox includeNonMenu;
    private ActionDelegate      delegate;
    private Resources           resources;
    private KeyBindingAgent     keyBindingAgent;
    private ActionManager       actionManager;
    private PopupPanel          popupPanel;
    private SimpleList<Action>  list;
    private Map<Action, String> actions;

    @Inject
    public FindActionViewImpl(com.codenvy.ide.Resources resources, KeyBindingAgent keyBindingAgent, ActionManager actionManager) {
        this.resources = resources;
        this.keyBindingAgent = keyBindingAgent;
        this.actionManager = actionManager;
        this.presentationFactory = new PresentationFactory();
        css = CodeAssistantImpl.res.autocompleteComponentCss();
        css.ensureInjected();
        DockLayoutPanel rootElement = ourUiBinder.createAndBindUi(this);
        setWidget(rootElement);
        setAutoHideEnabled(true);
        setAnimationEnabled(true);
        popupPanel = new PopupPanel();
        addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                delegate.onClose();
                if (popupPanel.isShowing()) {
                    popupPanel.hide();
                }
            }
        });

        includeNonMenu.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                 delegate.nameChanged(nameField.getText(), event.getValue());
            }
        });
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void focusOnInput() {
        nameField.setFocus(true);
    }

    @Override
    public void show() {
        super.show();
        center();
    }

    @Override
    public String getName() {
        return nameField.getText();
    }

    @Override
    public void showActions(Map<Action, String> actions) {
        this.actions = actions;
        if (popupPanel.isShowing()) {
            popupPanel.hide();
        }
        popupPanel.clear();
//        DomUtils.preventExcessiveScrollingPropagation((Element)popupPanel.getElement());
        TableElement itemHolder = Elements.createTableElement();
        itemHolder.setClassName(css.items());
        HTML html = new HTML();
        html.setStyleName(css.container());
        html.getElement().appendChild(((com.google.gwt.dom.client.Element)itemHolder));
        popupPanel.add(html);
        list = SimpleList.create((SimpleList.View)popupPanel.getElement().cast(), (Element)html.getElement(), itemHolder,
                                 resources.defaultSimpleListCss(), listItemRenderer, eventDelegate);
        html.getElement().getStyle().setOverflow(Style.Overflow.AUTO);
        ((Element)html.getElement()).getStyle().setProperty("max-height", "200px");
        list.render(new ArrayList<>(actions.keySet()));
        popupPanel.setWidth("400px");

        popupPanel.setPopupPositionAndShow(new PositionCallback() {
            @Override
            public void setPosition(int offsetWidth, int offsetHeight) {
                popupPanel.setPopupPosition(getPopupLeft(), getPopupTop() + getOffsetHeight());
            }
        });
        if (!actions.isEmpty()) {
           list.getSelectionModel().setSelectedItem(0);
        }
    }

    @Override
    public void hideActions() {
        if (popupPanel.isShowing()) {
            popupPanel.hide();
        }
    }

    @Override
    public boolean getCheckBoxState() {
        return includeNonMenu.getValue();
    }

    @UiHandler("nameField")
    void handleKeyDown(KeyDownEvent event) {
        switch (event.getNativeKeyCode()) {
            case KeyCodes.KEY_UP:
                if (popupPanel.isShowing()) {
                    if (list.getSelectionModel().getSelectedIndex() == 0) {
                        list.getSelectionModel().setSelectedItem(list.getSelectionModel().size() - 1);
                    } else {
                        list.getSelectionModel().selectPrevious();
                    }
                }
                return;
            case KeyCodes.KEY_DOWN:
                if (popupPanel.isShowing()) {
                    if (list.getSelectionModel().getSelectedIndex() == list.getSelectionModel().size() - 1) {
                        list.getSelectionModel().setSelectedItem(0);
                    } else {
                        list.getSelectionModel().selectNext();
                    }
                }
                return;
            case KeyCodes.KEY_ENTER:
                if (popupPanel.isShowing()) {
                    delegate.onActionSelected(list.getSelectionModel().getSelectedItem());
                }
                return;
            case KeyCodes.KEY_ESCAPE:
                hide();
                return;
        }

        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                delegate.nameChanged(nameField.getText(), includeNonMenu.getValue());
            }
        });
    }

    interface FindActionViewImplUiBinder
            extends UiBinder<DockLayoutPanel, FindActionViewImpl> {
    }
}