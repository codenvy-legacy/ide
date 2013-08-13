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
package com.codenvy.ide.ext.ssh.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.preferences.PreferencesAgent;
import com.codenvy.ide.ext.ssh.client.manage.SshKeyManagerPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Extension add Ssh support to the IDE Application.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
@Extension(title = "Ssh Support.", version = "3.0.0")
public class SshExtension {
    @Inject
    public SshExtension(PreferencesAgent preferencesAgent, SshKeyManagerPresenter managerPresenter) {
        preferencesAgent.addPage(managerPresenter);
    }
}