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
package org.exoplatform.ideall.client.command;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorNotFoundException;
import org.exoplatform.gwtframework.ui.client.dialogs.Dialogs;
import org.exoplatform.ideall.client.editor.EditorUtil;
import org.exoplatform.ideall.client.editor.event.EditorOpenFileEvent;
import org.exoplatform.ideall.client.event.file.CreateFileFromTemplateEvent;
import org.exoplatform.ideall.client.event.file.CreateFileFromTemplateHandler;
import org.exoplatform.ideall.client.event.file.CreateNewFileEvent;
import org.exoplatform.ideall.client.event.file.CreateNewFileHandler;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.template.FileTemplates;
import org.exoplatform.ideall.client.model.template.TemplateService;
import org.exoplatform.ideall.client.model.util.ImageUtil;
import org.exoplatform.ideall.client.model.util.MimeTypeResolver;
import org.exoplatform.ideall.client.model.util.NodeTypeUtil;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Item;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class CreateFileCommandThread implements CreateNewFileHandler, CreateFileFromTemplateHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private ApplicationContext context;

   public CreateFileCommandThread(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      handlers = new Handlers(eventBus);
      
      eventBus.addHandler(CreateNewFileEvent.TYPE, this);
      eventBus.addHandler(CreateFileFromTemplateEvent.TYPE, this);
   }

   public void onCreateNewFile(CreateNewFileEvent event)
   {
//      TODO
//      Item item = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0);
//
//      String extension = MimeTypeResolver.getExtensionsMap().get(event.getMimeType());
//
//      String path = item.getPath();
//      if (item instanceof File)
//      {
//         path = path.substring(0, path.lastIndexOf("/"));
//      }
//
//      String content = FileTemplates.getTemplateFor(event.getMimeType());
//
//      String fileName = "Untitled file." + extension;
//      int index = 1;
//      System.out.println("File path: " + path + "/" + fileName);
//      while (context.getOpenedFiles().get(path + "/" + fileName) != null) {
//         fileName = "Untitled file " + index + "." + extension;
//         index++;
//      }
//
//      File newFile = new File(path + "/" + fileName);
//      newFile.setContentType(event.getMimeType());
//      newFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(event.getMimeType()));
//      newFile.setIcon(ImageUtil.getIcon(event.getMimeType()));
//      newFile.setNewFile(true);
//      newFile.setContent(content);
//      newFile.setContentChanged(true);
//
//      try
//      {
//         Editor editor = EditorUtil.getEditor(event.getMimeType(), context);
//         eventBus.fireEvent(new EditorOpenFileEvent(newFile,editor));
//      }
//      catch (EditorNotFoundException e)
//      {
//         Dialogs.getInstance().showError("Can't find editor for type <b>" + event.getMimeType() + "</b>");
//      }
      
   }

   public void onCreateFileFromTemplate(CreateFileFromTemplateEvent event)
   {
      TemplateService.getInstance().getTemplates();
   }

}
