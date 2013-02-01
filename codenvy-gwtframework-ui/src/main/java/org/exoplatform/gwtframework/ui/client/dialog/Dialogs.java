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

package org.exoplatform.gwtframework.ui.client.dialog;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class Dialogs
{

   private static Dialogs instance;

   protected Queue<Dialog> dialogs = new LinkedList<Dialog>();

   protected Dialog currentDialog;

   protected Dialogs()
   {
      instance = this;
   }

   public static Dialogs getInstance()
   {
      return instance;
   }

   /*
    * INFO
    */

   public void showInfo(String message)
   {
      showInfo("Info", message, null);
   }

   public void showInfo(String title, String message)
   {
      showInfo(title, message, null);
   }

   public void showInfo(String title, String message, BooleanValueReceivedHandler handler)
   {
      Dialog dialog = new Dialog(title, message + "<br>&nbsp;", Dialog.Type.INFO);
      dialog.setBooleanValueReceivedHandler(handler);
      showDialog(dialog);
   }

   /*
    * WARNING
    */

   public void showError(String message)
   {
      showError("Error", message, null);
   }

   public void showError(String title, String message)
   {
      showError(title, message, null);
   }

   public void showError(String title, String message, BooleanValueReceivedHandler handler)
   {
      Dialog dialog = new Dialog(title, message + "<br>&nbsp;", Dialog.Type.WARNING);
      dialog.setBooleanValueReceivedHandler(handler);
      showDialog(dialog);
   }

   /*
    * BOOLEAN ASKING
    */

   public void ask(String title, String message, BooleanValueReceivedHandler handler)
   {
      Dialog dialog = new Dialog(title, message + "<br>&nbsp;", Dialog.Type.ASK);
      dialog.setBooleanValueReceivedHandler(handler);
      showDialog(dialog);
   }

   /*
    * VALUE ASKING
    */

   public void askForValue(String title, String message, String defaultValue, StringValueReceivedHandler handler)
   {
      Dialog dialog = new Dialog(title, message + "<br>&nbsp;", Dialog.Type.ASKVALUE);
      dialog.setDefaultValue(defaultValue);
      dialog.setStringValueReceivedHandler(handler);
      showDialog(dialog);
   }

   /*
    * OPEN CUSTOM DIALOG
    */

   public void showDialog(Dialog dialog)
   {
      dialogs.add(dialog);
      if (currentDialog != null)
      {
         return;
      }

      showQueueDialog();
   }

   protected void showQueueDialog()
   {
      currentDialog = null;

      if (dialogs.size() == 0)
      {
         return;
      }

      currentDialog = dialogs.poll();

      if (currentDialog.getType() == Dialog.Type.ASKVALUE)
      {
         openAskForValueDialog(currentDialog.getTitle(), currentDialog.getMessage(), currentDialog.getDefaultValue());
      }
      else if (currentDialog.getType() == Dialog.Type.ASK)
      {
         openAskDialog(currentDialog.getTitle(), currentDialog.getMessage());
      }
      else if (currentDialog.getType() == Dialog.Type.WARNING)
      {
         openWarningDialog(currentDialog.getTitle(), currentDialog.getMessage());
      }
      else
      {
         openInfoDialog(currentDialog.getTitle(), currentDialog.getMessage());
      }
   }

   protected abstract void openAskForValueDialog(String title, String message, String defaultValue);

   protected abstract void openAskDialog(String title, String message);

   protected abstract void openWarningDialog(String title, String message);

   protected abstract void openInfoDialog(String title, String message);

}
