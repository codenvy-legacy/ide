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
package com.codenvy.ide.tutorial.wizard.newproject.pages.paas;

import com.google.inject.Provider;

/**
 * The provider for a PaaS wizard page.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class PaasPageProvider implements Provider<PaasPage> {
    private String caption;
    private String paasID;

    public PaasPageProvider(String caption, String paasID) {
        this.caption = caption;
        this.paasID = paasID;
    }

    /** {@inheritDoc} */
    @Override
    public PaasPage get() {
        return new PaasPage(caption, paasID);
    }
}