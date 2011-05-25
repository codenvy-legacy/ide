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
package org.exoplatform.ide.extension.heroku.shared;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class HerokuRequest
{
   private String command;
   private Map<String, String> options;
   private List<String> args;
   private String workDir;

   public HerokuRequest(String command, Map<String, String> options, List<String> args, String workDir)
   {
      this.command = command;
      this.options = options;
      this.args = args;
      this.workDir = workDir;
   }

   public HerokuRequest()
   {
   }

   public String getCommand()
   {
      return command;
   }

   public void setCommand(String command)
   {
      this.command = command;
   }

   public Map<String, String> getOptions()
   {
      return options;
   }

   public void setOptions(Map<String, String> options)
   {
      this.options = options;
   }

   public List<String> getArgs()
   {
      return args;
   }

   public void setArgs(List<String> args)
   {
      this.args = args;
   }

   public String getWorkDir()
   {
      return workDir;
   }

   public void setWorkDir(String workDir)
   {
      this.workDir = workDir;
   }
}
