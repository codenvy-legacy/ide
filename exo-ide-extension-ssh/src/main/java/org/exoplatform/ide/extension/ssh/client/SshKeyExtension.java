/*
 * Copyright (C) 2011 eXo Platform SAS.
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
