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
package org.exoplatform.ide.extension.cloudbees.client.account;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientBundle;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.login.LoginEvent;

/**
 * Control for switching between CloudBees accounts.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 5, 2012 5:02:29 PM anya $
 */
@RolesAllowed({"developer"})
public class SwitchAccountControl extends SimpleControl implements IDEControl, VfsChangedHandler {
    private static final String ID = CloudBeesExtension.LOCALIZATION_CONSTANT.controlSwitchAccountId();

    private static final String TITLE = CloudBeesExtension.LOCALIZATION_CONSTANT.controlSwitchAccountTitle();

    private static final String PROMPT = CloudBeesExtension.LOCALIZATION_CONSTANT.controlSwitchAccountPrompt();

    /** @param id */
    public SwitchAccountControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(CloudBeesClientBundle.INSTANCE.switchAccount(), CloudBeesClientBundle.INSTANCE.switchAccountDisabled());
        setEvent(new LoginEvent(null, null));
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);

        setVisible(true);
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        setEnabled(event.getVfsInfo() != null);
    }

}
