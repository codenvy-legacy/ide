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
package com.codenvy.ide.factory.client;

import com.codenvy.ide.factory.client.copy.CopyProjectController;
import com.codenvy.ide.factory.client.factory.CreateFactoryPresenter;
import com.codenvy.ide.factory.client.generate.CommitChangesPresenter;
import com.codenvy.ide.factory.client.generate.FactoryURLHandler;
import com.codenvy.ide.factory.client.generate.FactoryUrlControl;
import com.codenvy.ide.factory.client.generate.SendMailPresenter;
import com.codenvy.ide.factory.client.greeting.GreetingUserPresenter;
import com.codenvy.ide.factory.client.receive.FactoryHandler;
import com.google.gwt.core.client.GWT;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * Codenvy Factory extension entry point.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FactoryExtension.java Jun 11, 2013 12:49:05 PM azatsarynnyy $
 */
public class FactoryExtension extends Extension implements InitializeServicesHandler {

    /** Localization constants. */
    public static final FactoryLocalizationConstants LOCALIZATION_CONSTANTS = GWT.create(FactoryLocalizationConstants.class);

    /** {@inheritDoc} */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new FactoryClientServiceImpl(event.getLoader());
    }

    /** {@inheritDoc} */
    @Override
    public void initialize() {
        IDE.addHandler(InitializeServicesEvent.TYPE, this);
        IDE.getInstance().addControl(new FactoryUrlControl());

        new FactoryURLHandler();
        new FactoryHandler();
        new CreateFactoryPresenter();
        new SendMailPresenter();
        new CommitChangesPresenter();
        new CopyProjectController();
        new GreetingUserPresenter();
    }
}
