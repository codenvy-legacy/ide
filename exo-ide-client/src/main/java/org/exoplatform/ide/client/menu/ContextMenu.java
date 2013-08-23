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
package org.exoplatform.ide.client.menu;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.command.ui.MenuItemControl;
import org.exoplatform.gwtframework.ui.client.menu.*;
import org.exoplatform.ide.client.framework.module.IDE;

import java.util.*;

/**
 * Custom context menu, which consists of {@link Control} list.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 23, 2012 11:45:52 AM anya $
 */
public class ContextMenu implements ItemSelectedHandler, CloseMenuHandler {
    private final String ID = "eXoIDEContextMenu";

    /** Instance of context menu. */
    private static ContextMenu contextMenu;

    /** Lock layer, when context menu is shown. */
    private MenuLockLayer lockLayer;

    /** Context menu. */
    private PopupMenu popupMenu;

    private CloseMenuHandler closeHandler;

    private ContextMenu() {
    }

    /** @return {@link ContextMenu} */
    public static ContextMenu get() {
        if (contextMenu == null) {
            contextMenu = new ContextMenu();
        }
        return contextMenu;
    }


    private LinkedHashMap<String, MenuItem> menuItems = new LinkedHashMap<String, MenuItem>();

    private HashMap<MenuItem, SimpleControl> menuItemControls = new HashMap<MenuItem, SimpleControl>();

    /**
     * Add {@link MenuItem} to popup menu.
     *
     * @param control
     */
    private void addControl(SimpleControl control) {
        String parts[] = control.getId().split("/");
        String itemPath = parts[0];

        MenuItem parentItem = null;
        for (int i = 1; i < parts.length; i++) {
            itemPath += "/" + parts[i];
            if (itemPath.equals(control.getId())) {
                if (parentItem == null) {
                    PopupMenuItem popupMenuItem = new PopupMenuItem(control.getIcon(), control.getTitle());
                    menuItems.put(itemPath, popupMenuItem);
                    menuItemControls.put(popupMenuItem, control);
                    new MenuItemControl(IDE.getInstance().eventBus(), popupMenuItem, control);
                } else {
                    MenuItem menuItem = parentItem.addItem(control.getIcon(), control.getTitle());
                    menuItemControls.put(menuItem, control);
                    new MenuItemControl(IDE.getInstance().eventBus(), menuItem, control);
                }
            } else {
                if (parentItem == null) {
                    if (menuItems.containsKey(itemPath)) {
                        parentItem = menuItems.get(itemPath);
                    } else {
                        PopupMenuItem item = new PopupMenuItem(control.getTitle());
                        menuItems.put(itemPath, item);
                        parentItem = item;
                    }
                } else {
                    // search existed item in parent item
                    MenuItem item = null;
                    for (MenuItem menuItem : parentItem.getItems()) {
                        if (menuItem.getTitle() != null && menuItem.getTitle().equals(parts[i])) {
                            item = menuItem;
                            break;
                        }
                    }

                    if (item != null) {
                        parentItem = item;
                    } else {
                        item = parentItem.addItem(parts[i]);
                    }
                }
            }
        }
    }

    /**
     * Groups items in the list of {@link MenuItem}
     *
     * @param menuItems
     * @return
     */
    private ArrayList<MenuItem> groupItems(Collection<MenuItem> menuItems) {
        LinkedHashMap<String, ArrayList<MenuItem>> groups = new LinkedHashMap<String, ArrayList<MenuItem>>();

        for (MenuItem menuItem : menuItems) {
            SimpleControl control = menuItemControls.get(menuItem);
            String groupName = control == null ? "" : control.getGroupName() == null ? "" : control.getGroupName();
            ArrayList<MenuItem> groupList = groups.get(groupName);
            if (groupList == null) {
                groupList = new ArrayList<MenuItem>();
                groups.put(groupName, groupList);
            }
            groupList.add(menuItem);
        }

        ArrayList<MenuItem> result = new ArrayList<MenuItem>();
        for (ArrayList<MenuItem> itemList : groups.values()) {
            if (result.size() > 0 && itemList.size() > 0) {
                result.add(new PopupMenuItem(null));
            }

            for (MenuItem item : itemList) {
                result.add(item);
            }
        }

        return result;
    }

    /**
     * Recursively group the list of {@link MenuItem}
     *
     * @param menuItems
     */
    private void groupChildrenRecursively(List<MenuItem> menuItems) {
        if (menuItems == null) {
            return;
        }

        List<MenuItem> groupped = groupItems(menuItems);
        menuItems.clear();
        menuItems.addAll(groupped);

        for (MenuItem item : menuItems) {
            int to = item.getItems().size();
            groupChildrenRecursively(item.getItems());
        }
    }

    /**
     * Show context menu.
     *
     * @param commands
     *         list of commands to show in context menu
     * @param x
     *         context menu left coordinate
     * @param y
     *         context menu top coordinate
     */
    @SuppressWarnings("rawtypes")
    public void show(List<Control> commands, int x, int y, CloseMenuHandler closeHandler) {
        this.closeHandler = closeHandler;
        menuItems.clear();
        menuItemControls.clear();

        for (Control control : commands) {
            if (control instanceof SimpleControl && ((SimpleControl)control).isShowInContextMenu()) {
                if (!control.isVisible()) {
                    continue;
                }

                addControl((SimpleControl)control);
            }
        }

        List<MenuItem> itemList = new ArrayList<MenuItem>();
        itemList.addAll(menuItems.values());
        groupChildrenRecursively(itemList);

        showPopupMenu(itemList, x, y);
    }

    HandlerRegistration windowResizeHandler;

    HandlerRegistration previewNativeEventHandler;

    private void showPopupMenu(List<MenuItem> itemList, final int x, final int y) {
        lockLayer = new MenuLockLayer(this);
        popupMenu = new PopupMenu(itemList, lockLayer, this);
        popupMenu.getElement().setId(ID);
        popupMenu.addDomHandler(new ContextMenuHandler() {
            @Override
            public void onContextMenu(ContextMenuEvent event) {
                event.stopPropagation();
                event.preventDefault();
            }
        }, ContextMenuEvent.getType());

        lockLayer.add(popupMenu);
        popupMenu.getElement().getStyle().setTop(y, Unit.PX);
        popupMenu.getElement().getStyle().setLeft(x, Unit.PX);

        popupMenu.getElement().getStyle().setVisibility(Visibility.HIDDEN);

        windowResizeHandler = Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                closePopupMenu();
            }
        });

        previewNativeEventHandler = Event.addNativePreviewHandler(new NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(NativePreviewEvent event) {
                if (event.getTypeInt() == Event.ONKEYDOWN) {
                    Event e = Event.as(event.getNativeEvent());
                    if (DOM.eventGetKeyCode(e) == 27) {
                        e.stopPropagation();
                        e.preventDefault();
                        closePopupMenu();
                    }
                }
            }
        });

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                int absLeft = popupMenu.getAbsoluteLeft();
                int absTop = popupMenu.getAbsoluteTop();
                int width = popupMenu.getOffsetWidth();
                int height = popupMenu.getOffsetHeight();
                int windowWidth = Window.getClientWidth();
                int windowHeight = Window.getClientHeight();

                int left = x;
                if (absLeft + width > windowWidth) {
                    if (absLeft >= width) {
                        left = absLeft - width;
                    } else {
                        int dx = absLeft + width - windowWidth;
                        left -= dx;
                    }
                }

                int top = y;
                if (absTop + height > windowHeight) {
                    if (absTop >= height) {
                        top = absTop - height;
                    } else {
                        int dy = absTop + height - windowHeight;
                        top -= dy;
                    }
                }

                popupMenu.getElement().getStyle().setLeft(left, Unit.PX);
                popupMenu.getElement().getStyle().setTop(top, Unit.PX);
                popupMenu.getElement().getStyle().setVisibility(Visibility.VISIBLE);
            }
        });

    }

    /** @see org.exoplatform.gwtframework.ui.client.menu.ItemSelectedHandler#onMenuItemSelected(org.exoplatform.gwtframework.ui.client
     * .menu.MenuItem) */
    @Override
    public void onMenuItemSelected(MenuItem menuItem) {
        closePopupMenu();
    }

    /** @see org.exoplatform.gwtframework.ui.client.menu.CloseMenuHandler#onCloseMenu() */
    @Override
    public void onCloseMenu() {
        closePopupMenu();
    }

    /** Close popup menu. */
    protected void closePopupMenu() {
        if (popupMenu != null) {
            popupMenu.removeFromParent();
            popupMenu = null;
        }

        if (lockLayer != null) {
            lockLayer.removeFromParent();
            lockLayer = null;
        }

        if (closeHandler != null) {
            closeHandler.onCloseMenu();
            closeHandler = null;
        }

        if (windowResizeHandler != null) {
            windowResizeHandler.removeHandler();
            windowResizeHandler = null;
        }

        if (previewNativeEventHandler != null) {
            previewNativeEventHandler.removeHandler();
            previewNativeEventHandler = null;
        }

    }
}
