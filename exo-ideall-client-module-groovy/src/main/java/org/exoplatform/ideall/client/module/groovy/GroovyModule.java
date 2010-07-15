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
import org.exoplatform.ideall.client.framework.model.AbstractApplicationContext;
import org.exoplatform.ideall.client.framework.module.IDEModule;
import org.exoplatform.ideall.client.module.groovy.service.groovy.GroovyServiceImpl;
import org.exoplatform.ideall.client.module.groovy.service.wadl.WadlServiceImpl;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class GroovyModule implements IDEModule
{

   private HandlerManager eventBus;

   private AbstractApplicationContext applicationContext;

   public GroovyModule(HandlerManager eventBus, AbstractApplicationContext applicationContext)
   {
      this.eventBus = eventBus;
      this.applicationContext = applicationContext;
   }

   public void initialize(HandlerManager eventBus, AbstractApplicationContext context)
   {
      System.out.println("GroovyPlugin.initializePlugin()");
      new GroovyPluginEventHandler(eventBus, context);
   }

   public void initializeModule()
   {
      //      public class NewGroovyFileCommand extends NewFileCommand
      //      {
      //
      //         public static final String ID = "File/New/New REST Service";
      //
      //         public NewGroovyFileCommand()
      //         {
      //            super(ID,
      //               "REST Service",
      //               "Create New REST Service",
      //               Images.FileTypes.GROOVY,
      //               new CreateNewFileEvent(MimeType.SCRIPT_GROOVY));
      //         }
      //
      //      }

      // add controls

      /*
       * RUN GROUP
       */

      //      addCommand(new SetAutoloadCommand()).disable().hide().setDelimiterBefore().dockOnToolbar(true);
      //
      //      addCommand(new ValidateGroovyCommand()).disable().hide().setDelimiterBefore().dockOnToolbar(true);
      //      addCommand(new DeployGroovyCommand()).disable().hide().dockOnToolbar(true);
      //      addCommand(new UndeployGroovyCommand()).disable().hide().dockOnToolbar(true);
      //
      //      /*
      //       * PREVIEW OUTPUT
      //       */
      //
      //      addCommand(new PreviewWadlOutputCommand()).disable().hide().dockOnToolbar(true);      
   }

   public void initializeServices(Loader loader)
   {
      new GroovyServiceImpl(eventBus, applicationContext.getApplicationConfiguration().getContext(), loader);
      new WadlServiceImpl(eventBus, loader);      
   }

}
