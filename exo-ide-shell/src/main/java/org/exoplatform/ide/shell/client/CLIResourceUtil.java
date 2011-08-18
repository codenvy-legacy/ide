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
package org.exoplatform.ide.shell.client;

import org.exoplatform.ide.shell.client.cli.CommandLine;
import org.exoplatform.ide.shell.client.cli.GnuParser;
import org.exoplatform.ide.shell.client.cli.Option;
import org.exoplatform.ide.shell.client.cli.Options;
import org.exoplatform.ide.shell.client.cli.Parser;
import org.exoplatform.ide.shell.client.cli.Util;
import org.exoplatform.ide.shell.shared.CLIResource;
import org.exoplatform.ide.shell.shared.CLIResourceParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Aug 5, 2011 10:51:12 AM anya $
 *
 */
public class CLIResourceUtil
{
   /**
    * Parse the string command line to {@link CommandLine} instance.
    * Specify the <b>parameters</b> of the concrete command, that you need to parse.
    * 
    * @param cmd command line
    * @param parameters command's parameters
    * @return {@link CommandLine} parsed command line
    * @throws Exception
    */
   public static CommandLine parseCommandLine(String cmd, Set<CLIResourceParameter> parameters) throws Exception
   {
      String[] arguments = Util.translateCommandline(cmd);
      Parser parser = new GnuParser();
      Options options = formOptions(parameters);
      return parser.parse(options, arguments);
   }

   /**
    * Form the list of options, that are available for pointed parameters
    * and are necessary for parse command liine operation.
    * 
    * @param parameters command's parameters
    * @return {@link Options} options
    */
   protected static Options formOptions(Set<CLIResourceParameter> parameters)
   {
      Options options = new Options();
      if (parameters == null)
         return options;

      for (CLIResourceParameter parameter : parameters)
      {
         if (parameter.getOptions() != null)
         {
            String optionName = null;
            String longOpt = null;
            //Get options (long format starts with "--")
            for (String opt : parameter.getOptions())
            {
               //Only the names of options must be pointed (without "-" and "--").
               if (opt.startsWith("--"))
               {
                  longOpt = opt.replace("--", "");
               }
               else
               {
                  optionName = opt.startsWith("-") ? opt.replaceFirst("-", "") : opt;
               }
            }
            optionName = (optionName == null) ? longOpt : optionName;
            if (optionName == null)
            {
               continue;
            }
            //TODO No description at the moment:
            options.addOption(new Option(optionName, longOpt, parameter.isHasArg(), ""));
         }
      }
      return options;
   }

   /**
    * Get command names form {@link CLIResource} set
    * @param commands 
    * @return list of all command names
    */
   public static List<String> getAllCommandNames(Set<CLIResource> commands)
   {
      List<String> names = new ArrayList<String>();
      for (CLIResource res : commands)
      {
         names.addAll(res.getCommand());
      }
      return names;
   }
}
