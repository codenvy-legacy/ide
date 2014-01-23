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
package com.codenvy.ide.ext.ssh.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.api.ui.preferences.PreferencesPagePresenter;
import com.codenvy.ide.ext.ssh.client.SshKeyService;
import com.codenvy.ide.ext.ssh.client.SshKeyServiceImpl;
import com.codenvy.ide.ext.ssh.client.key.SshKeyView;
import com.codenvy.ide.ext.ssh.client.key.SshKeyViewImpl;
import com.codenvy.ide.ext.ssh.client.manage.SshKeyManagerPresenter;
import com.codenvy.ide.ext.ssh.client.manage.SshKeyManagerView;
import com.codenvy.ide.ext.ssh.client.manage.SshKeyManagerViewImpl;
import com.codenvy.ide.ext.ssh.client.upload.UploadSshKeyView;
import com.codenvy.ide.ext.ssh.client.upload.UploadSshKeyViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Singleton;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@ExtensionGinModule
public class SshGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(SshKeyService.class).to(SshKeyServiceImpl.class).in(Singleton.class);

        bind(SshKeyManagerView.class).to(SshKeyManagerViewImpl.class).in(Singleton.class);
        bind(SshKeyView.class).to(SshKeyViewImpl.class).in(Singleton.class);
        bind(UploadSshKeyView.class).to(UploadSshKeyViewImpl.class).in(Singleton.class);
        GinMultibinder<PreferencesPagePresenter> prefBinder = GinMultibinder.newSetBinder(binder(), PreferencesPagePresenter.class);
        prefBinder.addBinding().to(SshKeyManagerPresenter.class);
    }
}