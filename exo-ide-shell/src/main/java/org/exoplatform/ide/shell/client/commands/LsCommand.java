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

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.shell.client.CloudShell;
import org.exoplatform.ide.shell.client.EnvironmentVariables;
import org.exoplatform.ide.shell.client.cli.CommandLine;
import org.exoplatform.ide.shell.client.cli.Options;
import org.exoplatform.ide.shell.client.commands.Utils;
import org.exoplatform.ide.shell.client.model.ClientCommand;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 10, 2011 evgen $
 *
 */
public class LsCommand extends ClientCommand
{

   private static final Set<String> commads = new HashSet<String>();

   static
   {
      commads.add("ls");
   }

   /**
    * 
    */
   public LsCommand()
   {
      super(commads, new Options(), CloudShell.messages.lsHelp());
   }

   /**
    * @see org.exoplatform.ide.shell.client.model.ClientCommand#execute(org.exoplatform.ide.shell.client.cli.CommandLine)
    */
   @Override
   public void execute(CommandLine commandLine)
   {
      List<String> args = commandLine.getArgList();
      args.remove(0);

      if (commandLine.hasOption("h"))
      {
         printHelp(CloudShell.messages.lsUsage(), CloudShell.messages.lsHeader());
         return;
      }
      String workdir = VirtualFileSystem.getInstance().getEnvironmentVariable(EnvironmentVariables.WORKDIR);
      if (args.size() == 0)
      {
         getFolderContent(workdir);
      }
      else
      {
         getFolderContent(Utils.getPath(workdir, args.get(0)));
      }

   }

   private void getFolderContent(final String path)
   {
      Folder f = new Folder(path);
      VirtualFileSystem.getInstance().getChildren(f, new AsyncRequestCallback<Folder>()
      {

         @Override
         protected void onSuccess(Folder result)
         {
            StringBuilder res = new StringBuilder();
            for (Item i : result.getChildren())
            {
               if(i instanceof Folder)
               {
                  res.append("<span style=\"color:#246fd5;\">").append(i.getName()).append("</span>");
               }
               else
               {
                  res.append(i.getName());
               }
               
               res.append("\n");
            }
            CloudShell.console().print(res.toString());
         }

         /**
          * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
          */
         @Override
         protected void onFailure(Throwable exception)
         {
            CloudShell.console().print(CloudShell.messages.lsError(path) + "\n");
         }
      });
   }

}
