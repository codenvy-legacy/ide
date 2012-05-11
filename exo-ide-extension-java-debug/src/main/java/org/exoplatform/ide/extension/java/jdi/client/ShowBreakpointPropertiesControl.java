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
import org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuEvent;
import org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.java.jdi.client.events.ShowBreakpointPropertiesEvent;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;

public class ShowBreakpointPropertiesControl extends SimpleControl implements IDEControl, ShowContextMenuHandler
{
   public static final String ID = "Run/Breakpoint Properties";

   private static final String TITLE = "Breakpoint Properties";

   private static final String PROMPT = "Breakpoint Properties";

   public ShowBreakpointPropertiesControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(DebuggerClientBundle.INSTANCE.breakpointProperties(),
         DebuggerClientBundle.INSTANCE.breakpointPropertiesDisabled());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(ShowContextMenuEvent.TYPE, this);
      setVisible(true);
      setEnabled(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuHandler#onShowContextMenu(org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuEvent)
    */
   @Override
   public void onShowContextMenu(ShowContextMenuEvent event)
   {
      if (event.getObject() instanceof EditorBreakPoint)
      {
         setShowInContextMenu(true);
         setEvent(new ShowBreakpointPropertiesEvent(((EditorBreakPoint)event.getObject()).getBreakPoint()));
      }
      else if (event.getObject() instanceof BreakPoint)
      {
         setShowInContextMenu(true);
         setEvent(new ShowBreakpointPropertiesEvent((BreakPoint)event.getObject()));
      }
      else
      {
         setShowInContextMenu(false);
      }
   }

}
