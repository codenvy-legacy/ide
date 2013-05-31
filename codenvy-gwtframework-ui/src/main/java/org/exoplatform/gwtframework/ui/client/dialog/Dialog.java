/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.gwtframework.ui.client.dialog;


/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Dialog {

    public enum Type {
        ASK,
        ASKVALUE,
        INFO,
        WARNING

    }

    private BooleanValueReceivedHandler booleanValueReceivedHandler;
    private String                      defaultValue;
    private String                      message;
    private StringValueReceivedHandler  stringValueReceivedHandler;
    private String                      title;
    private Type                        type;
    private boolean                     modal;

    public Dialog(String title, String message, Type type) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.modal = false;
    }

    public Dialog(String title, String message, Type type, boolean modal) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.modal = modal;
    }

    public BooleanValueReceivedHandler getBooleanValueReceivedHandler() {
        return booleanValueReceivedHandler;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getMessage() {
        return message;
    }

    public StringValueReceivedHandler getStringValueReceivedHandler() {
        return stringValueReceivedHandler;
    }

    public String getTitle() {
        return title;
    }

    public boolean getModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        this.modal = modal;
    }

    public Type getType() {
        return type;
    }

    public void setBooleanValueReceivedHandler(BooleanValueReceivedHandler booleanValueReceivedHandler) {
        this.booleanValueReceivedHandler = booleanValueReceivedHandler;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStringValueReceivedHandler(StringValueReceivedHandler stringValueReceivedHandler) {
        this.stringValueReceivedHandler = stringValueReceivedHandler;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
