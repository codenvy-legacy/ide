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
package com.codenvy.ide.ext.openshift.client.cartridge;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link CreateCartridgePresenter}.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface CreateCartridgeView extends View<CreateCartridgeView.ActionDelegate> {
    /** Needs for delegate some function into Create cartridge view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Create cartridge button. */
        public void onCreateCartridgeClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        public void onCancelClicked();
    }

    /**
     * Getting cartridge name from window.
     *
     * @return cartridge name
     */
    public String getCartridgeName();

    /**
     * Set list of cartridges into list box in window.
     *
     * @param cartridgesList
     *         array of all available cartridges
     */
    public void setCartridgesList(JsonArray<String> cartridgesList);

    /**
     * is view opened.
     *
     * @return true if window is opened otherwise false
     */
    public boolean isShown();

    /** Close current window */
    public void close();

    /** Open window */
    public void showDialog();
}
