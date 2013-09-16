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
package org.exoplatform.ide.extension.ssh.client.keymanager;

import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.client.framework.preference.AbstractPreferenceItem;
import org.exoplatform.ide.client.framework.preference.PreferencePerformer;
import org.exoplatform.ide.extension.ssh.client.SshClientBundle;
import org.exoplatform.ide.extension.ssh.client.SshKeyExtension;

/**
 * SSH keys preference item.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 23, 2012 10:26:52 AM anya $
 */
public class SshPreferenceItem extends AbstractPreferenceItem {
    private static final String NAME = SshKeyExtension.CONSTANTS.sshManagerTitle();

    public SshPreferenceItem(PreferencePerformer performer) {
        super(NAME, new Image(SshClientBundle.INSTANCE.sshKeyManager()), performer);
    }
}
