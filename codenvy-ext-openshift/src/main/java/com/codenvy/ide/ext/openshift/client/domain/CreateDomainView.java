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
