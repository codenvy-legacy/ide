package com.codenvy.ide.ext.gae.client.project;

import com.codenvy.ide.api.mvp.View;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * The view of {@link ProjectPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
public interface ProjectView extends View<ProjectView.ActionDelegate> {
    /** Needs for delegate some function into Applications view. */
    interface ActionDelegate {
        /**
         * Perform action when close button clicked.
         */
        void onCloseButtonClicked();
    }

    /**
     * Create new tab and return widget to place view into it.
     *
     * @param tabTitle
     *         title of the newly created tab.
     * @return {@link AcceptsOneWidget} object. {@link com.google.gwt.user.client.ui.SimplePanel} in case of created tabs.
     */
    AcceptsOneWidget addTab(String tabTitle);

    /**
     * Set focus in first tab.
     */
    void focusFirstTab();

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
