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
package com.codenvy.ide.extension.android.client;

import com.codenvy.ide.extension.android.client.deploy.DeployApplicationPresenter;
import com.codenvy.ide.extension.android.client.run.ManyMoControlGroup;
import com.codenvy.ide.extension.android.client.run.PaaSManyMoAndroid;
import com.codenvy.ide.extension.android.client.run.RunApplicationControl;
import com.codenvy.ide.extension.android.client.run.RunApplicationManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.paas.PaaS;
import org.exoplatform.ide.client.framework.project.ProjectType;

import java.util.Arrays;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AndroidExtension extends Extension implements InitializeServicesHandler {

    public static final AndroidExtensionLocalizationConstant LOCALIZATION = GWT.create(AndroidExtensionLocalizationConstant.class);

    @Override
    public void initialize() {
        IDE.getInstance().registerPaaS(new PaaS("Manymo", "Manymo Android Emulator", new Image(AndroidExtensionClientBundle.INSTANCE.manymoPaas()), new Image(AndroidExtensionClientBundle.INSTANCE.manymoPaasDisabled()),
                                                Arrays.asList(ProjectType.ANDROID), new DeployApplicationPresenter()));

        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        
        IDE.getInstance().addControl(new ManyMoControlGroup());
        IDE.getInstance().addControl(new RunApplicationControl());
        IDE.getInstance().addControl(new PaaSManyMoAndroid());

        new RunApplicationManager();
    }

    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new AndroidExtensionServiceImpl(event.getApplicationConfiguration().getContext());
    }
}
