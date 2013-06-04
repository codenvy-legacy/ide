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
package com.codenvy.ide.ext.openshift.client.domain;


import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link CreateDomainPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface CreateDomainView extends View<CreateDomainView.ActionDelegate> {
    /** Needs for delegate some function into Create Domain view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Chane Domain button. */
        public void onDomainChangeClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        public void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed something. */
        public void onValueChanged();
    }

    /**
     * Get current domain name given by user.
     *
     * @return domain name
     */
    public String getDomain();

    /**
     * Set domain name.
     *
     * @param domain
     *         domain name
     */
    public void setDomain(String domain);

    /**
     * Set error message if fails to inform user.
     *
     * @param message
     *         message contains error
     */
    public void setError(String message);

    /**
     * Enable or disable change domain name button if user filled correctly value.
     *
     * @param isEnable
     *         true - if input data is correct, otherwise false
     */
    public void setEnableChangeDomainButton(boolean isEnable);

    /** Focus to the domain name field. */
    public void focusDomainField();

    /**
     * Is current windows showed.
     *
     * @return true - if window showed, otherwise - false
     */
    public boolean isShown();

    /** Close current window. */
    public void close();

    /** Show window. */
    public void showDialog();
}
