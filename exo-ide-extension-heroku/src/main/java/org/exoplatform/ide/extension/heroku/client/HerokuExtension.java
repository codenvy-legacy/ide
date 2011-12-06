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

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.heroku.client.control.AddKeyControl;
import org.exoplatform.ide.extension.heroku.client.control.ChangeApplicationStackConrol;
import org.exoplatform.ide.extension.heroku.client.control.CreateApplicationControl;
import org.exoplatform.ide.extension.heroku.client.control.DeleteApplicationControl;
import org.exoplatform.ide.extension.heroku.client.control.HerokuControlsGroup;
import org.exoplatform.ide.extension.heroku.client.control.RakeControl;
import org.exoplatform.ide.extension.heroku.client.control.RenameApplicationControl;
import org.exoplatform.ide.extension.heroku.client.control.ShowApplicationInfoControl;
import org.exoplatform.ide.extension.heroku.client.control.ShowLogsControl;
import org.exoplatform.ide.extension.heroku.client.control.SwitchAccountControl;
import org.exoplatform.ide.extension.heroku.client.create.CreateApplicationPresenter;
import org.exoplatform.ide.extension.heroku.client.delete.DeleteApplicationCommandHandler;
import org.exoplatform.ide.extension.heroku.client.deploy.DeployApplicationPresenter;
import org.exoplatform.ide.extension.heroku.client.info.ApplicationInfoPresenter;
import org.exoplatform.ide.extension.heroku.client.key.KeysPresenter;
import org.exoplatform.ide.extension.heroku.client.login.LoginPresenter;
import org.exoplatform.ide.extension.heroku.client.logs.LogsPresenter;
import org.exoplatform.ide.extension.heroku.client.rake.RakeCommandPresenter;
import org.exoplatform.ide.extension.heroku.client.rename.RenameApplicationPresenter;
import org.exoplatform.ide.extension.heroku.client.stack.ChangeStackPresenter;

import com.google.gwt.core.client.GWT;

/**
 * Heroku extension to be added to IDE Application.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 25, 2011 11:38:06 AM anya $
 *
 */
public class HerokuExtension extends Extension implements InitializeServicesHandler
{
   
   public static final HerokuLocalizationConstant LOCALIZATION_CONSTANT = GWT.create(HerokuLocalizationConstant.class);
   
   public static final HerokuCredentialsConstant CREDENTIALS_CONSTANT = GWT.create(HerokuCredentialsConstant.class);

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      new HerokuClientServiceImpl(IDE.eventBus(), event.getApplicationConfiguration().getContext(), event.getLoader());
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(InitializeServicesEvent.TYPE, this);

      //Add controls
      IDE.getInstance().addControl(new HerokuControlsGroup());
      IDE.getInstance().addControl(new CreateApplicationControl());
      IDE.getInstance().addControl(new DeleteApplicationControl());
      IDE.getInstance().addControl(new RenameApplicationControl());
      IDE.getInstance().addControl(new ChangeApplicationStackConrol());
      IDE.getInstance().addControl(new ShowApplicationInfoControl());
      IDE.getInstance().addControl(new ShowLogsControl());
      IDE.getInstance().addControl(new RakeControl());
      IDE.getInstance().addControl(new AddKeyControl());
      IDE.getInstance().addControl(new SwitchAccountControl());
      //IDE.getInstance().addControl(new ClearKeysControl(), DockTarget.NONE, false);

      //Add presenters
      new CreateApplicationPresenter();
      new DeleteApplicationCommandHandler();
      new ApplicationInfoPresenter();
      new RenameApplicationPresenter();
      new LoginPresenter();
      new KeysPresenter();
      new RakeCommandPresenter();
      new ChangeStackPresenter();
      new LogsPresenter();
      
      new DeployApplicationPresenter();
   }

}
