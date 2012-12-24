/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

import org.exoplatform.ide.client.BootstrapController;
import org.exoplatform.ide.client.StyleInjector;
import org.exoplatform.ide.client.projectExplorer.ProjectExplorerPresenter;
import org.exoplatform.ide.client.projectExplorer.ProjectExplorerView;
import org.exoplatform.ide.client.projectExplorer.ProjectExplorerViewImpl;
import org.exoplatform.ide.client.workspace.WorkspacePresenter;
import org.exoplatform.ide.client.workspace.WorkspaceView;
import org.exoplatform.ide.client.workspace.WorkspaceViewImpl;

/**
 * GIN Client module, describes relations and dependencies  
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jul 24, 2012  
 */
public class IDEClientModule extends AbstractGinModule
{

   //   private static final EventBus eventBus = new SimpleEventBus();

   /**
    * {@inheritDoc}
    */
   @Override
   protected void configure()
   {
      bind(BootstrapController.class).in(Singleton.class);
      bind(WorkspaceView.class).to(WorkspaceViewImpl.class).in(Singleton.class);
      bind(WorkspacePresenter.class).in(Singleton.class);
      bind(ProjectExplorerView.class).to(ProjectExplorerViewImpl.class).in(Singleton.class);
      bind(ProjectExplorerPresenter.class).in(Singleton.class);
      bind(StyleInjector.class).in(Singleton.class);
      //      bind(IDEAppController.class).in(Singleton.class);
      //      bind(Presenter.class).to(IDEAppPresenter.class);
      //      bind(IDEAppPresenter.Display.class).to(IDEAppView.class);
   }

   //   @Provides
   //   EventBus eventBusFactory()
   //   {
   //      return eventBus;
   //   }

}
