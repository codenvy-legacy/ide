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
package org.exoplatform.ide.client.project;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.framework.control.ControlsFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ProjectMenuItemFormatter.java Nov 21, 2011 1:01:01 PM vereshchaka $
 */
public class ProjectMenuItemFormatter implements ControlsFormatter {
    private List<String> controlIdsOrder;

    private void initControlsOrder() {
        controlIdsOrder = new ArrayList<String>();

        controlIdsOrder.add("Project/New...");
        controlIdsOrder.add("Project/Open...");
        controlIdsOrder.add("Project/Close");
        controlIdsOrder.add("Project/PaaS");
    }

    /** @see org.exoplatform.ide.client.framework.control.ControlsFormatter#format(java.util.List) */
    @Override
    public void format(List<Control> controls) {
        initControlsOrder();
        Collections.sort(controls, controlComparator);
    }

    private Comparator<Control> controlComparator = new Comparator<Control>() {
        public int compare(Control control1, Control control2) {
            Integer index1 = controlIdsOrder.indexOf(control1.getId());
            Integer index2 = controlIdsOrder.indexOf(control2.getId());

            if (index1 == -1 || index2 == -1)
                return 0;

            return index1.compareTo(index2);
        }
    };

}
