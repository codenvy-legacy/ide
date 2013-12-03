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
package com.codenvy.ide.tutorial.wizard.newresource.page;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

/**
 * The view of {@link MyResourcePagePresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@ImplementedBy(MyResourcePageViewImpl.class)
public interface MyResourcePageView extends View<MyResourcePageView.ActionDelegate> {
    /** Required for delegating functions in view. */
    public interface ActionDelegate {
        /** Performs some actions in response to a user's changing something */
        void onValueChanged();
    }

    /** @return login field content */
    @NotNull
    String getLogin();

    /**
     * Set login content in place on view.
     *
     * @param login
     *         text that need to be set
     */
    void setLogin(@NotNull String login);

    /** @return password field content */
    @NotNull
    String getPassword();

    /**
     * Set password content in place on view.
     *
     * @param password
     *         text that need to be set
     */
    void setPassword(@NotNull String password);
}