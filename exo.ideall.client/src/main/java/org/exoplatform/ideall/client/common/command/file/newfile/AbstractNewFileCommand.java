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
package org.exoplatform.ideall.client.common.command.file.newfile;

import org.exoplatform.ideall.client.browser.BrowserPanel;
import org.exoplatform.ideall.client.framework.control.IDEControl;
import org.exoplatform.ideall.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ideall.client.panel.event.PanelSelectedHandler;
import org.exoplatform.ideall.client.workspace.event.SwitchEntryPointEvent;
import org.exoplatform.ideall.client.workspace.event.SwitchEntryPointHandler;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AbstractNewFileCommand extends IDEControl implements PanelSelectedHandler, SwitchEntryPointHandler
{

   private boolean browserSelected = false;

   public AbstractNewFileCommand(String id, String title, String prompt, String icon, GwtEvent<?> event)
   {
      super(id);
      setTitle(title);
      setPrompt(prompt);
      setIcon(icon);
      setEvent(event);
   }

   public AbstractNewFileCommand(String id, String title, String prompt, ImageResource normalIcon,
      ImageResource disabledIcon, GwtEvent<?> event)
   {
      super(id);
      setTitle(title);
      setPrompt(prompt);
      setImages(normalIcon, disabledIcon);
      setEvent(event);
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(PanelSelectedEvent.TYPE, this);
      addHandler(SwitchEntryPointEvent.TYPE, this);
   }

   @Override
   protected void onInitializeApplication()
   {
      setVisible(true);
      updateEnabling();
   }

   private void updateEnabling()
   {
      if (context.getEntryPoint() == null)
      {
         setEnabled(false);
         return;
      }

      if (browserSelected)
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

   public void onPanelSelected(PanelSelectedEvent event)
   {
      browserSelected = BrowserPanel.ID.equals(event.getPanelId()) ? true : false;
      updateEnabling();
   }

   public void onSwitchEntryPoint(SwitchEntryPointEvent event)
   {
      updateEnabling();
   }

}
