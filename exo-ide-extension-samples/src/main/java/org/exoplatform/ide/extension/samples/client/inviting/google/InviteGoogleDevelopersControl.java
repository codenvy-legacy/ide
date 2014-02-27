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
package org.exoplatform.ide.extension.samples.client.inviting.google;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.DisableInTempWorkspace;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
@RolesAllowed({"workspace/developer"})
@DisableInTempWorkspace
public class InviteGoogleDevelopersControl extends SimpleControl implements IDEControl {

    private static final String ID = "Share/Invite Developers...";

    private static final String TITLE = "Invite Developers...";

    private static final String PROMPT = "Invite Developers...";

    public InviteGoogleDevelopersControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(SamplesClientBundle.INSTANCE.invite(), SamplesClientBundle.INSTANCE.inviteDisable());
        setEvent(new InviteGoogleDevelopersEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setEnabled(true);
        setVisible(true);
    }

}
