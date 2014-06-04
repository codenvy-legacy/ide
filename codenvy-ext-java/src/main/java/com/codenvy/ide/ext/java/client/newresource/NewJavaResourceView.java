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