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
package org.exoplatform.ide.extension.android.client;

import com.google.gwt.core.client.GWT;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.android.client.run.RunApplicationControl;
import org.exoplatform.ide.extension.android.client.run.RunApplicationManager;
import org.exoplatform.ide.extension.android.client.run.StopApplicationControl;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AndroidExtension extends Extension implements InitializeServicesHandler {

    public static final AndroidExtensionAutoBeanFactory AUTO_BEAN_FACTORY = GWT
            .create(AndroidExtensionAutoBeanFactory.class);

    public static final AndroidExtensionLocalizationConstant LOCALIZATION = GWT.create(AndroidExtensionLocalizationConstant.class);

    @Override
    public void initialize() {
        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        IDE.getInstance().addControl(new RunApplicationControl());
        IDE.getInstance().addControl(new StopApplicationControl());

        new RunApplicationManager();
    }

    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new AndroidExtensionServiceImpl(event.getApplicationConfiguration().getContext());
    }
}
