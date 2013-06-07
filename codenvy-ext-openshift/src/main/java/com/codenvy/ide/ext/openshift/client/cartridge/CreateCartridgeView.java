/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.openshift.client.cartridge;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

import java.util.List;

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
