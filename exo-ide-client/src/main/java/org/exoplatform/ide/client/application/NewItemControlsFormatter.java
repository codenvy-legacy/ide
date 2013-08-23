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
package org.exoplatform.ide.client.application;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.ControlsFormatter;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.operation.createfile.CreateNewFileEvent;
import org.exoplatform.ide.client.operation.createfile.NewFileControl;
import org.exoplatform.ide.client.operation.createfile.NewItemPopupToolbarControl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Formatter to sort controls from "File/New" group.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class NewItemControlsFormatter implements ControlsFormatter {
    private List<String> controlIdsOrder;

    /** Initialize the order of the controls in menu "New". */
    private void initControlsOrder() {
        controlIdsOrder = new ArrayList<String>();
        controlIdsOrder.add("File/New/Create Folder...");

        controlIdsOrder.add("File/New/New TEXT");
        controlIdsOrder.add("File/New/New OpenSocial Gadget");
        controlIdsOrder.add("File/New/New XML");
        controlIdsOrder.add("File/New/New Java Script");
        controlIdsOrder.add("File/New/New HTML");
        controlIdsOrder.add("File/New/New CSS");

        controlIdsOrder.add("File/New/New REST Service");
        controlIdsOrder.add("File/New/New POGO");
        controlIdsOrder.add("File/New/New Template");
        controlIdsOrder.add("File/New/New Data Object");
        controlIdsOrder.add("File/New/New Package");
        controlIdsOrder.add("File/New/New Java Class");
        controlIdsOrder.add("File/New/New JSP File");
        controlIdsOrder.add("File/New/New Ruby File");
        controlIdsOrder.add("File/New/New PHP File");
        controlIdsOrder.add("File/New/New Python File");
        controlIdsOrder.add("File/New/New YAML File");

        controlIdsOrder.add("File/New/Create File From Template...");

    }

    public NewItemControlsFormatter() {
        initControlsOrder();
    }

    /** @see org.exoplatform.ide.client.framework.control.ControlsFormatter#format(java.util.List) */
    public void format(List<Control> controls) {
        List<Control> newItemControls = sortNewItemsControls(controls);
        // Remove new items controls:
        controls.removeAll(newItemControls);
        // Add sorted items controls:
        controls.addAll(newItemControls);

        createNewItemGroup(controls);
        fillNewItemPopupControl(controls);
    }

    /**
     * Sort new items controls and return them.
     *
     * @param controls
     *         all controls
     * @return sorted only new item controls
     */
    private List<Control> sortNewItemsControls(List<Control> controls) {
        List<Control> newItemControls = new ArrayList<Control>();
        for (Control control : controls) {
            if (control.getId().startsWith("File/New/")) {
                newItemControls.add(control);
            }
        }

        Collections.sort(newItemControls, controlComparator);
        return newItemControls;
    }

    /**
     * Fill new item popup control with sub controls.
     *
     * @param controls
     */
    private void fillNewItemPopupControl(List<Control> controls) {
        NewItemPopupToolbarControl popup = null;
        for (Control control : controls) {
            if (NewItemPopupToolbarControl.ID.equals(control.getId())) {
                popup = (NewItemPopupToolbarControl)control;
            }
        }

        if (popup == null) {
            return;
        }

        for (Control control : controls) {
            if (control.getId().startsWith("File/New/") && control instanceof SimpleControl) {
                popup.getCommands().add((SimpleControl)control);
            }
        }
    }

    /** Comparator for items order. */
    private Comparator<Control> controlComparator = new Comparator<Control>() {
        public int compare(Control control1, Control control2) {
            if (!control1.getId().startsWith("File/New/") || !control2.getId().startsWith("File/New/")) {
                return 0;
            }

            Integer index1 = controlIdsOrder.indexOf(control1.getId());
            Integer index2 = controlIdsOrder.indexOf(control2.getId());

            // If item is not found in order list, then put it at the end of the list
            if (index2 == -1)
                return -1;
            if (index1 == -1)
                return 1;

            return index1.compareTo(index2);
        }
    };

    /** @param controls */
    private void createNewItemGroup(List<Control> controls) {
        while (true) {
            NewItemControl control = getNewItemControl(controls);
            if (control == null) {
                break;
            }

            int position = controls.indexOf(control);

            NewFileControl command = null;
            if (control.getMimeType() == null) {
                if (control.getNormalImage() != null) {
                    command =
                            new NewFileControl(control.getId(), control.getTitle(), control.getPrompt(),
                                               control.getNormalImage(), control.getDisabledImage(), control.getEvent());
                } else {
                    command =
                            new NewFileControl(control.getId(), control.getTitle(), control.getPrompt(), control.getIcon(),
                                               control.getEvent());
                }
            } else {
                if (control.getNormalImage() != null) {
                    command =
                            new NewFileControl(control.getId(), control.getTitle(), control.getPrompt(),
                                               control.getNormalImage(), control.getDisabledImage(),
                                               new CreateNewFileEvent(control.getMimeType()));

                } else {
                    command =
                            new NewFileControl(control.getId(), control.getTitle(), control.getPrompt(), control.getIcon(),
                                               new CreateNewFileEvent(control.getMimeType()));

                }
                command.setGroupName(control.getGroupName());
            }
            command.setDelimiterBefore(control.hasDelimiterBefore());

            controls.set(position, command);
        }
    }

    /**
     * @param controls
     * @return {@link NewItemControl}
     */
    private NewItemControl getNewItemControl(List<Control> controls) {
        for (Control control : controls) {
            if (control instanceof NewItemControl) {
                return (NewItemControl)control;
            }
        }
        return null;
    }

}
