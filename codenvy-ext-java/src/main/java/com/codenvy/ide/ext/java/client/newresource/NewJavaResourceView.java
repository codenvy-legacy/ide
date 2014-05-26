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
package com.codenvy.ide.ext.java.client.newresource;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;

/**
 * The view of {@link NewJavaResourcePresenter}.
 *
 * @author Artem Zatsarynnyy
 */
public interface NewJavaResourceView extends View<NewJavaResourceView.ActionDelegate> {

    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Ok button. */
        void onOkClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
    }

    /** Set available resource types. */
    void setTypes(Array<ResourceTypes> types);

    /** Returns content of the name field. */
    String getName();

    /** Returns selected type. */
    ResourceTypes getSelectedType();

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}