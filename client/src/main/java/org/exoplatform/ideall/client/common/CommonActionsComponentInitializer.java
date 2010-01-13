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

import org.exoplatform.ideall.client.application.command.DummyCommand;
import org.exoplatform.ideall.client.application.component.AbstractComponentInitializer;
import org.exoplatform.ideall.client.common.command.edit.EditCommand;
import org.exoplatform.ideall.client.common.command.edit.FormatSourceCommand;
import org.exoplatform.ideall.client.common.command.edit.RedoTypingCommand;
import org.exoplatform.ideall.client.common.command.edit.UndoTypingCommand;
import org.exoplatform.ideall.client.common.command.file.CreateFileFromTemplateCommand;
import org.exoplatform.ideall.client.common.command.file.CreateNewFolderCommand;
import org.exoplatform.ideall.client.common.command.file.DeleteItemCommand;
import org.exoplatform.ideall.client.common.command.file.FileCommand;
import org.exoplatform.ideall.client.common.command.file.MoveItemCommand;
import org.exoplatform.ideall.client.common.command.file.RefreshBrowserCommand;
import org.exoplatform.ideall.client.common.command.file.SaveAllFilesCommand;
import org.exoplatform.ideall.client.common.command.file.SaveFileAsCommand;
import org.exoplatform.ideall.client.common.command.file.SaveFileAsTemplateCommand;
import org.exoplatform.ideall.client.common.command.file.SaveFileCommand;
import org.exoplatform.ideall.client.common.command.file.SearchFilesCommand;
import org.exoplatform.ideall.client.common.command.file.UploadFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewGroovyFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewHTMLFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewJavaScriptFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewTEXTFileCommand;
import org.exoplatform.ideall.client.common.command.file.newfile.NewXMLFileCommand;
import org.exoplatform.ideall.client.common.command.run.RunCommand;
import org.exoplatform.ideall.client.common.command.run.ShowPreviewCommand;
import org.exoplatform.ideall.client.common.command.view.ViewCommand;
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

      addCommand(new FileCommand());

      addCommand(new NewFileCommand());
      addCommand(new NewXMLFileCommand(), true, false);
      addCommand(new NewGroovyFileCommand(), true, false);
      addCommand(new NewHTMLFileCommand(), true, false);
      addCommand(new NewTEXTFileCommand(), true, false);
      addCommand(new NewJavaScriptFileCommand(), true, false);

      addCommand(new UploadFileCommand(), true, false);
      addCommand(new CreateFileFromTemplateCommand(), true, false);

      addToolbarDelimiter(false);

      addCommand(new DummyCommand("File/---1"));

      addCommand(new SaveFileCommand(), true, false);
      addCommand(new SaveFileAsCommand(), true, false);
      addCommand(new SaveAllFilesCommand(), true, false);
      addCommand(new SaveFileAsTemplateCommand(), true, false);

      addCommand(new DummyCommand("File/---2"));

      addToolbarDelimiter(false);

      addCommand(new CreateNewFolderCommand(), true, false);
      addCommand(new DeleteItemCommand(), true, false);
      addCommand(new MoveItemCommand(), true, false);
      addCommand(new SearchFilesCommand(), true, false);
      addCommand(new RefreshBrowserCommand(), true, false);

      /*
       * EDIT GROUP
       */

      addToolbarDelimiter(false);

      addCommand(new EditCommand());
      addCommand(new UndoTypingCommand(), true, false);
      addCommand(new RedoTypingCommand(), true, false);
      addCommand(new FormatSourceCommand(), true, false);

      /*
       * VIEW GROUP
       */

      addToolbarDelimiter(true);
      addCommand(new ViewCommand());
      addCommand(new ViewItemPropertiesCommand(), true, true);
      addToolbarDelimiter(true);

      /*
       * RUN GROUP
       */

      addCommand(new RunCommand());
      addCommand(new ShowPreviewCommand(), true, true);

   }

}
