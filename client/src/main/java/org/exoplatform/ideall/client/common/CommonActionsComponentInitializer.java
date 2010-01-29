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
import org.exoplatform.ideall.client.common.command.edit.FormatSourceCommand;
import org.exoplatform.ideall.client.common.command.edit.HideLineNumbersCommand;
import org.exoplatform.ideall.client.common.command.edit.RedoTypingCommand;
import org.exoplatform.ideall.client.common.command.edit.ShowLineNumbersCommand;
import org.exoplatform.ideall.client.common.command.edit.UndoTypingCommand;
import org.exoplatform.ideall.client.common.command.file.CreateFileFromTemplateCommand;
import org.exoplatform.ideall.client.common.command.file.CreateNewFolderCommand;
import org.exoplatform.ideall.client.common.command.file.DeleteItemCommand;
import org.exoplatform.ideall.client.common.command.file.DownloadFileCommand;
import org.exoplatform.ideall.client.common.command.file.DownloadZippedFolderCommand;
import org.exoplatform.ideall.client.common.command.file.GetFileURLCommand;
import org.exoplatform.ideall.client.common.command.file.GoToFolderCommand;
import org.exoplatform.ideall.client.common.command.file.MoveItemCommand;
import org.exoplatform.ideall.client.common.command.file.RefreshBrowserCommand;
import org.exoplatform.ideall.client.common.command.file.SaveAllFilesCommand;
import org.exoplatform.ideall.client.common.command.file.SaveFileAsCommand;
import org.exoplatform.ideall.client.common.command.file.SaveFileAsTemplateCommand;
import org.exoplatform.ideall.client.common.command.file.SaveFileCommand;
import org.exoplatform.ideall.client.common.command.file.SearchFilesCommand;
import org.exoplatform.ideall.client.common.command.file.UploadFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewCSSFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewGroovyFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewHTMLFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewJavaScriptFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewTEXTFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewXMLFileCommand;
import org.exoplatform.ideall.client.common.command.run.ShowPreviewCommand;
import org.exoplatform.ideall.client.common.command.view.ViewItemPropertiesCommand;

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
      /*
       * FILE GROUP
       */

      addCommand(new NewFileCommand()).disable().hide();
      addCommand(new NewXMLFileCommand()).disable().hide();
      addCommand(new NewGroovyFileCommand()).disable().hide();
      addCommand(new NewHTMLFileCommand()).disable().hide();
      addCommand(new NewTEXTFileCommand()).disable().hide();
      addCommand(new NewJavaScriptFileCommand()).disable().hide();
      addCommand(new NewCSSFileCommand()).disable().hide();

      addCommand(new CreateFileFromTemplateCommand()).disable().hide().setDelimiterBefore().dockOnToolbar();
      addCommand(new UploadFileCommand()).disable().hide().dockOnToolbar();
      
      addCommand(new DownloadFileCommand()).disable().hide().dockOnToolbar();
      addCommand(new DownloadZippedFolderCommand()).disable().hide().dockOnToolbar();

      addCommand(new SaveFileCommand()).disable().hide().setDelimiterBefore().dockOnToolbar();
      addCommand(new SaveFileAsCommand()).disable().hide().dockOnToolbar();
      addCommand(new SaveAllFilesCommand()).disable().hide().dockOnToolbar();
      addCommand(new SaveFileAsTemplateCommand()).disable().hide().dockOnToolbar();

      addCommand(new CreateNewFolderCommand()).disable().hide().setDelimiterBefore().dockOnToolbar();
      addCommand(new GetFileURLCommand()).disable().hide().dockOnToolbar();
      addCommand(new MoveItemCommand()).disable().hide().dockOnToolbar();
      addCommand(new GoToFolderCommand()).disable().hide().dockOnToolbar();
      addCommand(new DeleteItemCommand()).disable().hide().dockOnToolbar();
      addCommand(new SearchFilesCommand()).disable().hide().dockOnToolbar();
      addCommand(new RefreshBrowserCommand()).disable().hide().dockOnToolbar();

      /*
       * EDIT GROUP
       */

      addCommand(new UndoTypingCommand()).disable().hide().deselect().setDelimiterBefore().dockOnToolbar();
      addCommand(new RedoTypingCommand()).disable().hide().deselect().dockOnToolbar();
      addCommand(new FormatSourceCommand()).disable().hide().deselect().dockOnToolbar();

      addCommand(new ShowLineNumbersCommand()).disable().hide().deselect().setDelimiterBefore().dockOnToolbar();
      addCommand(new HideLineNumbersCommand()).disable().hide().deselect().dockOnToolbar();

      /*
       * VIEW GROUP
       */

      addCommand(new ViewItemPropertiesCommand()).disable().hide().deselect().dockOnToolbar(true);

      /*
       * RUN GROUP
       */

      addCommand(new ShowPreviewCommand()).disable().hide().deselect().dockOnToolbar(true);
   }

}
