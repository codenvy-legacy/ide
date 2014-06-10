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
