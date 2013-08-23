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

import java.util.ArrayList;

/**
 * Formatter to sort controls from "View" menu.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 22, 2011 3:47:02 PM anya $
 */
public class ViewControlsFormatter extends ControlFormatterBase {
    /** Initialize the order of the controls in menu "View". */
    @Override
    protected void initControlsOrder() {
        controlIdsOrder = new ArrayList<String>();
        controlIdsOrder.add("View/Properties");
        controlIdsOrder.add("View/Permissions");
        controlIdsOrder.add("View/Show \\ Hide Outline");
        controlIdsOrder.add("View/Show \\ Hide Documentation");
        controlIdsOrder.add("View/Go to Folder");
        controlIdsOrder.add("View/Get URL...");
        controlIdsOrder.add("View/Progress");
        controlIdsOrder.add("View/Output");
        controlIdsOrder.add("View/Log");
        controlIdsOrder.add("View/Show \\ Hide Hidden Files");
    }

    /** @see org.exoplatform.ide.client.application.ControlFormatterBase#getMainMenuPrefix() */
    @Override
    protected String getMainMenuPrefix() {
        return "View/";
    }
}
