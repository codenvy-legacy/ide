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
package org.exoplatform.ide.client.hotkeys.show;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.control.ControlsUpdatedEvent;
import org.exoplatform.ide.client.framework.control.ControlsUpdatedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Presenter for show keyboard shortcuts view.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShowHotKeysPresenter.java May 10, 2012 10:50:59 AM azatsarynnyy $
 */
public class ShowHotKeysPresenter implements ShowHotKeysHandler, ViewClosedHandler, ControlsUpdatedHandler {

    public interface Display extends IsView {
        HasClickHandlers getCloseButton();

        ListGridItem<HotKeyItem> getHotKeyItemListGrid();
    }

    /*
     * Group for keyboard shortcuts, that don't belong to one of defined groups.
     */
    private static final String OTHER_GROUP = org.exoplatform.ide.client.IDE.PREFERENCES_CONSTANT.hotkeysOtherGroup();

    private Display display;

    private List<Control> controls;

    public ShowHotKeysPresenter() {
        IDE.getInstance().addControl(new ShowHotKeysControl());

        IDE.addHandler(ControlsUpdatedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ShowHotKeysEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.hotkeys.show.ShowHotKeysHandler#onShowHotKeys(org.exoplatform.ide.client.hotkeys.show
     * .ShowHotKeysEvent) */
    @Override
    public void onShowHotKeys(ShowHotKeysEvent event) {
        if (display != null) {
            return;
        }

        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    @Override
    public void onControlsUpdated(ControlsUpdatedEvent event) {
        controls = event.getControls();
    }

    public void bindDisplay() {
        display.getCloseButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        fillShortcutListGrid();
    }

    /**
     * Fill shortcut list grid with shortcut items.
     * Choose only SimpleControls from list of controls, create shortcut items from them and add to list.
     * Update value of hotkey list grid.
     */
    private void fillShortcutListGrid() {
        HashMap<String, List<SimpleControl>> groups = new LinkedHashMap<String, List<SimpleControl>>();
        for (Control command : controls) {
            if (command instanceof SimpleControl) {
                if (((SimpleControl)command).getEvent() != null && ((SimpleControl)command).getHotKey() != null) {
                    addCommand(groups, (SimpleControl)command);
                }
            }
        }

        List<HotKeyItem> hotKeys = new ArrayList<HotKeyItem>();
        for (String groupName : groups.keySet()) {
            hotKeys.add(new HotKeyItem(groupName, null, true, groupName));
            List<SimpleControl> commands = groups.get(groupName);
            for (SimpleControl command : commands) {
                hotKeys.add(new HotKeyItem(command, command.getHotKey(), groupName));
            }
        }

        display.getHotKeyItemListGrid().setValue(hotKeys);
    }

    private void addCommand(HashMap<String, List<SimpleControl>> groups, SimpleControl command) {
        String groupName = command.getId();
        if (groupName.indexOf("/") >= 0) {
            groupName = groupName.substring(0, groupName.lastIndexOf("/"));
        } else {
            groupName = OTHER_GROUP;
        }

        List<SimpleControl> commands = groups.get(groupName);
        if (commands == null) {
            commands = new ArrayList<SimpleControl>();
            groups.put(groupName, commands);
        }

        commands.add(command);
    }

}
