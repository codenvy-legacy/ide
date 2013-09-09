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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.PasswordTextBox;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;

/**
 * Field to enter password (when entered letters are not seen).
 * In HTML it is represented as <code><input type="password"/></code> element
 * and it is possible to display label with field's title near it.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 30, 2010 $
 */
public class PasswordField extends TextItemBase implements TextFieldItem {

    /** Default constructor. */
    public PasswordField() {
        super(new TextInputBase(new PasswordTextBox().getElement()));
    }

    /**
     * @param name
     *         field name
     */
    public PasswordField(String name) {
        this();
        setName(name);
    }

    /**
     * @param name
     *         field name
     * @param title
     *         field title
     */
    public PasswordField(String name, String title) {
        this();
        setName(name);
        setTitle(title);
    }

    /**
     * Sets ID attribute to Password Field element.
     * This method is required to use in UI Binder.
     *
     * @param id
     */
    public void setPasswordFieldId(String id) {
        super.setId(id);
    }

    /** Sets focus in password field. */
    public void focus() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                focusInItem();
            }
        });
    }

}
