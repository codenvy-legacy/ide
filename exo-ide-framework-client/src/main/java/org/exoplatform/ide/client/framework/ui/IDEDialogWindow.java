/*
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
 */
package org.exoplatform.ide.client.framework.ui;

import org.exoplatform.gwtframework.ui.client.window.CloseClickHandler;
import org.exoplatform.gwtframework.ui.client.window.Window;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDEDialogWindow extends Window
{

   private static AbsolutePanel ideDialogWindowsRootPanel;

   public static AbsolutePanel getIdeDialogWindowsRootPanel()
   {
      return ideDialogWindowsRootPanel;
   }

   public static void setIdeDialogWindowsRootPanel(AbsolutePanel ideDialogsRootPanel)
   {
      IDEDialogWindow.ideDialogWindowsRootPanel = ideDialogsRootPanel;
   }

   public IDEDialogWindow(int width, int height, String id)
   {
      ClearFocusForm.getInstance().clearFocus();

      getElement().setId(id);
      setWidth(width);
      setHeight(height);
      center();
      setModal(false);

      addCloseClickHandler(new CloseClickHandler()
      {
         @Override
         public void onCloseClick()
         {
            destroy();
         }
      });
   }

   @Override
   public void show()
   {
      if (ideDialogWindowsRootPanel != null)
      {
         super.show(ideDialogWindowsRootPanel);
      }
      else
      {
         super.show();
      }
   }

   @Override
   public void showCentered()
   {
      if (ideDialogWindowsRootPanel != null)
      {
         super.showCentered(ideDialogWindowsRootPanel);
      }
      else
      {
         super.showCentered();
      }
   }

   @Override
   public void show(Panel parent)
   {
      if (ideDialogWindowsRootPanel != null)
      {
         super.show(ideDialogWindowsRootPanel);
      }
      else
      {
         super.show(parent);
      }
   }

   @Override
   public void showCentered(Panel parent)
   {
      if (ideDialogWindowsRootPanel != null)
      {
         super.showCentered(ideDialogWindowsRootPanel);
      }
      else
      {
         super.showCentered(parent);
      }
   }

}
