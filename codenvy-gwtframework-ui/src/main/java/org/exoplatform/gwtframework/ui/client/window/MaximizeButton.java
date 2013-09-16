/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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
