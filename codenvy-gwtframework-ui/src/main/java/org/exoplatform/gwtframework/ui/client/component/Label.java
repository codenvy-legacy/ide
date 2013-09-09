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

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Label extends com.google.gwt.user.client.ui.Label implements HasValue<String> {

    private boolean isHTML = false;

    private String value;

    public boolean isHTML() {
        return isHTML;
    }

    public void setIsHTML(boolean isHTML) {
        this.isHTML = isHTML;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        if (isHTML) {
            getElement().setInnerHTML(value);
        } else {
            setText(value);
        }
    }

    public void setValue(String value, boolean fireEvents) {
        setValue(value);
    }

    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return null;
    }

    public void setID(String id) {
        getElement().setAttribute("id", id);
    }

}
