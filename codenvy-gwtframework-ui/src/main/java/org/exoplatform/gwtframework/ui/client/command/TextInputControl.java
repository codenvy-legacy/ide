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
package org.exoplatform.gwtframework.ui.client.command;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TextInputControl extends Control<TextInputControl> {

    public static final int DEFAULT_WIDTH = 100;

    private int size = DEFAULT_WIDTH;

    private String text = "";

    private TextEnteredEvent<?> event;

    public TextInputControl(String id) {
        super(id);
    }

    public int getSize() {
        return size;
    }

    public TextInputControl setSize(int size) {
        this.size = size;
        return this;
    }

    public String getText() {
        return text;
    }

    public TextInputControl setText(String text) {
        if (this.text == text) {
            return this;
        }

        this.text = text;
        for (ControlStateListener listener : getStateListeners()) {
            ((TextInputControlStateListener)listener).updateControlText(text);
        }

        return this;
    }

    public TextEnteredEvent<?> getEvent() {
        return event;
    }

    public TextInputControl setEvent(TextEnteredEvent<?> event) {
        this.event = event;
        return this;
    }

}
