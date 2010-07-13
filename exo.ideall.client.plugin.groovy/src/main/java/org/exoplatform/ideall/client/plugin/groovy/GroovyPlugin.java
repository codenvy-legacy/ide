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
package org.exoplatform.ideall.client.plugin.groovy;

import org.exoplatform.ideall.client.framework.model.AbstractApplicationContext;
import org.exoplatform.ideall.client.framework.plugin.IDEPlugin;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class GroovyPlugin implements IDEPlugin
{

   public void initializePlugin(HandlerManager eventBus, AbstractApplicationContext context)
   {
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
      
      new GroovyPluginEventHandler(eventBus, context);
   }

}
