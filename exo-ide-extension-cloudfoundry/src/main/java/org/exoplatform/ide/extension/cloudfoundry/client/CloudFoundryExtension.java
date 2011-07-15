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
package org.exoplatform.ide.extension.cloudfoundry.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.cloudfoundry.client.control.ApplicationInfoControl;
import org.exoplatform.ide.extension.cloudfoundry.client.control.CloudFoundryControl;
import org.exoplatform.ide.extension.cloudfoundry.client.control.CreateApplicationControl;
import org.exoplatform.ide.extension.cloudfoundry.client.control.DeleteApplicationControl;
import org.exoplatform.ide.extension.cloudfoundry.client.control.RenameApplicationControl;
import org.exoplatform.ide.extension.cloudfoundry.client.control.RestartApplicationControl;
import org.exoplatform.ide.extension.cloudfoundry.client.control.StartApplicationControl;
import org.exoplatform.ide.extension.cloudfoundry.client.control.StopApplicationControl;
import org.exoplatform.ide.extension.cloudfoundry.client.control.UpdateApplicationControl;
import org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter;
import org.exoplatform.ide.extension.cloudfoundry.client.delete.DeleteApplicationPresenter;
import org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoginPresenter;
import org.exoplatform.ide.extension.cloudfoundry.client.operations.UpdateApplicationPresenter;
import org.exoplatform.ide.extension.cloudfoundry.client.rename.RenameApplicationPresenter;
import org.exoplatform.ide.extension.cloudfoundry.client.start.StartApplicationPresenter;

/**
 * CloudFoundry extention for IDE.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudFoundryExtension.java Jul 7, 2011 5:00:41 PM vereshchaka $
 *
 */
public class CloudFoundryExtension extends Extension implements InitializeServicesHandler
{
   /**
    * Events handler.
    */
   private HandlerManager eventBus;
   
   public static final CloudFoundryLocalizationConstant LOCALIZATION_CONSTANT = GWT.create(CloudFoundryLocalizationConstant.class);

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      new CloudFoundryClientServiceImpl(eventBus, event.getApplicationConfiguration().getContext(), event.getLoader());
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      eventBus = IDE.EVENT_BUS;
      eventBus.addHandler(InitializeServicesEvent.TYPE, this);
      
      IDE.getInstance().addControl(new CloudFoundryControl(), DockTarget.NONE, false);
      IDE.getInstance().addControl(new CreateApplicationControl(), DockTarget.NONE, false);
      IDE.getInstance().addControl(new UpdateApplicationControl(), DockTarget.NONE, false);
      IDE.getInstance().addControl(new DeleteApplicationControl(), DockTarget.NONE, false);
      IDE.getInstance().addControl(new RenameApplicationControl(), DockTarget.NONE, false);
      IDE.getInstance().addControl(new ApplicationInfoControl(), DockTarget.NONE, false);
      
      IDE.getInstance().addControl(new StartApplicationControl(), DockTarget.NONE, false);
      IDE.getInstance().addControl(new StopApplicationControl(), DockTarget.NONE, false);
      IDE.getInstance().addControl(new RestartApplicationControl(), DockTarget.NONE, false);
      
      new CreateApplicationPresenter(eventBus);
      new LoginPresenter(eventBus);
      new StartApplicationPresenter(eventBus);
      new ApplicationInfoPresenter(eventBus);
      new UpdateApplicationPresenter(eventBus);
      new RenameApplicationPresenter(eventBus);
      new DeleteApplicationPresenter(eventBus);
   }

}
