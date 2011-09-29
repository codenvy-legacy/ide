/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.shell.client.commands;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.shell.client.CloudShell;
import org.exoplatform.ide.shell.client.Environment;
import org.exoplatform.ide.shell.client.cli.CommandLine;
import org.exoplatform.ide.shell.client.cli.Options;
import org.exoplatform.ide.shell.client.model.ClientCommand;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 11, 2011 evgen $
 *
 */
public class CatCommand extends ClientCommand
{

   private static final Set<String> commads = new HashSet<String>();

   static
   {
      commads.add("cat");
   }

   private List<String> files;

   private StringBuilder out = new StringBuilder();

   /**
    * 
    */
   public CatCommand()
   {
      super(commads, new Options(), CloudShell.messages.catHelp());
   }

   /**
    * @see org.exoplatform.ide.shell.client.model.ClientCommand#execute(org.exoplatform.ide.shell.client.cli.CommandLine)
    */
   @Override
   public void execute(CommandLine commandLine)
   {
      @SuppressWarnings("unchecked")
      List<String> args = commandLine.getArgList();
      args.remove(0);
      if (commandLine.hasOption("h"))
      {
         printHelp(CloudShell.messages.catUsage(commads.iterator().next()));
         return;
      }

      if (args.isEmpty())
      {
         printHelp(CloudShell.messages.catUsage(commads.iterator().next()));
      }
      else
      {
         files = args;
         out.setLength(0);
         getNextContent();
      }
   }

   private void getNextContent()
   {
      Folder workdir = Environment.get().getCurrentFolder();
      if (files.size() != 0)
      {

         String newPath = Utils.getPath(workdir, files.get(0));
         try
         {
            VirtualFileSystem.getInstance().getItemByPath(newPath,
               new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper()))
               {

                  @Override
                  protected void onSuccess(ItemWrapper result)
                  {
                     try
                     {
                        Item i = result.getItem();
                        if (i instanceof FileModel)
                        {
                           VirtualFileSystem.getInstance().getContent(
                              new AsyncRequestCallback<FileModel>(new FileContentUnmarshaller((FileModel)i))
                              {

                                 @Override
                                 protected void onSuccess(FileModel result)
                                 {
                                    String content = Utils.htmlEncode(result.getContent());
                                    out.append(content);
                                    out.append("\n");
                                    files.remove(0);
                                    getNextContent();
                                 }

                                 @Override
                                 protected void onFailure(Throwable exception)
                                 {
                                    exception.printStackTrace();
                                    CloudShell.console().println(CloudShell.messages.catGetFileContentError());
                                 }
                              });
                        }
                        else
                        {
                           CloudShell.console().println(CloudShell.messages.catFolderError(i.getName()));
                        }
                     }
                     catch (RequestException e)
                     {
                        e.printStackTrace();
                        CloudShell.console().println(CloudShell.messages.catGetFileContentError());
                     }
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     exception.printStackTrace();
                     CloudShell.console().println(CloudShell.messages.catGetFileContentError());
                  }
               });
         }
         catch (RequestException e)
         {
            e.printStackTrace();
            CloudShell.console().println(CloudShell.messages.catFileNotFound(files.get(0)));
         }
      }
      else
      {
         CloudShell.console().print(out.toString());
      }

   }

}
