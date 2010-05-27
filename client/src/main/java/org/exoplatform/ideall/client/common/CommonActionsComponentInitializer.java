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

import org.exoplatform.gwtframework.ui.client.component.command.PopupMenuCommand;
import org.exoplatform.gwtframework.ui.client.component.command.builder.PopupMenuCommandBuilder;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.component.AbstractComponentInitializer;
import org.exoplatform.ideall.client.common.command.edit.CopyItemsCommand;
import org.exoplatform.ideall.client.common.command.edit.CutItemsCommand;
import org.exoplatform.ideall.client.common.command.edit.DeleteLineControl;
import org.exoplatform.ideall.client.common.command.edit.FormatSourceCommand;
import org.exoplatform.ideall.client.common.command.edit.GoToLineControl;
import org.exoplatform.ideall.client.common.command.edit.PasteItemsCommand;
import org.exoplatform.ideall.client.common.command.edit.RedoTypingCommand;
import org.exoplatform.ideall.client.common.command.edit.ShowLineNumbersCommand;
import org.exoplatform.ideall.client.common.command.edit.UndoTypingCommand;
import org.exoplatform.ideall.client.common.command.file.CreateNewFolderCommand;
import org.exoplatform.ideall.client.common.command.file.DeleteItemCommand;
import org.exoplatform.ideall.client.common.command.file.GetFileURLCommand;
import org.exoplatform.ideall.client.common.command.file.GoToFolderCommand;
import org.exoplatform.ideall.client.common.command.file.OpenFileWithCommand;
import org.exoplatform.ideall.client.common.command.file.RefreshBrowserCommand;
import org.exoplatform.ideall.client.common.command.file.RenameItemCommand;
import org.exoplatform.ideall.client.common.command.file.SaveAllFilesCommand;
import org.exoplatform.ideall.client.common.command.file.SaveFileAsCommand;
import org.exoplatform.ideall.client.common.command.file.SaveFileAsTemplateCommand;
import org.exoplatform.ideall.client.common.command.file.SaveFileCommand;
import org.exoplatform.ideall.client.common.command.file.SearchFilesCommand;
import org.exoplatform.ideall.client.common.command.file.download.DownloadFile;
import org.exoplatform.ideall.client.common.command.file.download.DownloadFileCommand;
import org.exoplatform.ideall.client.common.command.file.download.DownloadZippedFolderCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.CreateFileFromTemplateCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewCSSFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewGadgetCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewGroovyFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewHTMLFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewJavaScriptFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewTEXTFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewXMLFileCommand;
import org.exoplatform.ideall.client.common.command.file.upload.OpenLocalFileCommand;
import org.exoplatform.ideall.client.common.command.file.upload.UploadFile;
import org.exoplatform.ideall.client.common.command.file.upload.UploadFileCommand;
import org.exoplatform.ideall.client.common.command.run.ShowPreviewCommand;
import org.exoplatform.ideall.client.common.command.view.ViewItemPropertiesCommand;
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
      PopupMenuCommand newFilePopupMenuCommand =
         PopupMenuCommandBuilder.newCommand("File/New *").prompt("New").icon(Images.MainMenu.NEW).show().enable()
            .setDelimiterBefore().create();
      addCommand(newFilePopupMenuCommand).dockOnToolbar();

      /*
       * FILE GROUP
       */

      addCommand(new NewFileCommand()).disable().hide();

      NewXMLFileCommand newXmlFileCommand = new NewXMLFileCommand();
      addCommand(newXmlFileCommand).disable().hide();

      NewGroovyFileCommand newGroovyFileCommand = new NewGroovyFileCommand();
      addCommand(newGroovyFileCommand).disable().hide();

      NewHTMLFileCommand newHTMLFileCommand = new NewHTMLFileCommand();
      addCommand(newHTMLFileCommand).disable().hide();

      NewTEXTFileCommand newTEXTFileCommand = new NewTEXTFileCommand();
      addCommand(newTEXTFileCommand).disable().hide();

      NewJavaScriptFileCommand newJavaScriptFileCommand = new NewJavaScriptFileCommand();
      addCommand(newJavaScriptFileCommand).disable().hide();

      NewCSSFileCommand newCSSFileCommand = new NewCSSFileCommand();
      addCommand(newCSSFileCommand).disable().hide();

      NewGadgetCommand newGadgetCommand = new NewGadgetCommand();
      addCommand(newGadgetCommand).disable().hide();

      CreateFileFromTemplateCommand createFileFromTemplateCommand = new CreateFileFromTemplateCommand();
      addCommand(createFileFromTemplateCommand).disable().hide().setDelimiterBefore();

      addCommand(new OpenFileWithCommand()).disable().hide().dockOnToolbar();

      
      //addCommand(new UploadFile()).disable().hide().setDelimiterBefore();

      addCommand(new UploadFileCommand()).disable().hide().setDelimiterBefore();
      addCommand(new OpenLocalFileCommand()).disable().hide();

      /*
       * DOWNLOAD GROUP
       */
      //addCommand(new DownloadFile()).disable().hide();
      addCommand(new DownloadFileCommand()).disable().hide();
      addCommand(new DownloadZippedFolderCommand()).disable().hide();
      /*
       * END DOWNLOAD GROUP
       */

      addCommand(new SaveFileCommand()).disable().hide().setDelimiterBefore().dockOnToolbar();
      addCommand(new SaveFileAsCommand()).disable().hide().dockOnToolbar();
      addCommand(new SaveAllFilesCommand()).disable().hide().dockOnToolbar();
      addCommand(new SaveFileAsTemplateCommand()).disable().hide().dockOnToolbar();

      addCommand(new CreateNewFolderCommand()).disable().hide().setDelimiterBefore().dockOnToolbar();
      addCommand(new RenameItemCommand()).disable().hide().setDelimiterBefore().dockOnToolbar();
      /*
       * COPY, CUT, PASTE COMMAND
       */
      addCommand(new CutItemsCommand()).disable().hide().setDelimiterBefore().dockOnToolbar();
      addCommand(new CopyItemsCommand()).disable().hide().dockOnToolbar();
      addCommand(new PasteItemsCommand()).disable().hide().dockOnToolbar();

      addCommand(new GoToFolderCommand()).disable().hide().dockOnToolbar();
      addCommand(new DeleteItemCommand()).disable().hide().dockOnToolbar();
      addCommand(new GetFileURLCommand()).disable().hide();
      addCommand(new SearchFilesCommand()).disable().hide().dockOnToolbar();
      addCommand(new RefreshBrowserCommand()).disable().hide().dockOnToolbar();
      

      /*
       * EDIT GROUP
       */

      addCommand(new UndoTypingCommand()).disable().hide().setDelimiterBefore().dockOnToolbar();
      addCommand(new RedoTypingCommand()).disable().hide().dockOnToolbar();
      addCommand(new FormatSourceCommand()).disable().hide().dockOnToolbar();

      addCommand(new ShowLineNumbersCommand()).disable().hide().setDelimiterBefore();

      addCommand(new DeleteLineControl()).disable().hide().dockOnToolbar();
      addCommand(new GoToLineControl()).disable().hide().dockOnToolbar();

      /*
       * VIEW GROUP
       */

      addCommand(new ViewItemPropertiesCommand()).disable().hide().dockOnToolbar(true);

      /*
       * RUN GROUP
       */

      addCommand(new ShowPreviewCommand()).disable().hide().dockOnToolbar(true);

      /*
       * filling new item popup menu
       */
      newFilePopupMenuCommand.getCommands().add(newXmlFileCommand);
      newFilePopupMenuCommand.getCommands().add(newGroovyFileCommand);
      newFilePopupMenuCommand.getCommands().add(newHTMLFileCommand);
      newFilePopupMenuCommand.getCommands().add(newTEXTFileCommand);
      newFilePopupMenuCommand.getCommands().add(newJavaScriptFileCommand);
      newFilePopupMenuCommand.getCommands().add(newCSSFileCommand);
      newFilePopupMenuCommand.getCommands().add(newGadgetCommand);
      newFilePopupMenuCommand.getCommands().add(createFileFromTemplateCommand);

      /*
       * STATUS BAR
       * */

      addCommand(new NavigatorStatusControl(eventBus, context));
      context.getStatusBarItems().add(NavigatorStatusControl.ID);

   }

}
