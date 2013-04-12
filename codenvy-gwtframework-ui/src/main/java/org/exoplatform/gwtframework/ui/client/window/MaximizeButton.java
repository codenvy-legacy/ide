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
import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.gwtframework.ui.client.WindowResource;

/**
 * Button for maximizing window (displayed at the right upper corner).
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 4, 2011 11:25:26 AM anya $
 */
public class MaximizeButton extends WindowButton {
    /** Maximize handler. */
    private MaximizeHandler maximizeHandler;

    /** Restore handler. */
    private RestoreHandler restoreHandler;

    /** Used for toggling button. */
    private boolean isMaximized = false;

    /** Restore button's icon. */
    private static final ImageResource iconRestore = WindowResource.INSTANCE.minimizeWindowButton();

    /** Restore button's icon with over state. */
    private static final ImageResource iconRestoreOver = WindowResource.INSTANCE.minimizeWindowButtonOver();

    /** Restore button's icon with disabled state. */
    private static final ImageResource iconRestoreDisabled = WindowResource.INSTANCE.minimizeWindowButtonDisabled();

    /** Maximize button's icon. */
    private static final ImageResource iconMaximize = WindowResource.INSTANCE.maximizeWindowButton();

    /** Maximize button's icon with over state. */
    private static final ImageResource iconMaximizeOver = WindowResource.INSTANCE.maximizeWindowButtonOver();

    /** Maximize button's icon with disabled state. */
    private static final ImageResource iconMaximizeDisabled = WindowResource.INSTANCE.maximizeWindowButtonDisabled();

    /**
     * @param maximizeHandler
     *         maximize window handler
     * @param restoreHandler
     *         restore window handler
     */
    public MaximizeButton(MaximizeHandler maximizeHandler, RestoreHandler restoreHandler) {
        super(iconMaximize, iconMaximizeOver, iconMaximizeDisabled);
        this.restoreHandler = restoreHandler;
        this.maximizeHandler = maximizeHandler;
        setPrompt("Maximize");

        addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                doClick();
            }
        });
    }

    /** Perform actions on button's click. */
    public void doClick() {
        if (isMaximized) {
            isMaximized = false;
            setIcon(iconMaximize);
            setIconOver(iconMaximizeOver);
            setIconDisabled(iconMaximizeDisabled);
            setPrompt("Maximize");
            restoreHandler.onRestore();
        } else {
            isMaximized = true;
            setIcon(iconRestore);
            setIconOver(iconRestoreOver);
            setIconDisabled(iconRestoreDisabled);
            setPrompt("Restore");
            maximizeHandler.onMaximize();
        }
    }

    public boolean isMaximized() {
        return isMaximized;
    }

}
