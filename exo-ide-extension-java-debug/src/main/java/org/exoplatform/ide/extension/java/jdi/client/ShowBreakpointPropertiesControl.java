/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.java.jdi.client.events.ShowBreakpointPropertiesEvent;

public class ShowBreakpointPropertiesControl extends SimpleControl implements IDEControl
{
   //public static final String ID = DebuggerExtension.LOCALIZATION_CONSTANT.runAppControlId();
   public static final String ID = "Run/Breakpoint Properties";

   private static final String TITLE = "Breakpoint Properties";

   private static final String PROMPT = "Breakpoint Properties";

   public ShowBreakpointPropertiesControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(DebuggerClientBundle.INSTANCE.runApp(), DebuggerClientBundle.INSTANCE.runAppDisabled());
      setEvent(new ShowBreakpointPropertiesEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      setVisible(true);
      setEnabled(true);
   }

}
