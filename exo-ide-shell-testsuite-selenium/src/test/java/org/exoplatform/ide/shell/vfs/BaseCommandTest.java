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
package org.exoplatform.ide.shell.vfs;

import static org.fest.assertions.Assertions.assertThat;

import org.exoplatform.ide.shell.BaseTest;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Dec 26, 2011 3:04:47 PM evgen $
 *
 */
public class BaseCommandTest extends BaseTest
{

   @Test
   public void helpCommand() throws Exception
   {
      assertThat(shell.getText()).contains("Welcome to eXo Cloud Shell");
      shell.type("help");
      shell.executeCommand();
      assertThat(shell.getText().split("\n")).contains(
         "  cat            Concatenate files and print on the console.",
         "  cd             Changes the current folder.",
         "  clear          Clear the shell screen.",
         "  git add        Add file contents to the index.",
         "  git commit     Record changes to the repository.",
         "  git init       Initialize new GIT repository.", 
         "  git status     Get status of working directory.",
         "  help           Type help to see this list.",
         "  jcr cd         To view command help try : [command] -h",
         "  jcr commit     To view command help try : [command] -h",
         "  jcr cp         To view command help try : [command] -h",
         "  jcr ls         To view command help try : [command] -h",
         "  jcr man        To view command help try : [command] -h",
         "  jcr mixin      To view command help try : [command] -h",
         "  jcr mv         To view command help try : [command] -h",
         "  jcr node       To view command help try : [command] -h",
         "  jcr pwd        To view command help try : [command] -h",
         "  jcr rm         To view command help try : [command] -h",
         "  jcr rollback   To view command help try : [command] -h",
         "  jcr select     To view command help try : [command] -h",
         "  jcr setperm    To view command help try : [command] -h",
         "  jcr version    To view command help try : [command] -h",
         "  jcr ws login   To view command help try : [command] -h",
         "  jcr ws logout  To view command help try : [command] -h",
         "  jcr xpath      To view command help try : [command] -h",
         "  ls             List information about the files and folders.", 
         "  mkdir          Create new folder.",
         "  pwd            Print current folder path.", 
         "  rm             Remove file or folder",
         "  vmc apps       List deployed applications on Cloud Foundry",
         "  vmc delete     Delete the Cloud Foundry application",
         "  vmc info       Cloud Foundry system and account information",
         "  vmc login      Login to Cloud Foundry",
         "  vmc restart    Restart the Cloud Foundry application",
         "  vmc start      Start the Cloud Foundry application",
         "  vmc stats      Display resource usage for the Cloud Foundry application",
         "  vmc stop       Stop the Cloud Foundry application"
         );

   }
   
   @Test
   public void clearCommand() throws Exception
   {
      assertThat(shell.getText()).contains("Welcome to eXo Cloud Shell");
      shell.type("clear");
      shell.executeCommand();
      assertThat(shell.getText()).isEqualTo(USER_NAME + ":" +"/$  ");
   }

}
