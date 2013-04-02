/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.eclipse.jdt.client.outline;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Image;

/**
 * Is used to set empty tree message.
 * CellTree has hard coded empty message, which cannot be changed.
 * So this node is added instead of the empty message.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Feb 24, 2012 9:16:16 AM anya $
 */
public class EmptyTreeMessage {
    /** Image to display (loader for example). */
    private Image image;

    /** Message to display. */
    private String message;

    /**
     * @param image
     * @param message
     */
    public EmptyTreeMessage(Image image, String message) {
        this.message = message;
        this.image = image;
        if (image != null) {
            DOM.setStyleAttribute(image.getElement(), "cssFloat", "left");
            DOM.setStyleAttribute(image.getElement(), "marginRight", "5px");
        }
    }

    /** @return the image */
    public Image getImage() {
        return image;
    }

    /** @return the message */
    public String getMessage() {
        return message;
    }
}
