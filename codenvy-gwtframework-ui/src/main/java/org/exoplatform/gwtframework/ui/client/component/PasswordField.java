/*
 * Copyright (C) 2010 eXo Platform SAS.
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
