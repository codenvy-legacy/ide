/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.module.navigation.control.upload;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.module.navigation.event.upload.OpenFileByPathEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
*/
@RolesAllowed({"administrators", "developers"})
public class OpenFileByPathCommand extends SimpleControl implements IDEControl, EntryPointChangedHandler
{

   private final static String ID = "File/Open File By Path...";

   private final static String TITLE = "Open File By Path...";

   private final static String PROMPT = "Open File By Path...";

   private boolean browserPanelSelected = true;

   public OpenFileByPathCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(IDEImageBundle.INSTANCE.openFileByPath(), IDEImageBundle.INSTANCE.openFileByPathDisabled());
      setEvent(new OpenFileByPathEvent());
   }

   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(EntryPointChangedEvent.TYPE, this);
   }   

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      if (event.getEntryPoint() != null)
      {
         setVisible(true);
      }
      else
      {
         setVisible(false);
      }

      updateEnabling();
   }
   
   private void updateEnabling()
   {
      if (browserPanelSelected)
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }   
}
