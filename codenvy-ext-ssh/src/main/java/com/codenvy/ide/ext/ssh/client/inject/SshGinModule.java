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
package com.codenvy.ide.ext.ssh.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.ext.ssh.client.SshKeyService;
import com.codenvy.ide.ext.ssh.client.SshKeyServiceImpl;
import com.codenvy.ide.ext.ssh.client.key.SshKeyView;
import com.codenvy.ide.ext.ssh.client.key.SshKeyViewImpl;
import com.codenvy.ide.ext.ssh.client.manage.SshKeyManagerView;
import com.codenvy.ide.ext.ssh.client.manage.SshKeyManagerViewImpl;
import com.codenvy.ide.ext.ssh.client.upload.UploadSshKeyView;
import com.codenvy.ide.ext.ssh.client.upload.UploadSshKeyViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
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
    }
}