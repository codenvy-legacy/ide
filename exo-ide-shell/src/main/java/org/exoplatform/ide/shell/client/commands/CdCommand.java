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
import org.exoplatform.ide.shell.client.EnvironmentVariables;
import org.exoplatform.ide.shell.client.cli.CommandLine;
import org.exoplatform.ide.shell.client.cli.Options;
import org.exoplatform.ide.shell.client.model.ClientCommand;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Folder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 10, 2011 evgen $
 *
 */
public class CdCommand extends ClientCommand
{

   private static final Set<String> commads = new HashSet<String>();

   static
   {
      commads.add("cd");
   }

   /**
    * 
    */
   public CdCommand()
   {
      super(commads, new Options(), CloudShell.messages.cdHelp());
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

      if (commandLine.hasOption('h'))
      {
         printHelp(CloudShell.messages.cdUsage());
         return;
      }

      if (args.isEmpty())
      {
         CloudShell.console().printPrompt();
      }
      else if (args.size() == 1)
      {
         String path = args.get(0);
         if (".".equals(path))
         {
            CloudShell.console().printPrompt();
            return;
         }
         final String newPath = Utils.getPath(Environment.get().getCurrentFolder(), path);
         try
         {
            VirtualFileSystem.getInstance().getItemByPath(newPath,
               new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper()))
               {

                  @Override
                  protected void onSuccess(ItemWrapper result)
                  {
                     if (result.getItem() instanceof Folder)
                     {
                        Environment.get().setCurrentFolder((Folder)result.getItem());
                        Environment.get().saveValue(EnvironmentVariables.CURRENT_FOLDER_ID, result.getItem().getId());
                        CloudShell.console().printPrompt();
                     }
                     else
                        CloudShell.console().println(CloudShell.messages.cdErrorFolder(result.getItem().getName()));
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     exception.printStackTrace();
                     CloudShell.console().println(CloudShell.messages.cdErrorFolder(newPath));
                  }
               });
         }
         catch (RequestException e)
         {
            e.printStackTrace();
            CloudShell.console().println(CloudShell.messages.cdErrorFolder(newPath));
         }
      }
      else
      {
         CloudShell.console().println(CloudShell.messages.cdError());
      }
   }

}
