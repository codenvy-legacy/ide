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
package org.exoplatform.ideall.client.common.command.window;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.component.IDECommand;
import org.exoplatform.ideall.client.toolbar.customize.event.CustomizeToolbarEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CustomizeToolbarCommand extends IDECommand
{
   
   public static final String ID = "Window/Customize Toolbar...";
   
   public static final String TITLE = "Customize Toolbar...";

   public CustomizeToolbarCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setIcon(Images.MainMenu.CUSTOMIZE_TOOLBAR);
      setEvent(new CustomizeToolbarEvent());
   }

   @Override
   protected void onRegisterHandlers()
   {
      setVisible(true);
      setEnabled(true);
   }

}
