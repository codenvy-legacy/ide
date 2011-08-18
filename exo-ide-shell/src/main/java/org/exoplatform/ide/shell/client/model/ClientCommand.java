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
package org.exoplatform.ide.shell.client.model;

import org.exoplatform.ide.shell.client.cli.CommandLine;
import org.exoplatform.ide.shell.client.cli.HelpFormatter;
import org.exoplatform.ide.shell.client.cli.Options;
import org.exoplatform.ide.shell.shared.CLIResource;
import org.exoplatform.ide.shell.shared.CLIResourceParameter;

import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 10, 2011 evgen $
 *
 */
public abstract class ClientCommand extends CLIResource
{

   protected Options options;

   public ClientCommand(Set<String> command, String path, String method, Set<String> consumes, Set<String> produces,
      Set<CLIResourceParameter> params, String description)
   {
      super(command, path, method, consumes, produces, params, description);
   }

   public ClientCommand(Set<String> command, String path, String method, Set<String> consumes, Set<String> produces,
      Set<CLIResourceParameter> params)
   {
      this(command, path, method, consumes, produces, params, null);
   }

   /**
    * 
    */
   public ClientCommand(Set<String> command, Options options, String description)
   {
      super(command, null, null, null, null, null, description);
      this.options = options;
      options.addOption("h", false, "display this help");
   }

   /**
    * @return the options
    */
   public Options getOptions()
   {
      return options;
   }

   public void printHelp(String usage)
   {
      printHelp(usage, null);
   }

   public void printHelp(String usage, String header)
   {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(usage, header, options, null);
   }

   public abstract void execute(CommandLine commandLine);

}
