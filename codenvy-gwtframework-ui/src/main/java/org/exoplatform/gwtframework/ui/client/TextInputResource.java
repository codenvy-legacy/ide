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
package org.exoplatform.gwtframework.ui.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import org.exoplatform.gwtframework.ui.client.component.TextItemBase;

/**
 * The resources for {@link TextItemBase} component.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Feb 16, 2011 12:56:09 PM anya $
 */
public interface TextInputResource extends ClientBundle {
    /** @return {@link Style} CSS styles of the text input element */
    @Source("eXoStyle/skin/default/TextItemBase.css")
    Style css();

    /** CSS style resource. */
    public interface Style extends CssResource {
        /** @return {@link String} style of text input */
        String textInput();

        /** @return {@link String} style of text input, when it's in disabled state */
        String textInputDisabled();

        /** @return {@link String} style of text input, when it's title has top orientation */
        String textInputWithTopTitle();

        /** @return {@link String} style of text input's title */
        String textInputTitle();

        /** @return {@link String} style of text input's title, when it's not shown */
        String textInputTitleHidden();

        /** @return {@link String} style of text input's title with top orientation */
        String textInputTitleTop();

        /** @return {@link String} style of text input's title with left orientation */
        String textInputTitleLeft();

        /** @return {@link String} style of text input's title with right orientation */
        String textInputTitleRight();

        /** @return {@link String} style of text input's title with text align center */
        String textAlignCenter();

        /** @return {@link String} style of text input's title with text align left */
        String textAlignLeft();

        /** @return {@link String} style of text input's title with text align right */
        String textAlignRight();

    }
}
