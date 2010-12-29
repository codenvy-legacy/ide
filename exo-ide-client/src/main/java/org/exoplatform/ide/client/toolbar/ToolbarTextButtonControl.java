/**
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.exoplatform.ide.client.toolbar;

import org.exoplatform.gwtframework.ui.client.command.StatusTextControl;
import org.exoplatform.gwtframework.ui.client.command.StatusTextControlStateListener;
import org.exoplatform.gwtframework.ui.client.text.TextButton;
import org.exoplatform.gwtframework.ui.client.toolbar.Toolbar;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ToolbarTextButtonControl extends TextButton implements StatusTextControlStateListener
{

   private HandlerManager eventBus;

   private StatusTextControl statusTextControl;

   private Toolbar toolbar;

   public ToolbarTextButtonControl(HandlerManager eventBus, StatusTextControl statusTextControl, Toolbar toolbar)
   {
      super(statusTextControl.getText());

      this.eventBus = eventBus;
      this.statusTextControl = statusTextControl;
      this.toolbar = toolbar;

      setTitle(statusTextControl.getPrompt());

      if (statusTextControl.getEvent() != null)
      {
         setCommand(textButtonCommand);
      }

      if (statusTextControl.getSize() > 0)
      {
         setWidth("" + statusTextControl.getSize() + "px");
      }

      setTextAlignment(statusTextControl.getTextAlignment());
   }

   @Override
   protected void onAttach()
   {
      super.onAttach();
      getParent().setTitle(statusTextControl.getPrompt());
      getParent().setVisible(statusTextControl.isVisible());
      statusTextControl.getStateListeners().add(this);
   }

   @Override
   protected void onDetach()
   {
      super.onDetach();
      statusTextControl.getStateListeners().remove(this);
   }

   public void updateControlEnabling(boolean enabled)
   {
   }

   public void updateControlVisibility(boolean visible)
   {
      getParent().setVisible(visible);
      toolbar.hideDuplicatedDelimiters();

      if (statusTextControl.getEvent() != null)
      {
         setCommand(textButtonCommand);
      }
      else
      {
         setCommand(null);
      }
   }

   public void updateControlPrompt(String prompt)
   {
      setTitle(prompt);
   }

   public void updateControlIcon(String icon)
   {
   }

   public void updateStatusText(String text)
   {
      setText(text);
   }

   private Command textButtonCommand = new Command()
   {
      public void execute()
      {
         if (statusTextControl.getEvent() != null)
         {
            eventBus.fireEvent(statusTextControl.getEvent());
         }
      }
   };

}
