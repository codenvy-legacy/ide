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
package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FormPanel;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Feb 21, 2011 4:15:54 PM anya $
 */
public class DynamicForm extends FormPanel {

    private String id;

    public void setWidth(int width) {
        setWidth(width + "px");
    }

    public void setHeight(int height) {
        setHeight(height + "px");
    }

    public void setMargin(int marging) {
        DOM.setStyleAttribute(getElement(), "margin", marging + "px");
    }

    public void setPadding(int padding) {
        DOM.setStyleAttribute(getElement(), "padding", padding + "px");
    }

    public void setID(String id) {
        this.id = id;
        getElement().setAttribute("id", id);
    }

    /** @return the id */
    public String getId() {
        return id;
    }

}
