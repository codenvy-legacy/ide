/*
 * Copyright (C) 2013 eXo Platform SAS.
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
