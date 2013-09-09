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
