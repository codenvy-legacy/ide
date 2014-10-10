/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.ssh.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.preferences.PreferencesAgent;
import com.codenvy.ide.ext.ssh.client.manage.SshKeyManagerPresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Extension add Ssh support to the IDE Application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "SSH", version = "3.0.0")
public class SshExtension {
    @Inject
    public SshExtension(PreferencesAgent preferencesAgent, Provider<SshKeyManagerPresenter> managerPresenter) {
//        preferencesAgent.addPage(managerPresenter);
    }
}