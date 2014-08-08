/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.toolbar;

import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ActionGroup;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.Presentation;
import com.codenvy.ide.api.action.Separator;
import com.codenvy.ide.api.action.ToggleAction;
import com.codenvy.ide.api.keybinding.KeyBindingAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.util.input.KeyMapUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * PopupMenu is visual component represents all known Popup Menu.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 */

public class PopupMenu extends Composite {

    private static final PopupResources POPUP_RESOURCES = GWT.create(PopupResources.class);

    static {
        POPUP_RESOURCES.popup().ensureInjected();
    }

    private final ActionGroup           actionGroup;
    private final ActionManager         actionManager;
    private final String                place;
    /** Working variable is needs to indicate when PopupMenu has at list one MenuItem with selected state. */
    private       boolean               hasCheckedItems;
    /** Callback uses for notify Parent Menu when menu item is selecred. */
    private       ActionSelectedHandler actionSelectedHandler;
    /**
     * Lock layer uses as root for displaying this PopupMenu and uses for locking screen and hiding menu when user just clicked outside
     * menu.
     */
    private       MenuLockLayer         lockLayer;
    /** Contains opened sub Popup Menu. */
    private       PopupMenu             openedSubPopup;
    /** Contains HTML element ( <TR> ) which is hovered for the current time. */
    private       Element               hoveredTR;
    /**
     * Working variable.
     * PopupMenu panel.
     */
    private       SimplePanel           popupMenuPanel;
    /** Working variable. Special table uses for handling mouse events. */
    private       PopupMenuTable        table;
    private       PresentationFactory   presentationFactory;
    private       KeyBindingAgent       keyBindingAgent;
    /**
     * Prefix to be appended to the ID for each menu item.
     * This is debug feature.
     */
    private       String                itemIdPrefix;
    private       Array<Action>         list;
    private Timer openSubPopupTimer  = new Timer() {
        @Override
        public void run() {
            openSubPopup(hoveredTR);
        }
    };
    private Timer closeSubPopupTimer = new Timer() {
        @Override
        public void run() {
            if (openedSubPopup != null) {
                openedSubPopup.closePopup();
                openedSubPopup = null;
            }

        }
    };


    /**
     * Create PopupMenu
     *
     * @param lockLayer
     *         - lock layer, uses as rot for attaching this popup menu.
     * @param actionSelectedHandler
     *         - callback, uses for notifying parent menu when menu item is selected.
     */
    public PopupMenu(ActionGroup actionGroup, ActionManager actionManager, String place, PresentationFactory presentationFactory,
                     MenuLockLayer lockLayer, ActionSelectedHandler actionSelectedHandler, KeyBindingAgent keyBindingAgent,
                     String itemIdPrefix) {
        this.actionGroup = actionGroup;
        this.actionManager = actionManager;
        this.place = place;
        this.presentationFactory = presentationFactory;
        this.keyBindingAgent = keyBindingAgent;
        this.itemIdPrefix = itemIdPrefix;

        list = Collections.createArray();
        Utils.expandActionGroup(actionGroup, list, presentationFactory, place, actionManager);

        this.lockLayer = lockLayer;
        this.actionSelectedHandler = actionSelectedHandler;

        popupMenuPanel = new SimplePanel();
        initWidget(popupMenuPanel);

        popupMenuPanel.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                closeSubPopupTimer.cancel();

                PopupMenu.this.setStyleNormal(hoveredTR);
                hoveredTR = null;
            }
        }, MouseOutEvent.getType());

        popupMenuPanel.setStyleName(POPUP_RESOURCES.popup().popupMenuMain());

        hasCheckedItems = hasCheckedItems();

        redraw();
    }

    private boolean isRowEnabled(Element tr) {
        if (tr == null) {
            return false;
        }

        String index = DOM.getElementAttribute(tr, "item-index");
        if (index == null || "".equals(index)) {
            return false;
        }

        String enabled = DOM.getElementAttribute(tr, "item-enabled");
        if (enabled == null || "".equals(enabled) || "false".equals(enabled)) {
            return false;
        }

        int itemIndex = Integer.parseInt(index);
        Action menuItem = list.get(itemIndex);
        return presentationFactory.getPresentation(menuItem).isEnabled();

    }

    /** Close this Popup Menu. */
    public void closePopup() {
        if (openedSubPopup != null) {
            openedSubPopup.closePopup();
        }
        removeFromParent();
    }

    /** Render Popup Menu component. */
    private void redraw() {
        String idPrefix = itemIdPrefix;
        if (idPrefix == null) {
            idPrefix = "";
        } else {
            idPrefix += "/";
        }

        table = new PopupMenuTable();
        table.setStyleName(POPUP_RESOURCES.popup().popupMenuTable());
        table.setCellPadding(0);
        table.setCellSpacing(0);
        DOM.setElementAttribute(table.getElement(), "border", "0");

        for (int i = 0; i < list.size(); i++) {
            Action menuItem = list.get(i);

            if (menuItem instanceof Separator) {
                table.getFlexCellFormatter().setColSpan(i, 0, hasCheckedItems ? 5 : 4);
                table.setHTML(i, 0, "<nobr><hr noshade=\"noshade\" style=\"color:#000000;\" size=\"1\"></nobr>");
                table.getCellFormatter().setStyleName(i, 0, POPUP_RESOURCES.popup().popupMenuDelimiter());
            } else {
                Presentation presentation = presentationFactory.getPresentation(menuItem);

                if (presentation.getSVGIcon() != null) {
                    table.setWidget(i, 0, new SVGImage(presentation.getSVGIcon()));
                } else {
                    Image image = null;
                    if (presentation.getIcon() != null) {
                        image = new Image(presentation.getIcon());
                    }
                    table.setWidget(i, 0, image);
                }
                table.getCellFormatter().setStyleName(i, 0,
                                                      presentation.isEnabled() ? POPUP_RESOURCES.popup().popupMenuIconField()
                                                                               : POPUP_RESOURCES.popup().popupMenuIconFieldDisabled());

                int work = 1;

                if (hasCheckedItems && menuItem instanceof ToggleAction) {
                    String checkImage;
                    ToggleAction toggleAction = (ToggleAction)menuItem;
                    ActionEvent e = new ActionEvent(place, presentationFactory.getPresentation(toggleAction), actionManager, 0);
                    if (toggleAction.isSelected(e)) {
                        table.setHTML(i, work, AbstractImagePrototype.create(POPUP_RESOURCES.check()).getHTML());
                    }
                    table.getCellFormatter().setStyleName(i, work,
                                                          presentation.isEnabled() ? POPUP_RESOURCES.popup().popupMenuCheckField()
                                                                                   : POPUP_RESOURCES.popup()
                                                                                                    .popupMenuCheckFieldDisabled());
                    work++;

                }

                table.setHTML(i, work, "<nobr id=\"" + idPrefix + presentation.getText() + "\">" + presentation.getText()
                                       + "</nobr>");
                table.getCellFormatter().setStyleName(i, work,
                                                      presentation.isEnabled() ? POPUP_RESOURCES.popup().popupMenuTitleField()
                                                                               : POPUP_RESOURCES.popup().popupMenuTitleFieldDisabled());

                work++;
                String hotKey = KeyMapUtil.getShortcutText(keyBindingAgent.getKeyBinding(actionManager.getId(menuItem)));
                if (hotKey == null) {
                    hotKey = "&nbsp;";
                } else {
                    hotKey =
                            "<nobr>&nbsp;[" + hotKey + "]&nbsp;</nobr>";
                }

                table.setHTML(i, work, hotKey);
                table.getCellFormatter().setStyleName(i, work,
                                                      presentation.isEnabled() ? POPUP_RESOURCES.popup().popupMenuHotKeyField()
                                                                               : POPUP_RESOURCES.popup().popupMenuHotKeyFieldDisabled());

                work++;

                if (menuItem instanceof ActionGroup && !(((ActionGroup)menuItem).canBePerformed() &&
                                                         !Utils.hasVisibleChildren((ActionGroup)menuItem, presentationFactory,
                                                                                   actionManager,
                                                                                   place))) {
                    table.setWidget(i, work, new SVGImage(POPUP_RESOURCES.subMenu()));
                    table.getCellFormatter().setStyleName(i, work,
                                                          presentation.isEnabled() ? POPUP_RESOURCES.popup().popupMenuSubMenuField()
                                                                                   : POPUP_RESOURCES.popup()
                                                                                                    .popupMenuSubMenuFieldDisabled());
                } else {
                    table.getCellFormatter().setStyleName(i, work,
                                                          presentation.isEnabled() ? POPUP_RESOURCES.popup().popupMenuSubMenuField()
                                                                                   : POPUP_RESOURCES.popup()
                                                                                                    .popupMenuSubMenuFieldDisabled());
                }

                work++;

                DOM.setElementAttribute(table.getRowFormatter().getElement(i), "item-index", "" + i);
                DOM.setElementAttribute(table.getRowFormatter().getElement(i), "item-enabled", "" + presentation.isEnabled());

                String actionId = actionManager.getId(menuItem);
                String debugId;
                if (actionId == null) {
                    debugId = idPrefix + menuItem.getTemplatePresentation().getText();
                } else {
                    debugId = idPrefix + actionId;
                }
                UIObject.ensureDebugId(table.getRowFormatter().getElement(i), debugId);
            }

//            Element row = table.getRowFormatter().getElement(i);
//            for (Map.Entry<String, String> attrEntry : menuItem.getAttributes().entrySet()) {
//                row.setAttribute(attrEntry.getKey(), attrEntry.getValue());
//            }
        }

        popupMenuPanel.add(table);
    }

    /** @return true when at list one item from list of menu items has selected state. */
    private boolean hasCheckedItems() {
        for (int i = 0; i < list.size(); i++) {
            Action action = list.get(i);
            if (action instanceof ToggleAction) {

                ActionEvent e = new ActionEvent(place, presentationFactory.getPresentation(action), actionManager, 0);
                if (((ToggleAction)action).isSelected(e)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Handling MouseOut event.
     *
     * @param row
     *         - element to be processed.
     */
    protected void setStyleNormal(Element row) {
        if (row == null) {
            return;
        }

        if (hasCheckedItems) {
            Element iconTD = DOM.getChild(row, 0);
            Element checkTD = DOM.getChild(row, 1);
            Element titleTD = DOM.getChild(row, 2);
            Element hotKeyTD = DOM.getChild(row, 3);
            Element submenuTD = DOM.getChild(row, 4);

            iconTD.setClassName(POPUP_RESOURCES.popup().popupMenuIconField());
            checkTD.setClassName(POPUP_RESOURCES.popup().popupMenuCheckField());
            titleTD.setClassName(POPUP_RESOURCES.popup().popupMenuTitleField());
            hotKeyTD.setClassName(POPUP_RESOURCES.popup().popupMenuHotKeyField());
            submenuTD.setClassName(POPUP_RESOURCES.popup().popupMenuSubMenuField());
        } else {
            Element iconTD = DOM.getChild(row, 0);
            Element titleTD = DOM.getChild(row, 1);
            Element hotKeyTD = DOM.getChild(row, 2);
            Element submenuTD = DOM.getChild(row, 3);

            iconTD.setClassName(POPUP_RESOURCES.popup().popupMenuIconField());
            titleTD.setClassName(POPUP_RESOURCES.popup().popupMenuTitleField());
            hotKeyTD.setClassName(POPUP_RESOURCES.popup().popupMenuHotKeyField());
            submenuTD.setClassName(POPUP_RESOURCES.popup().popupMenuSubMenuField());
        }
    }

    private void setStyleHovered(Element tr) {
        if (hasCheckedItems) {
            Element iconTD = DOM.getChild(tr, 0);
            Element checkTD = DOM.getChild(tr, 1);
            Element titleTD = DOM.getChild(tr, 2);
            Element hotKeyTD = DOM.getChild(tr, 3);
            Element submenuTD = DOM.getChild(tr, 4);

            iconTD.setClassName(POPUP_RESOURCES.popup().popupMenuIconFieldOver());
            checkTD.setClassName(POPUP_RESOURCES.popup().popupMenuCheckFieldOver());
            titleTD.setClassName(POPUP_RESOURCES.popup().popupMenuTitleFieldOver());
            hotKeyTD.setClassName(POPUP_RESOURCES.popup().popupMenuHotKeyFieldOver());
            submenuTD.setClassName(POPUP_RESOURCES.popup().popupMenuSubMenuFieldOver());
        } else {
            Element iconTD = DOM.getChild(tr, 0);
            Element titleTD = DOM.getChild(tr, 1);
            Element hotKeyTD = DOM.getChild(tr, 2);
            Element submenuTD = DOM.getChild(tr, 3);

            iconTD.setClassName(POPUP_RESOURCES.popup().popupMenuIconFieldOver());
            titleTD.setClassName(POPUP_RESOURCES.popup().popupMenuTitleFieldOver());
            hotKeyTD.setClassName(POPUP_RESOURCES.popup().popupMenuHotKeyFieldOver());
            submenuTD.setClassName(POPUP_RESOURCES.popup().popupMenuSubMenuFieldOver());
        }
    }

    /**
     * Handling MouseOver event.
     *
     * @param tr
     *         - element to be processed.
     */
    protected void onRowHovered(Element tr) {
        if (tr == hoveredTR) {
            return;
        }

        setStyleNormal(hoveredTR);
        if (!isRowEnabled(tr)) {
            hoveredTR = null;
            return;
        }

        hoveredTR = tr;
        setStyleHovered(tr);

        int itemIndex = Integer.parseInt(DOM.getElementAttribute(tr, "item-index"));
        Action menuItem = list.get(itemIndex);
        openSubPopupTimer.cancel();
        if (menuItem instanceof ActionGroup && !(((ActionGroup)menuItem).canBePerformed() &&
                                                 !Utils.hasVisibleChildren((ActionGroup)menuItem, presentationFactory, actionManager,
                                                                           place))) {
            openSubPopupTimer.schedule(300);
        } else {
            closeSubPopupTimer.cancel();
            closeSubPopupTimer.schedule(200);
        }
    }

    /**
     * Handle Mouse Click
     *
     * @param tr
     */
    protected void onRowClicked(Element tr) {
        if (!isRowEnabled(tr)) {
            return;
        }

        int itemIndex = Integer.parseInt(DOM.getElementAttribute(tr, "item-index"));
        Action menuItem = list.get(itemIndex);
        if (menuItem instanceof ActionGroup && (!((ActionGroup)menuItem).canBePerformed() &&
                                                Utils.hasVisibleChildren((ActionGroup)menuItem, presentationFactory, actionManager,
                                                                         place))) {
            openSubPopup(tr);
        } else {
            if (actionSelectedHandler != null) {
                actionSelectedHandler.onActionSelected(menuItem);
            }
            ActionEvent e = new ActionEvent(place, presentationFactory.getPresentation(menuItem), actionManager, 0);
            menuItem.actionPerformed(e);
        }
    }

    private void openSubPopup(final Element tableRowElement) {
        if (tableRowElement == null) {
            return;
        }

        int itemIndex = Integer.parseInt(DOM.getElementAttribute(tableRowElement, "item-index"));
        Action menuItem = list.get(itemIndex);

        if (openedSubPopup != null) {
            openedSubPopup.closePopup();
        }

        String idPrefix = itemIdPrefix;
        if (idPrefix != null) {
            idPrefix += "/" + presentationFactory.getPresentation(menuItem).getText();
        }
        openedSubPopup =
                new PopupMenu((ActionGroup)menuItem, actionManager, place, presentationFactory, lockLayer, actionSelectedHandler,
                              keyBindingAgent, idPrefix);

        final int HORIZONTAL_OFFSET = 3;
        final int VERTICAL_OFFSET = 1;

        openedSubPopup.getElement().getStyle().setVisibility(Visibility.HIDDEN);
        lockLayer.add(openedSubPopup, 0, 0);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                int left = getAbsoluteLeft() + getOffsetWidth() - HORIZONTAL_OFFSET;
                int top = tableRowElement.getAbsoluteTop() - lockLayer.getTopOffset() - VERTICAL_OFFSET;

                if (left + openedSubPopup.getOffsetWidth() > Window.getClientWidth()) {
                    if (left > openedSubPopup.getOffsetWidth()) {
                        left = getAbsoluteLeft() - openedSubPopup.getOffsetWidth() + HORIZONTAL_OFFSET;
                    } else {
                        int diff = left + openedSubPopup.getOffsetWidth() - Window.getClientWidth();
                        left -= diff;
                    }
                }

                if (top + openedSubPopup.getOffsetHeight() > Window.getClientHeight()) {
                    if (top > openedSubPopup.getOffsetHeight()) {
                        top = tableRowElement.getAbsoluteTop() - openedSubPopup.getOffsetHeight() + VERTICAL_OFFSET;
                    } else {
                        int diff = top + openedSubPopup.getOffsetHeight() - Window.getClientHeight();
                        top -= diff;
                    }
                }

                openedSubPopup.getElement().getStyle().setLeft(left, Unit.PX);
                openedSubPopup.getElement().getStyle().setTop(top, Unit.PX);
                openedSubPopup.getElement().getStyle().setVisibility(Visibility.VISIBLE);
            }
        });
    }

    interface PopupResources extends ClientBundle {

        @Source({"popup-menu.css", "com/codenvy/ide/api/ui/style.css"})
        Css popup();

        @Source("check.gif")
        ImageResource check();

        @Source("com/codenvy/ide/menu/submenu.svg")
        SVGResource subMenu();
    }

    interface Css extends CssResource {

        String popupMenuSubMenuFieldDisabled();

        String popupMenuHotKeyFieldDisabled();

        String popupMenuTitleField();

        String popupMenuIconField();

        String popupMenuDelimiter();

        String popupMenuIconFieldDisabled();

        String popupMenuIconFieldOver();

        String popupMenuCheckFieldOver();

        String popupMenuCheckField();

        String popupMenuTable();

        String popupMenuSubMenuField();

        String popupMenuMain();

        String popupMenuTitleFieldOver();

        String popupMenuTitleFieldDisabled();

        String popupMenuCheckFieldDisabled();

        String popupMenuHotKeyFieldOver();

        String popupMenuSubMenuFieldOver();

        String popupMenuHotKeyField();
    }

    /** This table uses for handling mouse events. */
    private class PopupMenuTable extends FlexTable {

        public PopupMenuTable() {
            sinkEvents(Event.ONMOUSEOVER | Event.ONCLICK);
        }

        @Override
        public void onBrowserEvent(Event event) {
            Element td = getEventTargetCell(event);

            if (td == null) {
                return;
            }
            Element tr = DOM.getParent(td);

            String index = DOM.getElementAttribute(tr, "item-index");
            if (index == null || "".equals(index)) {
                return;
            }

            switch (DOM.eventGetType(event)) {
                case Event.ONMOUSEOVER:
                    onRowHovered(tr);
                    break;

                case Event.ONCLICK:
                    onRowClicked(tr);
                    event.preventDefault();
                    event.stopPropagation();
                    break;
            }

        }
    }
}
