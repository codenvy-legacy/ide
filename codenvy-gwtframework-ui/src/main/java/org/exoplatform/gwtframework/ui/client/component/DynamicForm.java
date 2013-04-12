/*
 * Copyright (C) 2011 eXo Platform SAS.
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
