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
package org.exoplatform.ide.extension.java.client.datasource;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.extension.java.client.JavaClientBundle;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
@RolesAllowed({"developer"})
public class ConfigureDatasourceControl extends SimpleControl implements IDEControl,
    ProjectOpenedHandler, ProjectClosedHandler {

    public static final String ID = "Project/Datasource...";
    
    public static final String TITLE = "Datasource...";
    
    public static final String PROMPT = "Configure Datasource...";
    
    public ConfigureDatasourceControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(JavaClientBundle.INSTANCE.datasource(), JavaClientBundle.INSTANCE.datasourceDisabled());
        setEvent(new ConfigureDatasourceEvent());
    }

    @Override
    public void initialize() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);        
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        if (JavaProjects.contains(event.getProject())) {
            setVisible(true);
            setEnabled(true);
            return;
        }
        
        setEnabled(false);
        setVisible(false);
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setEnabled(false);
        setVisible(false);
    }
    
}
