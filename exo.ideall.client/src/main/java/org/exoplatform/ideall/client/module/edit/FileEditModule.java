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
package org.exoplatform.ideall.client.module.edit;

import org.exoplatform.ideall.client.common.command.edit.DeleteLineControl;
import org.exoplatform.ideall.client.common.command.edit.FindTextCommand;
import org.exoplatform.ideall.client.common.command.edit.FormatSourceCommand;
import org.exoplatform.ideall.client.common.command.edit.RedoTypingCommand;
import org.exoplatform.ideall.client.common.command.edit.ShowLineNumbersCommand;
import org.exoplatform.ideall.client.common.command.edit.UndoTypingCommand;
import org.exoplatform.ideall.client.framework.model.AbstractApplicationContext;
import org.exoplatform.ideall.client.framework.plugin.AbstractIDEModule;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class FileEditModule extends AbstractIDEModule
{

   /**
    * @param eventBus
    * @param context
    */
   public FileEditModule(HandlerManager eventBus, ApplicationContext context)
   {
      super(eventBus, context);
      new FileEditModuleEventHandler(eventBus, context);
   }

   /**
    * @see org.exoplatform.ideall.client.framework.plugin.IDEModule#initializePlugin(com.google.gwt.event.shared.HandlerManager, org.exoplatform.ideall.client.framework.model.AbstractApplicationContext)
    */
   public void initializePlugin(HandlerManager eventBus, AbstractApplicationContext context)
   {
      addControl(new UndoTypingCommand(),true);
      addControl(new RedoTypingCommand(), true);
      addControl(new FormatSourceCommand(), true);

      addControl(new FindTextCommand(), true);
      addControl(new ShowLineNumbersCommand());
      addControl(new DeleteLineControl());
   }

}
