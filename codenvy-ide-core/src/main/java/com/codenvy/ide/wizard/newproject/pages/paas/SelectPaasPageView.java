/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.wizard.newproject.pages.paas;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.collections.Array;
import com.google.inject.ImplementedBy;

/**
 * @author Evgen Vidolob
 */
@ImplementedBy(SelectPaasViewImpl.class)
public interface SelectPaasPageView extends View<SelectPaasPageView.ActionDelegate> {
    public interface ActionDelegate{
        /**
         * Performs any actions appropriate in response to the user having selected paas.
         *
         * @param id
         *         button's id
         */
        void onPaaSSelected(int id);
    }

    /**
     * Set PaaSes on place on view.
     *
     * @param paases
     *         paases those need to be set
     */
    void setPaases(Array<PaaS> paases);

    /**
     * Select paas on view.
     *
     * @param id
     *         id of paas that need to be selected
     */
    void selectPaas(int id);

    /**
     * Sets whether Paas button is enabled.
     *
     * @param id
     *         id of PaaS that need to be enabled
     * @param isEnabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnablePaas(int id, boolean isEnabled);
}
