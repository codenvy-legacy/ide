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

import com.google.gwt.http.client.Request;

import org.exoplatform.cloudshell.client.CloudShell;
import org.exoplatform.cloudshell.client.EnvironmentVariables;
import org.exoplatform.cloudshell.client.cli.CommandLine;
import org.exoplatform.cloudshell.client.cli.Options;
import org.exoplatform.cloudshell.client.model.ClientCommand;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 11, 2011 evgen $
 *
 */
public class RmCommand extends ClientCommand
{
   private static final Set<String> commads = new HashSet<String>();

   static
   {
      commads.add("rm");
   }

   /**
    * 
    */
   public RmCommand()
   {
      super(commads, new Options(), "Remove file or folder");
   }

   /**
    * @see org.exoplatform.cloudshell.client.model.ClientCommand#execute(org.exoplatform.cloudshell.client.cli.CommandLine)
    */
   @Override
   public void execute(CommandLine commandLine)
   {
      List<String> args = commandLine.getArgList();
      args.remove(commads.iterator().next());
      if (args.size() == 1)
      {
         String path = args.get(0);
         Folder deleteItem =
            new Folder(Utils.getPath(
               VirtualFileSystem.getInstance().getEnvironmentVariable(EnvironmentVariables.WORKDIR), path));
         VirtualFileSystem.getInstance().deleteItem(deleteItem, new AsyncRequestCallback<Item>()
         {
            
            @Override
            protected void onSuccess(Item result)
            {
               CloudShell.console().printPrompt();
            }
            
            /**
             * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
             */
            @Override
            protected void onFailure(Throwable exception)
            {
               CloudShell.console().print(exception.getMessage() + "\n");
            }
         });
      }
   }

}
