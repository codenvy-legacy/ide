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
