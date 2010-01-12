/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.groovy;

import org.exoplatform.ideall.client.application.command.DummyCommand;
import org.exoplatform.ideall.client.application.component.AbstractComponentInitializer;
import org.exoplatform.ideall.client.groovy.command.DeployGroovyCommand;
import org.exoplatform.ideall.client.groovy.command.PreviewGroovyOutputCommand;
import org.exoplatform.ideall.client.groovy.command.RunCommand;
import org.exoplatform.ideall.client.groovy.command.UndeployGroovyCommand;
import org.exoplatform.ideall.client.groovy.command.ValidateGroovyCommand;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GroovyActionsComponentInitializer extends AbstractComponentInitializer
{

   @Override
   protected void onItitialize()
   {
      addCommand(new RunCommand());
      
      /*
       * RUN GROUP
       */

      addToolbarDelimiter(true);
      addCommand(new ValidateGroovyCommand(), true, true);
      addCommand(new DeployGroovyCommand(), true, true);
      addCommand(new UndeployGroovyCommand(), true, true);

      /*
       * DELIMITER
       */

      addCommand(new DummyCommand("Run/---1"));

      /*
       * PREVIEW OUTPUT
       */

      addCommand(new PreviewGroovyOutputCommand(), true, true);
      addToolbarDelimiter(true);
   }

}
