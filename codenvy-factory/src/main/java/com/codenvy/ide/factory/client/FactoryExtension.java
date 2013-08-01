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
package com.codenvy.ide.factory.client;

import com.codenvy.ide.factory.client.copy.CopyProjectControl;
import com.codenvy.ide.factory.client.copy.CopyProjectController;
import com.codenvy.ide.factory.client.generate.CommitChangesPresenter;
import com.codenvy.ide.factory.client.generate.FactoryURLHandler;
import com.codenvy.ide.factory.client.generate.FactoryUrlControl;
import com.codenvy.ide.factory.client.generate.GetCodeNowButtonPresenter;
import com.codenvy.ide.factory.client.generate.SendMailPresenter;
import com.codenvy.ide.factory.client.greeting.GreetingUserPresenter;
import com.codenvy.ide.factory.client.receive.FanctoryHandler;
import com.google.gwt.core.client.GWT;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * Codenvy Factory extension entry point.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FactoryExtension.java Jun 11, 2013 12:49:05 PM azatsarynnyy $
 *
 */
public class FactoryExtension extends Extension implements InitializeServicesHandler {

    /** Localization constants. */
    public static final FactoryLocalizationConstants LOCALIZATION_CONSTANTS = GWT.create(FactoryLocalizationConstants.class);

    /**
     * Handler for {@link InitializeServicesEvent}.
     * 
     * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     *      .client.framework.application.event.InitializeServicesEvent)
     */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new FactoryClientServiceImpl(event.getLoader());
    }

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(InitializeServicesEvent.TYPE, this);
        IDE.getInstance().addControl(new FactoryUrlControl());
        new FactoryURLHandler();
        new FanctoryHandler();
        new GetCodeNowButtonPresenter();
        new SendMailPresenter();
        new CommitChangesPresenter();
        new CopyProjectController();
        new GreetingUserPresenter();
    }
}
