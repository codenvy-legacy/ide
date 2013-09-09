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
package org.exoplatform.ide.client.framework.ui;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Contains hidden component, to clear focus from active component.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ClearFocusForm.java Feb 4, 2011 12:16:35 PM vereshchaka $
 */
public class ClearFocusForm {

    private static ClearFocusForm form;

    private TextBox textBox;

    private ClearFocusForm() {
        textBox = new TextBox();
        textBox.getElement().getStyle().setPosition(Position.ABSOLUTE);
        textBox.getElement().getStyle().setWidth(10, Unit.PX);
        textBox.getElement().getStyle().setHeight(10, Unit.PX);
        RootPanel.get().add(textBox, -10000, -10000);
    }

    public void clearFocus() {
        textBox.setFocus(true);
        textBox.setText(".");
    }

    public static ClearFocusForm getInstance() {
        if (form == null) {
            form = new ClearFocusForm();
        }
        return form;
    }

}
