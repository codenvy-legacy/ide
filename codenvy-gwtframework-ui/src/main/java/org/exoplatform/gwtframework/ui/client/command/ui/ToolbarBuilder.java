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
        toolbars[0].addItem(event.getWidget(), event.isRightDocking());
    }

}
