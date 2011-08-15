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
package org.exoplatform.cloudshell.client.commands;

import org.exoplatform.cloudshell.client.CloudShell;
import org.exoplatform.cloudshell.client.EnvironmentVariables;
import org.exoplatform.cloudshell.client.cli.CommandLine;
import org.exoplatform.cloudshell.client.cli.Options;
import org.exoplatform.cloudshell.client.model.ClientCommand;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 10, 2011 evgen $
 *
 */
public class MkdirCommand extends ClientCommand
{

   private static final Set<String> commads = new HashSet<String>();

   static
   {
      commads.add("mkdir");
   }

   /**
    * 
    */
   public MkdirCommand()
   {
      super(commads, new Options(), CloudShell.messages.mkdirHelp());
   }

   /**
    * @see org.exoplatform.cloudshell.client.model.ClientCommand#execute(org.exoplatform.cloudshell.client.cli.CommandLine)
    */
   @Override
   public void execute(CommandLine commandLine)
   {
      if (commandLine.hasOption("h"))
      {
         printHelp(CloudShell.messages.mkdirUsage(), CloudShell.messages.mkdirHeader());
         return;
      }
      List<String> args = commandLine.getArgList();
      args.remove(0);
      if (args.isEmpty())
      {
         CloudShell.console().print(CloudShell.messages.mkdirError() + "\n");
         return;
      }
      for (String name : args)
      {
         Folder newFolder =
            new Folder(Utils.getPath(
               VirtualFileSystem.getInstance().getEnvironmentVariable(EnvironmentVariables.WORKDIR), name));
         VirtualFileSystem.getInstance().createFolder(newFolder, new AsyncRequestCallback<Folder>()
         {

            @Override
            protected void onSuccess(Folder result)
            {
               CloudShell.console().print(result.getName() + "\n");
            }

            /**
             * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
             */
            @Override
            protected void onFailure(Throwable exception)
            {
               CloudShell.console().print(exception.getMessage() + "\n");
               super.onFailure(exception);
            }
         });
      }

   }

}
