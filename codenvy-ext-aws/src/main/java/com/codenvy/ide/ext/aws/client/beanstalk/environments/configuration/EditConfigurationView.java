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
package com.codenvy.ide.ext.aws.client.beanstalk.environments.configuration;

import com.codenvy.ide.api.mvp.View;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * The view for {@link EditConfigurationPresenter}
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface EditConfigurationView extends View<EditConfigurationView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    interface ActionDelegate {
        /** Perform action when apply button clicked. */
        void onApplyButtonCLicked();

        /** Perform action when cancel button clicked. */
        void onCancelButtonClicked();
    }

    /**
     * Adds Server tab pain and return Simple panel to assign it into new presenter.
     *
     * @param tabText
     *         tab title.
     * @return SimplePanel object.
     */
    AcceptsOneWidget addServerTabPain(String tabText);

    /**
     * Adds Load balancer tab pain and return Simple panel to assign it into new presenter.
     *
     * @param tabText
     *         tab title.
     * @return SimplePanel object.
     */
    AcceptsOneWidget addLoadBalancerTabPain(String tabText);

    /**
     * Adds Container tab pain and return Simple panel to assign it into new presenter.
     *
     * @param tabText
     *         tab title.
     * @return SimplePanel object.
     */
    AcceptsOneWidget addContainerTabPain(String tabText);

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
