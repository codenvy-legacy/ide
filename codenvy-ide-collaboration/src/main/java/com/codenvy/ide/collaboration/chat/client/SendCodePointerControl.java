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
package com.codenvy.ide.collaboration.chat.client;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RolesAllowed({"developer"})
public class SendCodePointerControl extends SimpleControl implements IDEControl, ViewActivatedHandler {

    private static final String ID = "Edit/Send Code Pointer";

    public SendCodePointerControl(ChatResources resources) {
        super(ID);
        setTitle("Send Code Pointer");
        setPrompt("Send Code Pointer");
        setEvent(new SendCodePointEvent());
        setEnabled(false);
        setVisible(true);
        setShowInContextMenu(true);
        setImages(resources.blank(), resources.blank());
        setShowInMenu(false);

    }

    /** {@inheritDoc} */
    @Override
    public void initialize() {
        IDE.eventBus().addHandler(ViewActivatedEvent.TYPE, this);
    }

    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        setShowInContextMenu(event.getView().getId().contains("editor-"));
    }
}
