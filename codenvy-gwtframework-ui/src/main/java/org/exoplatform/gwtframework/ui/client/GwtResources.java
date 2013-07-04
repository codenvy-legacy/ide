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

/**
 * Resources for common GWT components.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Feb 16, 2011 12:56:09 PM anya $
 */
public interface GwtResources extends ClientBundle {
    /** Instance of the GWT resources. */
    public static final GwtResources INSTANCE = GWT.create(GwtResources.class);

    /**
     * <b>Note : </b> To inject styles to DOM use command:
     * <pre>
     * GwtResources.INSTANCE.css().ensureInjected();
     * </pre>
     *
     * @return {@link Style} CSS styles
     */
    @Source("eXoStyle/skin/default/gwt-style.css")
    Style css();

    /** CSS styles for GWT components. */
    public interface Style extends CssResource {
        /** @return {@link String} checkbox style */
        String checkBox();

        /** @return {@link String} checkbox disabled state style */
        String checkBoxDisabled();

        /** @return {@link String} checkbox when title is at the left side */
        String checkBoxTitleLeft();

        /** @return {@link String} radio button's style */
        String radioButton();

        /** @return {@link String} radio button's style when disabled */
        String radioButtonDisabled();

        /** @return {@link String} text box's style */
        String textBox();

        /** @return {@link String} text box's style when has focus */
        String textBoxFocused();

        /** @return {@link String} text box's style in disabled state */
        String textBoxDisabled();

        String transparent();

        String loaderBackground();

        String loaderCenteredContent();

        String loaderImage();
    }

    @Source("org/exoplatform/gwtframework/ui/client/component/ajax-loader-new.gif")
    ImageResource loader();

    @Source("org/exoplatform/gwtframework/ui/client/component/loader-background-element.png")
    ImageResource loaderBackground();

}
