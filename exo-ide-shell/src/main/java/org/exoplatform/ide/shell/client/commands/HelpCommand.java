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

import org.exoplatform.ide.shell.client.CloudShell;
import org.exoplatform.ide.shell.client.cli.CommandLine;
import org.exoplatform.ide.shell.client.cli.Options;
import org.exoplatform.ide.shell.client.model.ClientCommand;
import org.exoplatform.ide.shell.shared.CLIResource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 10, 2011 evgen $
 *
 */
public class HelpCommand extends ClientCommand
{

   private static final Set<String> commands = new HashSet<String>();
   
   static
   {
      commands.add("help");
   }
   /**
    * 
    */
   public HelpCommand()
   {
      super(commands, new Options(), CloudShell.messages.helpHelp());
   }
   /**
    * @see org.exoplatform.ide.shell.client.model.ClientCommand#execute(org.exoplatform.ide.shell.client.cli.CommandLine)
    */
   @Override
   public void execute(CommandLine commandLine)
   {
      Map<String, String> commands = new TreeMap<String, String>();
      int max = 0;
      String tab = "  ";
      for (CLIResource res : CloudShell.getCommands())
      {
         for (String s : res.getCommand())
         {
            commands.put(s, res.getDescription() == null ? "" : res.getDescription());
            if (s.length() > max)
               max = s.length();
         }
      }
      StringBuilder help = new StringBuilder();
      for (String name : commands.keySet())
      {
         char chars[] = new char[tab.length() + max - name.length()];
         Arrays.fill(chars, (char)' ');
         String s = new String(chars);
         help.append(tab);
         help.append(name);
         help.append(s);
         help.append(commands.get(name));
         help.append("\n");
      }
      CloudShell.console().print(help.toString());
   }

}
