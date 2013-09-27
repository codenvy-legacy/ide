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
package org.exoplatform.ide.git.client;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.framework.control.ControlsFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Formatter to sort controls from "Git" menu.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 22, 2011 3:47:30 PM anya $
 */
public class GitControlsFormatter implements ControlsFormatter {
    private List<String> controlIdsOrder;

    /** Initialize the order of the controls in menu "Git". */
    private void initControlsOrder() {
        controlIdsOrder = new ArrayList<String>();
        controlIdsOrder.add("Git/Initialize Repository");
        controlIdsOrder.add("Git/Clone Repository...");
        controlIdsOrder.add("Git/Delete Repository...");
        controlIdsOrder.add("Git/Add...");
        controlIdsOrder.add("Git/Reset Files...");
        controlIdsOrder.add("Git/Reset...");
        controlIdsOrder.add("Git/Remove...");
        controlIdsOrder.add("Git/Commit...");
        controlIdsOrder.add("Git/Branches...");
        controlIdsOrder.add("Git/Merge...");
        controlIdsOrder.add("Git/Remote");
        controlIdsOrder.add("Git/Remote/Push...");
        controlIdsOrder.add("Git/Remote/Fetch...");
        controlIdsOrder.add("Git/Remote/Pull...");
        controlIdsOrder.add("Git/Remote/Remotes...");
        controlIdsOrder.add("Git/Show History...");
        controlIdsOrder.add("Git/Status");
        controlIdsOrder.add("Git/Git URL (Read-Only)...");
    }

    /** @param eventBus */
    public GitControlsFormatter() {
        initControlsOrder();
    }

    /** @see org.exoplatform.ide.client.framework.control.ControlsFormatter#format(java.util.List) */
    @SuppressWarnings("rawtypes")
    public void format(List<Control> controls) {
        List<Control> viewControls = sortViewControls(controls);
        controls.removeAll(viewControls);
        controls.addAll(viewControls);
    }

    /**
     * Sort new items controls and return them.
     * 
     * @param controls all controls
     * @return sorted only new item controls
     */
    @SuppressWarnings("rawtypes")
    private List<Control> sortViewControls(List<Control> controls) {
        List<Control> viewControls = new ArrayList<Control>();
        for (Control control : controls) {
            if (control.getId().startsWith("Git/")) {
                viewControls.add(control);
            }
        }

        Collections.sort(viewControls, controlComparator);
        return viewControls;
    }

    /** Comparator for items order. */
    @SuppressWarnings("rawtypes")
    private Comparator<Control> controlComparator = new Comparator<Control>() {
                                                      public int compare(Control control1, Control control2) {
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
}
