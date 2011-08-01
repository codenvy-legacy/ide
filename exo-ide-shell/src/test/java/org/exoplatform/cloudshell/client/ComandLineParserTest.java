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
package org.exoplatform.cloudshell.client;

import org.exoplatform.cloudshell.client.cli.CommandLine;
import org.exoplatform.cloudshell.client.cli.GnuParser;
import org.exoplatform.cloudshell.client.cli.Option;
import org.exoplatform.cloudshell.client.cli.Options;
import org.exoplatform.cloudshell.client.cli.Parser;
import org.exoplatform.cloudshell.client.cli.Util;
import org.junit.Test;

import junit.framework.Assert;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class ComandLineParserTest
{
   
   String tabl = "/ide/git/commit;POST=git commit\n" +
                   "git_commit.body.params=-m,-a\n" +
                   "git_commit.body.b1=-m\n" +
                   "git_commit.body.b2=-a\n";

   @Test
   public void parserTest() throws Exception
   {
      String cmd = "git commit -m=\"My first commit\" -a false";
      String[] args = Util.translateCommandline(cmd);
      Option msg = new Option("m", true, "Commit message");
      Option a = new Option("a", true, "Add file");
      Options options = new Options();
      options.addOption(a);
      options.addOption(msg);
      Parser parser = new GnuParser();
      CommandLine line = parser.parse(options, args);
      Assert.assertEquals("My first commit", line.getOptionValue("m"));
      Assert.assertFalse(Boolean.valueOf(line.getOptionValue("a")));
      
      String[] commands = tabl.split("\n");
      String command = commands[0].split("=")[1];
      
      String url = "";
      if (command.equalsIgnoreCase(line.getArgs()[0] + " " + line.getArgs()[1]))
      {
        
         url = commands[0].split("=")[0];
      }
      String body = "{\"b1\":\"" + line.getOptionValue("m") + "\",\"b2\":" + line.getOptionValue("a") + "\"}";
      
      System.out.println("Url : " + url.split(";")[0] + "\nMethod : " + url.split(";")[1] + "\nBody : " +  body);
      
   }

}
