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
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 20, 2011 2:51:47 PM anya $
 */
public class MainMenuControlsFormatter implements ControlsFormatter {
    
    private List<String> controlIdsOrder = new ArrayList<String>();
    
    public MainMenuControlsFormatter() {
        controlIdsOrder.add("File");
        controlIdsOrder.add("Project");
        controlIdsOrder.add("Edit");
        controlIdsOrder.add("View");
        controlIdsOrder.add("Run");
        controlIdsOrder.add("Git");
        controlIdsOrder.add("PaaS");
        controlIdsOrder.add("Ssh");
        controlIdsOrder.add("Window");
        controlIdsOrder.add("Help");        
    }
    
    /** @see org.exoplatform.ide.client.framework.control.ControlsFormatter#format(java.util.List) */
    public void format(List<Control> controls) {
        Collections.sort(controls, controlComparator);
    }

    private Comparator<Control> controlComparator = new Comparator<Control>() {
        public int compare(Control control1, Control control2) {
            String main1 =
                    (control1.getId().indexOf("/") > 0) ? control1.getId().substring(0, control1.getId().indexOf("/")) : null;
            String main2 =
                    (control2.getId().indexOf("/") > 0) ? control2.getId().substring(0, control2.getId().indexOf("/")) : null;

         /*
          * if (main1 == null || main2 == null) return 0;
          */

            Integer index1 = controlIdsOrder.indexOf(main1);
            Integer index2 = controlIdsOrder.indexOf(main2);

         /*
          * if (index1 == -1 || index2 == -1) return 0;
          */

            return index1.compareTo(index2);
        }
    };

}
