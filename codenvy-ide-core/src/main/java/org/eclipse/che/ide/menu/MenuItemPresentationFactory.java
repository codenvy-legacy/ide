/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.menu;

import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.toolbar.PresentationFactory;
import org.eclipse.che.ide.toolbar.PresentationFactory;

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
