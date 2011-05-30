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
package org.exoplatform.ide.extension.heroku.client;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.heroku.client.control.CreateApplicationControl;
import org.exoplatform.ide.extension.heroku.client.control.DeleteApplicationControl;
import org.exoplatform.ide.extension.heroku.client.create.CreateApplicationPresenter;
import org.exoplatform.ide.extension.heroku.client.delete.DeleteApplicationPresenter;
import org.exoplatform.ide.extension.heroku.client.login.LoginPresenter;

/**
 * Heroku extension to be added to IDE Application.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 25, 2011 11:38:06 AM anya $
 *
 */
public class HerokuExtension extends Extension implements InitializeServicesHandler
{
   /**
    * Events handler.
    */
   private HandlerManager eventBus;

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      new HerokuClientServiceImpl(eventBus, event.getApplicationConfiguration().getContext(), event.getLoader());
      new LoginPresenter(eventBus);
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      eventBus = IDE.EVENT_BUS;
      eventBus.addHandler(InitializeServicesEvent.TYPE, this);

      //Add controls
      IDE.getInstance().addControl(new CreateApplicationControl(), DockTarget.NONE, false);
      IDE.getInstance().addControl(new DeleteApplicationControl(), DockTarget.NONE, false);

      //Add presenters
      new CreateApplicationPresenter(eventBus);
      new DeleteApplicationPresenter(eventBus);
   }

}
