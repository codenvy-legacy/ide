package com.codenvy.ide.ext.gae.client.project;

import com.codenvy.ide.api.mvp.View;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
public interface ProjectView extends View<ProjectView.ActionDelegate> {
    interface ActionDelegate {
        void onCloseButtonClicked();
    }

    AcceptsOneWidget addTab(String tabTitle);

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
