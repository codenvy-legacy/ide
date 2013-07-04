/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.googleappengine.client.login;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jun 14, 2012 11:34:04 AM anya $
 */
@RolesAllowed("developer")
public class LogoutControl extends SimpleControl implements IDEControl, SetLoggedUserStateHandler {
    private static final String ID = "PaaS/Google App Engine/Logout";

    private static final String TITLE = GoogleAppEngineExtension.GAE_LOCALIZATION.logoutControlTitle();

    private static final String PROMPT = GoogleAppEngineExtension.GAE_LOCALIZATION.logoutControlPrompt();

    public LogoutControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(GAEClientBundle.INSTANCE.logout(), GAEClientBundle.INSTANCE.logoutDisabled());
        setEvent(new LogoutEvent());
        IDE.addHandler(SetLoggedUserStateEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(false);
        setEnabled(false);
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.login.SetLoggedUserStateHandler#onSetLoggedUserState(org.exoplatform.ide
     * .extension.googleappengine.client.login.SetLoggedUserStateEvent) */
    @Override
    public void onSetLoggedUserState(SetLoggedUserStateEvent event) {
        setVisible(event.isLogged());
        setEnabled(event.isLogged());
    }
}
