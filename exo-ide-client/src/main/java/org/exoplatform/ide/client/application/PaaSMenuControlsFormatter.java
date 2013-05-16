/*
 * Copyright (C) 2012 eXo Platform SAS.
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
