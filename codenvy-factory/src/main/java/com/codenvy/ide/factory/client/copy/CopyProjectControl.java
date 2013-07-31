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
package com.codenvy.ide.factory.client.copy;

import com.codenvy.ide.factory.client.FactoryClientBundle;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CopyProjectControl extends SimpleControl implements IDEControl, ProjectOpenedHandler {

    public CopyProjectControl() {
        super("Project/Copy");
        setTitle("Copy project");
        setPrompt("Copy project");
        setEnabled(false);
        setVisible(true);
        setImages(FactoryClientBundle.INSTANCE.share(), FactoryClientBundle.INSTANCE.shareDisabled());
        setEvent(new CopyProjectEvent());
    }

    @Override
    public void initialize() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        if(IDE.currentWorkspace.isTemporary() && !IDE.user.isTemporary()){
            setEnabled(true);
        }
    }
}
