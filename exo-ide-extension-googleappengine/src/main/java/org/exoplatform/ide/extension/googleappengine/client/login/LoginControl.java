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
package org.exoplatform.ide.extension.googleappengine.client.login;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;

/**
 * Control to log in Google App Engine.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jun 14, 2012 11:34:04 AM anya $
 */
@RolesAllowed({"workspace/developer"})
public class LoginControl extends SimpleControl implements IDEControl, SetLoggedUserStateHandler {
    private static final String ID = "PaaS/Google App Engine/Login";

    private static final String TITLE = GoogleAppEngineExtension.GAE_LOCALIZATION.loginControlTitle();

    private static final String PROMPT = GoogleAppEngineExtension.GAE_LOCALIZATION.loginControlPrompt();

    public LoginControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(GAEClientBundle.INSTANCE.login(), GAEClientBundle.INSTANCE.loginDisabled());
        setEvent(new LoginEvent());
        IDE.addHandler(SetLoggedUserStateEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(true);
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.login.SetLoggedUserStateHandler#onSetLoggedUserState(org.exoplatform.ide
     * .extension.googleappengine.client.login.SetLoggedUserStateEvent) */
    @Override
    public void onSetLoggedUserState(SetLoggedUserStateEvent event) {
        setVisible(!event.isLogged());
        setEnabled(!event.isLogged());
    }
}
