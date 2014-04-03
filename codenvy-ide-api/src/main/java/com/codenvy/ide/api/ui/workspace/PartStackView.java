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

package com.codenvy.ide.api.ui.workspace;

import com.codenvy.ide.api.mvp.View;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Image;


/** PartStack View interface */
public interface PartStackView extends View<PartStackView.ActionDelegate> {

    /** Tab which can be clicked and closed */
    public interface TabItem extends HasCloseHandlers<PartStackView.TabItem>, HasClickHandlers {
    }

    public enum TabPosition {
        BELOW, LEFT, RIGHT
    }

    /** Add Tab */
    public PartStackView.TabItem addTabButton(Image icon, String title, String toolTip, boolean closable);

    /** Remove Tab */
    public void removeTabButton(int index);

    /** Set Active Tab */
    public void setActiveTabButton(int index);

    /** Get Content Panel */
    public AcceptsOneWidget getContentPanel();

    /** Set PartStack focused */
    public void setFocus(boolean focused);

    /** Update Tab */
    public void updateTabItem(int index, ImageResource icon, String title, String toolTip);

    /** Clear content panel. */
    public void clearContentPanel();

    /** Handles Focus Request Event. It is generated, when user clicks a stack anywhere */
    public interface ActionDelegate {
        /** PartStack is being clicked and requests Focus */
        void onRequestFocus();
    }
}