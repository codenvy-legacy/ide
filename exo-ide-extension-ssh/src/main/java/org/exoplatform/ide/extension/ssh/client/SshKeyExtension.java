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
package org.exoplatform.ide.extension.ssh.client;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.preference.Preferences;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.ssh.client.keymanager.KeyManagerPresenter;
import org.exoplatform.ide.extension.ssh.client.keymanager.SshPreferenceItem;
import org.exoplatform.ide.extension.ssh.client.keymanager.SshPublicKeyPresenter;
import org.exoplatform.ide.extension.ssh.client.keymanager.UploadSshKeyPresenter;

/**
 * SSH support extension for IDE.
 */
public class SshKeyExtension extends Extension implements InitializeServicesHandler {

    /** The generator of an {@link AutoBean}. */
    public static final SshAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(SshAutoBeanFactory.class);

    /** Localization constants. */
    public static final SshLocalizationConstant CONSTANTS = GWT.create(SshLocalizationConstant.class);

    /** {@inheritDoc} */
    @Override
    public void initialize() {
        Preferences.get().addPreferenceItem(new SshPreferenceItem(new KeyManagerPresenter()));
        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        new SshPublicKeyPresenter();
        new UploadSshKeyPresenter();
    }

    /** {@inheritDoc} */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new SshKeyService(Utils.getRestContext(), event.getLoader());
    }
}
