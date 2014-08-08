/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.parts;

import com.codenvy.ide.api.mvp.View;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import org.vectomatic.dom.svg.ui.SVGImage;
import org.vectomatic.dom.svg.ui.SVGResource;

import static com.google.gwt.user.client.ui.InsertPanel.ForIsWidget;

/** PartStack View interface */
public interface PartStackView extends View<PartStackView.ActionDelegate> {

    /** Tab which can be clicked and closed */
    public interface TabItem extends HasCloseHandlers<PartStackView.TabItem>, HasClickHandlers {
    }

    public enum TabPosition {
        BELOW, LEFT, RIGHT
    }

    /** Add Tab */
    public PartStackView.TabItem addTabButton(SVGImage icon, String title, String toolTip, IsWidget widget, boolean closable);

    /** Remove Tab */
    public void removeTab(int index);

    /** Set Active Tab */
    public void setActiveTab(int index);

    /** Get Content Panel */
    public ForIsWidget getContentPanel();

    /** Set PartStack focused */
    public void setFocus(boolean focused);

    /** Update Tab */
    public void updateTabItem(int index, SVGResource icon, String title, String toolTip, IsWidget widget);

    /** Handles Focus Request Event. It is generated, when user clicks a stack anywhere */
    public interface ActionDelegate {
        /** PartStack is being clicked and requests Focus */
        void onRequestFocus();
    }
}