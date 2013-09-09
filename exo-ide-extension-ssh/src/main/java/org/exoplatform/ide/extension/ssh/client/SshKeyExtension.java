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
import org.exoplatform.ide.extension.ssh.client.keymanager.SshKeyManagerPresenter;
import org.exoplatform.ide.extension.ssh.client.keymanager.SshPreferenceItem;
import org.exoplatform.ide.extension.ssh.client.keymanager.SshPublicKeyPresenter;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowPublicSshKeyEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowPublicSshKeyHandler;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshExtension May 17, 2011 5:00:33 PM evgen $
 */
public class SshKeyExtension extends Extension implements InitializeServicesHandler, ShowPublicSshKeyHandler {

    /** The generator of an {@link AutoBean}. */
    public static final SshAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(SshAutoBeanFactory.class);

    public static final SshLocalizationConstant CONSTANTS = GWT.create(SshLocalizationConstant.class);

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        //TODO IDE.getInstance().addControl(new SshKeyManagerControl());
        Preferences.get().addPreferenceItem(new SshPreferenceItem(new SshKeyManagerPresenter()));
        IDE.addHandler(InitializeServicesEvent.TYPE, this);
        IDE.addHandler(ShowPublicSshKeyEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     * .client.framework.application.event.InitializeServicesEvent) */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        new SshKeyService(Utils.getRestContext(), event.getLoader());
    }

    /** @see org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowPublicSshKeyHandler#onShowPublicSshKey(org.exoplatform.ide
     * .extension.ssh.client.keymanager.event.ShowPublicSshKeyEvent) */
    @Override
    public void onShowPublicSshKey(ShowPublicSshKeyEvent event) {
        new SshPublicKeyPresenter(event.getKeyItem());
    }

}
