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
package com.codenvy.ide.menu;

import com.codenvy.ide.api.ui.action.Presentation;
import com.codenvy.ide.toolbar.PresentationFactory;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class MenuItemPresentationFactory extends PresentationFactory {
    public static final String HIDE_ICON = "HIDE_ICON";
    private final boolean myForceHide;

    public MenuItemPresentationFactory() {
        this(false);
    }

    public MenuItemPresentationFactory(boolean forceHide) {
        myForceHide = forceHide;
    }

    protected Presentation processPresentation(Presentation presentation) {
//        if (!UISettings.getInstance().SHOW_ICONS_IN_MENUS || myForceHide) {
//            presentation.setIcon(null);
//            presentation.putClientProperty(HIDE_ICON, Boolean.TRUE);
//        }
        return presentation;
    }
}
