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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.gwtframework.ui.client.window.Window;

/**
 * The {@link Window} component resources.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Feb 16, 2011 12:56:09 PM anya $
 */
public interface WindowResource extends ClientBundle {
    /** Instance of the Window resources. */
    public static final WindowResource INSTANCE = GWT.create(WindowResource.class);

    /**
     * The CSS styles of the component.
     * <b>Note : </b> To inject styles to DOM, use command:
     * <pre>
     * WindowResource.INSTANCE.css().ensureInjected();
     * </pre>
     *
     * @return {@link Style}
     */
    @Source("eXoStyle/skin/default/Window.css")
    Style css();

    @Source("images/window/close.png")
    ImageResource closeWindowButton();

    @Source("images/window/close_Disabled.png")
    ImageResource closeWindowButtonDisabled();

    @Source("images/window/close_Over.png")
    ImageResource closeWindowButtonOver();

    @Source("images/window/maximize.png")
    ImageResource maximizeWindowButton();

    @Source("images/window/maximize_Disabled.png")
    ImageResource maximizeWindowButtonDisabled();

    @Source("images/window/maximize_Over.png")
    ImageResource maximizeWindowButtonOver();

    @Source("images/window/minimize.png")
    ImageResource minimizeWindowButton();

    @Source("images/window/minimize_Disabled.png")
    ImageResource minimizeWindowButtonDisabled();

    @Source("images/window/minimize_Over.png")
    ImageResource minimizeWindowButtonOver();

    @Source("images/dialog/ask.png")
    ImageResource askDialog();

    @Source("images/dialog/error.png")
    ImageResource errorDialog();

    @Source("images/dialog/say.png")
    ImageResource sayDialog();

    @Source("images/dialog/warn.png")
    ImageResource warnDialog();

    /** CSS style resources. */
    public interface Style extends CssResource {
        /** @return {@link String} style of the window button */
        String windowButton();

        /** @return {@link String} style when mouse is on window's caption */
        String captionOver();
    }
}
