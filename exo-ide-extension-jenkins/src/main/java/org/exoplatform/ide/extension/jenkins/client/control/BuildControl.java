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
package org.exoplatform.ide.extension.jenkins.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.jenkins.client.JenkinsExtension;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
@RolesAllowed({"workspace/developer"})
public class BuildControl extends SimpleControl implements IDEControl {

    /** @param id */
    public BuildControl() {
        super(JenkinsExtension.MESSAGES.buildJavaAppId());
        setTitle(JenkinsExtension.MESSAGES.buildJavaAppTitle());
        setPrompt(JenkinsExtension.MESSAGES.buildJavaAppPrompt());
        setImages(JenkinsExtension.RESOURCES.build(), JenkinsExtension.RESOURCES.build_Disabled());
        setEvent(new BuildApplicationEvent());
        setGroupName(GroupNames.RUNDEBUG);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        // TODO enable only if Java project selected
        setEnabled(true);
    }

}
