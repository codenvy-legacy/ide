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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class PaaSMenuControlsFormatter implements ControlsFormatter {

    private static final String PAAS_PREFIX = "PaaS/";

    @Override
    public void format(List<Control> controls) {
        Collections.sort(controls, new Comparator<Control>() {

            @Override
            public int compare(Control control1, Control control2) {
                if (!control1.getId().startsWith(PAAS_PREFIX)) {
                    return 0;
                }

                if (!control2.getId().startsWith(PAAS_PREFIX)) {
                    return 0;
                }

                String name1 = control1.getId().substring(PAAS_PREFIX.length());

                String name2 = control2.getId().substring(PAAS_PREFIX.length());

                if (name1.indexOf("/") < 0 && name2.indexOf("/") < 0) {
                    return name1.compareTo(name2);
                } else if (name1.indexOf("/") >= 0 && name2.indexOf("/") < 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

    }

}
