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
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class EditControlsFormatter extends ControlFormatterBase {

    /** @see org.exoplatform.ide.client.application.ControlFormatterBase#initControlsOrder() */
    @Override
    protected void initControlsOrder() {
        controlIdsOrder = new ArrayList<String>();
        controlIdsOrder.add("Edit/Cut Item(s)");
        controlIdsOrder.add("Edit/Copy Item(s)");
        controlIdsOrder.add("Edit/Paste Item(s)");
        controlIdsOrder.add("Edit/Undo Typing");
        controlIdsOrder.add("Edit/Redo Typing");
        controlIdsOrder.add("Edit/Format");
        controlIdsOrder.add("Edit/Organize Imports");
        controlIdsOrder.add("Edit/Add Block Comment");
        controlIdsOrder.add("Edit/Remove Block Comment");
        controlIdsOrder.add("Edit/Find-Replace...");
        controlIdsOrder.add("Edit/Show \\ Hide Line Numbers");
        controlIdsOrder.add("Edit/Delete Current Line");
        controlIdsOrder.add("Edit/Go to Line...");
        controlIdsOrder.add("Edit/Lock \\ Unlock File");
    }

    /** @see org.exoplatform.ide.client.application.ControlFormatterBase#getMainMenuPrefix() */
    @Override
    protected String getMainMenuPrefix() {
        return "Edit/";
    }

}
