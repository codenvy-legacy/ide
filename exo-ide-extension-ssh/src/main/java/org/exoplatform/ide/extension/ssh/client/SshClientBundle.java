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
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshClientBundle May 18, 2011 9:23:17 AM evgen $
 */
public interface SshClientBundle extends ClientBundle {

    SshClientBundle INSTANCE = GWT.create(SshClientBundle.class);

    @Source("org/exoplatform/ide/extension/ssh/client/images/ssh-key-manager_Disabled.png")
    ImageResource sshKeyManagerDisabled();

    @Source("org/exoplatform/ide/extension/ssh/client/images/ssh-key-manager.png")
    ImageResource sshKeyManager();

    @Source("org/exoplatform/ide/extension/ssh/client/images/github-generate.png")
    ImageResource sshKeyGithubGenerate();

}
