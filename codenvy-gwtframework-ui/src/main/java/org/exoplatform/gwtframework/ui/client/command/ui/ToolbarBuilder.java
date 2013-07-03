/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package org.exoplatform.gwtframework.ui.client.command.ui;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.PopupMenuControl;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.command.StatusTextControl;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ToolbarBuilder implements SetToolbarItemsHandler, AddToolbarItemsHandler {

    private HandlerManager eventBus;

    private Toolbar[] toolbars;

    public ToolbarBuilder(HandlerManager eventBus, Toolbar... toolbars) {
        this.eventBus = eventBus;
        this.toolbars = toolbars;
        eventBus.addHandler(SetToolbarItemsEvent.TYPE, this);
        eventBus.addHandler(AddToolbarItemsEvent.TYPE, this);
    }

    public void onSetToolbarItems(SetToolbarItemsEvent event) {
        if (toolbars.length == 0) {
            return;
        }

        Toolbar toolbar = null;
        if (event.getToolbarId() != null) {
            for (Toolbar t : toolbars) {
                if (event.getToolbarId().equals(t.getId())) {
                    toolbar = t;
                    break;
                }
            }
        }

        if (toolbar == null) {
            toolbar = toolbars[0];
        }

        toolbar.clear();

        List<String> leftItems = new ArrayList<String>();
        List<String> rightItems = new ArrayList<String>();

        boolean rightDocking = false;
        for (String id : event.getToolBarItems()) {
            if ("".equals(id)) {
                rightDocking = true;
            } else {
                if (id.startsWith("---")) {
                    if (rightDocking) {
                        rightItems.add(0, id);
                    } else {
                        leftItems.add(id);
                    }
                } else {
                    Control control = getControl(event.getCommands(), id);
                    if (control == null) {
                        continue;
                    }

                    if (rightDocking) {
                        rightItems.add(0, id);
                    } else {
                        leftItems.add(id);
                    }
                }

            }
        }

        addItems(toolbar, leftItems, event.getCommands(), false);
        addItems(toolbar, rightItems, event.getCommands(), true);
        toolbar.hideDuplicatedDelimiters();
        
    }

    private void addItems(Toolbar toolbar, List<String> items, List<Control> controls, boolean right) {
        for (String item : items) {
            if ("---".equals(item)) {
                if (right) {
                    toolbar.addDelimiter(true);
                } else {
                    toolbar.addDelimiter();
                }

                continue;
            }

            Control control = getControl(controls, item);
            if (control != null) {
                if (control instanceof SimpleControl) {
                    addIconButton(toolbar, (SimpleControl)control, right);
                } else if (control instanceof PopupMenuControl) {
                    addPopupMenuButton(toolbar, (PopupMenuControl)control, right);
                } else if (control instanceof StatusTextControl) {
                    addTextButton(toolbar, (StatusTextControl)control, right);
                }
            }
        }
    }

    private void addIconButton(Toolbar toolbar, SimpleControl simpleControl, boolean rightDocking) {
        IconButtonControl iconButtonControl = new IconButtonControl(eventBus, simpleControl, toolbar);
        toolbar.addItem(iconButtonControl, rightDocking);
    }

    private void addPopupMenuButton(Toolbar toolbar, PopupMenuControl popupMenuControl, boolean rightDocking) {
        PopupMenuButtonControl popupMenuButtonControl = new PopupMenuButtonControl(eventBus, popupMenuControl, toolbar);
        toolbar.addItem(popupMenuButtonControl, rightDocking);
    }

    private void addTextButton(Toolbar toolbar, StatusTextControl statusTextControl, boolean rightDocking) {
        TextButtonControl statusText = new TextButtonControl(eventBus, statusTextControl, toolbar);
        toolbar.addItem(statusText, rightDocking);
    }

    private Control getControl(List<Control> controls, String controlId) {
        for (Control<?> c : controls) {
            if (c.getId().equals(controlId)) {
                return c;
            }
        }

        return null;
    }

    @Override
    public void onAddToolbarItems(AddToolbarItemsEvent event) {
        toolbars[0].addItem(event.getIconButton(), true);
    }

}
