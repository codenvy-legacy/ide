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

package com.codenvy.ide.client;

import com.codenvy.ide.client.inject.IDEInjector;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;


/** The EntryPoint of the IDE application */
public class IDE implements EntryPoint {
    /** This is the entry point method. */
    @Override
    public void onModuleLoad() {
        //TODO: with this try fix issue https://jira.codenvycorp.com/browse/IDEX-420
        //TODO: after few hours investigation don't found reason why IDE load twice
        //TODO: in devmode it not reproduce
        if (RootPanel.getBodyElement().getId() == null || RootPanel.get().getElement().getId().isEmpty()) {
            RootPanel.getBodyElement().setId("codenvy-ide-loaded");
            IDEInjector injector = GWT.create(IDEInjector.class);
            // Force instance to be created
            @SuppressWarnings("unused")
            BootstrapController bootstrap = injector.getBootstrapController();
        }
    }
}
