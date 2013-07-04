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
package com.codenvy.ide.ext.aws.client.beanstalk.manage;

import com.codenvy.ide.api.mvp.View;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * The view for {@link ManageApplicationPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface ManageApplicationView extends View<ManageApplicationView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    interface ActionDelegate {
        /** Perform action when close button clicked. */
        void onCloseButtonClicked();
    }

    /**
     * Adds main tab pain and return Simple panel to assign it into new presenter.
     *
     * @param tabText
     *         tab title.
     * @return SimplePanel object.
     */
    AcceptsOneWidget addMainTabPain(String tabText);

    /**
     * Adds version tab pain and return Simple panel to assign it into new presenter.
     *
     * @param tabText
     *         tab title.
     * @return SimplePanel object.
     */
    AcceptsOneWidget addVersionTabPain(String tabText);

    /**
     * Adds environment tab pain and return Simple panel to assign it into new presenter.
     *
     * @param tabText
     *         tab title.
     * @return SimplePanel object.
     */
    AcceptsOneWidget addEnvironmentTabPain(String tabText);

    /** Set focus in first tab. */
    void focusInFirstTab();

    /**
     * Return shown state for current window.
     *
     * @return true if shown, otherwise false.
     */
    boolean isShown();

    /** Shows current dialog. */
    void showDialog();

    /** Close current dialog. */
    void close();
}
