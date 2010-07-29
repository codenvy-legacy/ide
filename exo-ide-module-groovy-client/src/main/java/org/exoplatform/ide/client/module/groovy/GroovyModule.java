/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.module.groovy;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.module.groovy.controls.DeployGroovyCommand;
import org.exoplatform.ide.client.module.groovy.controls.PreviewWadlOutputCommand;
import org.exoplatform.ide.client.module.groovy.controls.SetAutoloadCommand;
import org.exoplatform.ide.client.module.groovy.controls.UndeployGroovyCommand;
import org.exoplatform.ide.client.module.groovy.controls.ValidateGroovyCommand;
import org.exoplatform.ide.client.module.groovy.service.groovy.GroovyServiceImpl;
import org.exoplatform.ide.client.module.groovy.service.wadl.WadlServiceImpl;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.module.IDEModule;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class GroovyModule implements IDEModule, InitializeServicesHandler
{

   private HandlerManager eventBus;

   public GroovyModule(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(InitializeServicesEvent.TYPE, this);

      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New REST Service", "REST Service",
         "Create REST Service", Images.FileType.GROOVY, MimeType.GROOVY_SERVICE)));

      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New Groovy Script", "Groovy Script",
         "Create Groovy Script", Images.FileType.GROOVY, MimeType.APPLICATION_GROOVY)));

      eventBus.fireEvent(new RegisterControlEvent(new SetAutoloadCommand(eventBus), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new ValidateGroovyCommand(eventBus), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new DeployGroovyCommand(eventBus), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new UndeployGroovyCommand(eventBus), true, true));
      eventBus.fireEvent(new RegisterControlEvent(new PreviewWadlOutputCommand(eventBus), true, true));

      new GroovyPluginEventHandler(eventBus);
   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      new GroovyServiceImpl(eventBus, event.getApplicationConfiguration().getContext(), event.getLoader());
      new WadlServiceImpl(eventBus, event.getLoader());
   }

}
