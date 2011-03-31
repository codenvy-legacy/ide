/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.git.client;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.git.client.add.AddToIndexPresenter;
import org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter;
import org.exoplatform.ide.git.client.commit.CommitPresenter;
import org.exoplatform.ide.git.client.control.AddToIndexControl;
import org.exoplatform.ide.git.client.control.CloneRepositoryControl;
import org.exoplatform.ide.git.client.control.CommitControl;
import org.exoplatform.ide.git.client.control.InitRepositoryControl;
import org.exoplatform.ide.git.client.control.ShowStatusControl;
import org.exoplatform.ide.git.client.create.InitRepositoryPresenter;
import org.exoplatform.ide.git.client.status.StatusCommandHandler;

/**
 * Git extension to be added to IDE application.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 22, 2011 12:53:29 PM anya $
 *
 */
public class GitExtension extends Extension implements InitializeServicesHandler
{
   private HandlerManager eventBus;
   
   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      this.eventBus = IDE.EVENT_BUS;
      eventBus.addHandler(InitializeServicesEvent.TYPE, this);
      
      //Add controls:
      IDE.getInstance().addControl(new InitRepositoryControl(), DockTarget.NONE, false);
      IDE.getInstance().addControl(new CloneRepositoryControl(), DockTarget.NONE, false);
      IDE.getInstance().addControl(new AddToIndexControl(), DockTarget.NONE, false);
      IDE.getInstance().addControl(new CommitControl(), DockTarget.NONE, false);
      IDE.getInstance().addControl(new ShowStatusControl(), DockTarget.NONE, false);
      
      //Create presenters:
      new CloneRepositoryPresenter(eventBus);
      new InitRepositoryPresenter(eventBus);
      new StatusCommandHandler(eventBus);
      new AddToIndexPresenter(eventBus);
      new CommitPresenter(eventBus);
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      new GitClientServiceImpl(eventBus, event.getApplicationConfiguration().getContext(), event.getLoader());
   }

}
