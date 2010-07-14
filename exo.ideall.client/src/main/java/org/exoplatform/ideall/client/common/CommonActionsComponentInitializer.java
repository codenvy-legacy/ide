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
package org.exoplatform.ideall.client.common;

import org.exoplatform.ideall.client.application.component.AbstractComponentInitializer;
import org.exoplatform.ideall.client.common.command.run.ShowPreviewCommand;
import org.exoplatform.ideall.client.common.command.view.GetFileURLCommand;
import org.exoplatform.ideall.client.common.command.view.GoToFolderCommand;
import org.exoplatform.ideall.client.common.command.view.GoToLineControl;
import org.exoplatform.ideall.client.common.command.view.ViewItemPropertiesCommand;
import org.exoplatform.ideall.client.outline.ShowOutlineControl;
import org.exoplatform.ideall.client.statusbar.EditorCursorPositionControl;
import org.exoplatform.ideall.client.statusbar.NavigatorStatusControl;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CommonActionsComponentInitializer extends AbstractComponentInitializer
{

   @Override
   protected void onItitialize()
   {
//      PopupMenuControl newFilePopupMenuCommand =
//         PopupMenuCommandBuilder.newCommand("File/New *")
//            .prompt("New")
//            .images(IDEImageBundle.INSTANCE.newFile(), IDEImageBundle.INSTANCE.newFileDisabled())
//            .setDelimiterBefore().enable().show().create();
//      addCommand(newFilePopupMenuCommand).dockOnToolbar();

      /*
       * FILE GROUP
       */

//      addCommand(new NewFileCommandMenuGroup()).disable().hide();

//      NewGroovyFileCommand newGroovyFileCommand = new NewGroovyFileCommand();
//      addCommand(newGroovyFileCommand).disable().hide();
//
//
//      NewGadgetCommand newGadgetCommand = new NewGadgetCommand();
//      addCommand(newGadgetCommand).disable().hide();
//
//      NewUWAWidgetCommand newUWAWidgetCommand = new NewUWAWidgetCommand();
//      addCommand(newUWAWidgetCommand).disable().hide();
//
//      CreateFileFromTemplateCommand createFileFromTemplateCommand = new CreateFileFromTemplateCommand();
//      addCommand(createFileFromTemplateCommand).disable().hide().setDelimiterBefore();

      
      
      

      //addCommand(new UploadFile()).disable().hide().setDelimiterBefore();

     

      /*
       * DOWNLOAD GROUP
       */
      //addCommand(new DownloadFile()).disable().hide();
     
      /*
       * END DOWNLOAD GROUP
       */

    
      /*
       * COPY, CUT, PASTE COMMAND
       */
   

      /*
       * EDIT GROUP
       */

     

      /*
       * VIEW GROUP
       */

      addCommand(new ViewItemPropertiesCommand()).disable().hide().dockOnToolbar(true); // navigation
      
      addCommand(new GetFileURLCommand()).disable().hide(); // navigation
      addCommand(new GoToFolderCommand()).disable().hide(); // navigation
      addCommand(new GoToLineControl()).disable().hide(); // edit
      

      /*
       * RUN GROUP
       */

      addCommand(new ShowOutlineControl()).setDelimiterBefore().dockOnToolbar(); //dev 
      addCommand(new ShowPreviewCommand()).disable().hide().dockOnToolbar(true); //dev


      /*
       * STATUS BAR
       * */

      addCommand(new NavigatorStatusControl(eventBus, context));  // navigation
      context.getStatusBarItems().add(NavigatorStatusControl.ID); // navigation
      
      addCommand(new EditorCursorPositionControl(eventBus, context)); // edit
      context.getStatusBarItems().add(EditorCursorPositionControl.ID); // edit
   }

}
