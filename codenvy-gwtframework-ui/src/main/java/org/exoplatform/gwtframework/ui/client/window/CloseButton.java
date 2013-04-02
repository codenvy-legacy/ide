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
package org.exoplatform.gwtframework.ui.client.window;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import org.exoplatform.gwtframework.ui.client.WindowResource;

/**
 * Button for closing window (displayed at the right upper corner).
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 3, 2011 11:09:49 AM anya $
 */
public class CloseButton extends WindowButton {
    /** Handler for close click action. */
    private CloseClickHandler closeClickHandler;

    /**
     * @param closeHandler
     *         close click window handler
     */
    public CloseButton(CloseClickHandler closeClickHandler) {
        super(WindowResource.INSTANCE.closeWindowButton(), WindowResource.INSTANCE.closeWindowButtonOver(),
              WindowResource.INSTANCE.closeWindowButtonDisabled());
        this.closeClickHandler = closeClickHandler;
        setPrompt("Close");

        addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doClick();
            }
        });
    }

    /** Perform actions on close button's click. */
    public void doClick() {
        if (closeClickHandler != null) {
            closeClickHandler.onCloseClick();
        }
    }

}
