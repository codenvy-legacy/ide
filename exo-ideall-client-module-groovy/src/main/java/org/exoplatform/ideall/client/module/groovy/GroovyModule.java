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
package org.exoplatform.ideall.client.module.groovy;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.framework.control.NewItemControl;
import org.exoplatform.ideall.client.framework.model.AbstractApplicationContext;
import org.exoplatform.ideall.client.framework.module.AbstractIDEModule;
import org.exoplatform.ideall.client.module.groovy.controls.DeployGroovyCommand;
import org.exoplatform.ideall.client.module.groovy.controls.PreviewWadlOutputCommand;
import org.exoplatform.ideall.client.module.groovy.controls.SetAutoloadCommand;
import org.exoplatform.ideall.client.module.groovy.controls.UndeployGroovyCommand;
import org.exoplatform.ideall.client.module.groovy.controls.ValidateGroovyCommand;
import org.exoplatform.ideall.client.module.groovy.service.groovy.GroovyServiceImpl;
import org.exoplatform.ideall.client.module.groovy.service.wadl.WadlServiceImpl;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class GroovyModule extends AbstractIDEModule
{

   public GroovyModule(HandlerManager eventBus, AbstractApplicationContext applicationContext)
   {
      super(eventBus, applicationContext);
   }

   public void initializeModule()
   {
      addControl(new NewItemControl("File/New/New REST Service", "REST Service", "Create REST Service",
         Images.FileType.GROOVY, MimeType.SCRIPT_GROOVY));

      addControl(new SetAutoloadCommand(), true, true);
      addControl(new ValidateGroovyCommand(), true, true);
      addControl(new DeployGroovyCommand(), true, true);
      addControl(new UndeployGroovyCommand(), true, true);

      addControl(new PreviewWadlOutputCommand(), true, true);

      new GroovyPluginEventHandler(eventBus, context);
   }

   public void initializeServices(Loader loader)
   {
      new GroovyServiceImpl(eventBus, context.getApplicationConfiguration().getContext(), loader);
      new WadlServiceImpl(eventBus, loader);
   }

}
