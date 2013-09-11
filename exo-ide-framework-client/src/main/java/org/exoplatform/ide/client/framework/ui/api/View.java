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
package org.exoplatform.ide.client.framework.ui.api;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import org.exoplatform.ide.client.framework.ui.api.event.HasBeforeViewLoseActivityHandler;
import org.exoplatform.ide.client.framework.ui.api.event.HasViewLostActivityHandler;

/**
 * This interfaces describes View used in IDE.
 * <p/>
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface View extends HasBeforeViewLoseActivityHandler, HasViewLostActivityHandler, IsWidget {

    /**
     * Get ID of this view.
     *
     * @return ID of this view
     */
    String getId();

    /**
     * Get type of this view.
     *
     * @return type of this view
     */
    String getType();

    /**
     * Get title of this view.
     *
     * @return title of this view
     */
    String getTitle();

    /**
     * Sets the new title of this view.
     *
     * @param title
     *         new title of this view
     */
    void setTitle(String title);

    /**
     * Get icon of this view. This icon shows in the window title or in the tab title.
     *
     * @return icon of this view.
     */
    Image getIcon();

    /**
     * Sets the new icon of this view.
     *
     * @param icon
     *         icon of this view
     */
    void setIcon(Image icon);

    /**
     * Determines whether or not this view can be closed.
     *
     * @return <b>true</b> if this view can be closed, <b>false</b> otherwise
     */
    boolean canBeClosed();

    /**
     * Get is this view is visible. This method actual only when this view is attached in the Panel.
     *
     * @return <b>true</b> when this view is visible, <b>false</b> otherwise
     */
    boolean isViewVisible();

    /** This method actual only when this view is attached in the Panel. */
    void setViewVisible();

    /**
     * Get default width of this view.
     *
     * @return default width of this view
     */
    int getDefaultWidth();

    /**
     * Get default height of this view.
     *
     * @return default height of this view
     */
    int getDefaultHeight();

    /**
     * Determines is this view resizeable.
     *
     * @return <b>true</b> if this view is resizeable.
     */
    boolean canResize();

    /**
     * Determines whether this view can show custom context menu.
     *
     * @return <b>true</b> if this view can show custom context menu.
     */
    boolean canShowContextMenu();

    /** Makes view activated. */
    void activate();

    /**
     * Determines is this view activated.
     *
     * @return <b>true</b> if this view activated, <b>false</b> otherwise.
     */
    boolean isActive();

    boolean closeOnEscape();

}
