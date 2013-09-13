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
import org.exoplatform.ide.client.framework.control.ControlsFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public abstract class ControlFormatterBase implements ControlsFormatter {

    protected List<String> controlIdsOrder;

    protected abstract void initControlsOrder();

    protected abstract String getMainMenuPrefix();

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
     * @param controls
     *         all controls
     * @return sorted only new item controls
     */
    @SuppressWarnings("rawtypes")
    private List<Control> sortViewControls(List<Control> controls) {
        List<Control> viewControls = new ArrayList<Control>();
        String prefix = getMainMenuPrefix();
        for (Control control : controls) {
            if (control.getId().startsWith(prefix)) {
                viewControls.add(control);
            }
        }

        Collections.sort(viewControls, controlComparator);

        return viewControls;
    }

    /**
     *
     */
    public ControlFormatterBase() {
        initControlsOrder();
    }

}