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

package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Text area form's item with possibility to display title near it.
 * In HTML it is represented as <code><textarea /></code> element
 * and label with field's title.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 * @deprecated use {@link TextAreaInput}
 */
public class TextAreaItem extends TextItemBase {

    /** Default constructor. */
    public TextAreaItem() {
        super(new TextInputBase(new TextArea().getElement()));
    }

    /**
     * @param name
     *         form element's name
     */
    public TextAreaItem(String name) {
        this();
        setName(name);
    }

    /**
     * @param name
     *         form element's name
     * @param title
     *         title shown near form element
     */
    public TextAreaItem(String name, String title) {
        this();
        setName(name);
        setTitle(title);
    }

    /** Sets focus in text area. */
    public void focus() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                focusInItem();
            }
        });
    }
}
