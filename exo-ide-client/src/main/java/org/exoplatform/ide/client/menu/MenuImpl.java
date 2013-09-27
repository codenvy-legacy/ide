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

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.command.ui.MenuItemControl;
import org.exoplatform.gwtframework.ui.client.menu.MenuBar;
import org.exoplatform.gwtframework.ui.client.menu.MenuItem;
import org.exoplatform.gwtframework.ui.client.menu.PopupMenuItem;
import org.exoplatform.ide.client.framework.module.IDE;

import java.util.*;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MenuImpl extends MenuBar implements Menu {

    public static String[] items = {"File", "Project", "Edit", "View", "Run", "Git", "PaaS", "Window", "Share", "Help"};

    private HashMap<String, List<SimpleControl>> controlsMap = new LinkedHashMap<String, List<SimpleControl>>();

    private boolean topItemsCreated = false;

    private void ensureTopMenuItemsCreates() {
        if (topItemsCreated) {
            return;
        }

        for (String item : items) {
            addItem(item);
        }

        topItemsCreated = true;
    }

    @Override
    public void refresh(List<Control> allControls) {
        ensureTopMenuItemsCreates();

      /*
       * filter controls
       */
        List<SimpleControl> controls = new ArrayList<SimpleControl>();
        for (Control control : allControls) {
            if (control instanceof SimpleControl) {
                controls.add((SimpleControl)control);
            }
        }

      /*
       * group controls and cut groupped from list of controls 
       */
        List<SimpleControl> cutList = new ArrayList<SimpleControl>();
        Map<String, List<SimpleControl>> groups = new LinkedHashMap<String, List<SimpleControl>>();

        for (SimpleControl control : controls) {
            String groupName;
            if (control.getGroupName() != null && !control.getGroupName().isEmpty()) {
                groupName = control.getGroupName();
            } else {
                groupName = "";
            }

            List<SimpleControl> groupList = groups.get(groupName);
            if (groupList == null) {
                groupList = new ArrayList<SimpleControl>();
                groups.put(groupName, groupList);
            }

            groupList.add(control);
            cutList.add(control);
        }

        controls.removeAll(cutList);

      /*
       * 
       */
        for (String groupName : groups.keySet()) {
            if (groupName.isEmpty()) {
                continue;
            }

            List<SimpleControl> groupped = groups.get(groupName);
            for (SimpleControl control : groupped) {
                if (groupped.indexOf(control) == 0) {
                    add(null, control, 0, true);
                }

                MenuItem menuItem = add(null, control, 0, false);
                new MenuItemControl(IDE.eventBus(), menuItem, control);

                if (groupped.indexOf(control) == groupped.size() - 1) {
                    add(null, control, 0, true);
                }
            }
        }

        // fill controls for group ""
        List<SimpleControl> groupped = groups.get("");
        if (groupped != null) {
            for (SimpleControl control : groupped) {
                if (groupped.indexOf(control) == 0) {
                    add(null, control, 0, true);
                }

                //            addGroupped(control, isFirst);
                MenuItem menuItem = add(null, control, 0, false);
                new MenuItemControl(IDE.eventBus(), menuItem, control);

                if (groupped.indexOf(control) == groupped.size() - 1) {
                    add(null, control, 0, true);
                }
            }
        }
    }

    private MenuItem add(MenuItem parent, SimpleControl control, int depth, boolean delimiter) {
        String[] path = control.getId().split("/");

        if (depth == path.length - 1 && delimiter && parent != null) {
            return parent.addItem(null);
        }

        MenuItem item = getItemByTitle(parent, path[depth]);

        if (item == null) {
            if (depth == 0) {
                item = addItem(path[0]);
            } else {
                if (!delimiter) {
                    item = parent.addItem(path[depth]);
                }
            }
        }

        if (depth < path.length - 1) {
            return add(item, control, depth + 1, delimiter);
        } else {
            if (item instanceof PopupMenuItem) {
                if (control.getAttributes().size() > 0) {
                    ((PopupMenuItem)item).setAttributes(control.getAttributes());
                }
            }
            return item;
        }
    }

    private MenuItem getItemByTitle(MenuItem parent, String title) {
        if (parent == null) {
            for (MenuItem item : getItems()) {
                if (title.equals(item.getTitle())) {
                    return item;
                }
            }
        } else {
            for (MenuItem item : parent.getItems()) {
                if (title.equals(item.getTitle())) {
                    return item;
                }
            }
        }

        return null;
    }

}
