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
package com.codenvy.ide.wizard.newresource.page;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.ui.wizard.newresource.NewResourceProvider;
import com.codenvy.ide.collections.Array;

/**
 * Interface of new resource view.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface NewResourcePageView extends View<NewResourcePageView.ActionDelegate> {
    /** Required for delegating function into NewResource view. */
    public interface ActionDelegate {
        /**
         * Performs some actions in response to a user's selecting a resource type.
         *
         * @param resourceType
         *         selected resource type
         */
        void onResourceTypeSelected(@NotNull NewResourceProvider resourceType);

        /** Performs some actions in response to a user's changing a resource name. */
        void onResourceNameChanged();
    }

    /** @return resource name */
    @NotNull
    String getResourceName();

    /**
     * Set resource name in place on view.
     *
     * @param name
     *         name that need to be set
     */
    void setResourceName(@NotNull String name);

    /**
     * Sets available resource wizards.
     *
     * @param resourceWizards
     *         list of resources
     */
    void setResourceWizard(@NotNull Array<NewResourceProvider> resourceWizards);

    /** Focus resource name field on view. */
    void focusResourceName();

    /**
     * Select resource type on view.
     *
     * @param resourceType
     *         resource type that need to be selected
     */
    void selectResourceType(@NotNull NewResourceProvider resourceType);
}