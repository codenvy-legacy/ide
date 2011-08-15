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
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 10, 2011 evgen $
 *
 */
public class PwdCommand extends ClientCommand
{

   private static final Set<String> commads = new HashSet<String>();

   static
   {
      commads.add("pwd");
   }

   /**
    * 
    */
   public PwdCommand()
   {
      super(commads, new Options(), CloudShell.messages.pwdHelp());
   }

   /**
    * @see org.exoplatform.cloudshell.client.model.ClientCommand#execute(org.exoplatform.cloudshell.client.cli.CommandLine)
    */
   @Override
   public void execute(CommandLine commandLine)
   {
      if(commandLine.hasOption("h"))
      {
         printHelp(CloudShell.messages.pwdUsage(), CloudShell.messages.pwdHeader());
         return;
      }
      String workdir = VirtualFileSystem.getInstance().getEnvironmentVariable(EnvironmentVariables.WORKDIR);
      String entryPoint = VirtualFileSystem.getInstance().getEnvironmentVariable(EnvironmentVariables.ENTRY_POINT);
      workdir = workdir.substring(entryPoint.length() - 1);
      CloudShell.console().print(workdir + "\n");
   }

}
