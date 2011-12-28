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
import org.exoplatform.ide.shell.VfsUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Dec 27, 2011 10:25:01 AM evgen $
 *
 */
public class VfsCommandTest extends BaseTest
{

   private static final String REST_SERVICE_GRS = "rest-service.grs";

   private static final String MAIN_FOLDER = VfsCommandTest.class.getSimpleName();

   private static Map<String, Link> folderLinks;

   @BeforeClass
   public static void uploadFolders() throws IOException
   {
      folderLinks =
         VfsUtils.importZipProject(MAIN_FOLDER, "src/test/resources/org/exoplatform/ide/shell/test-project.zip");
   }

   @AfterClass
   public static void deleteFolders() throws IOException
   {
      VfsUtils.deleteFolder(folderLinks.get(Link.REL_DELETE));
   }

   @Test
   public void vfsCommands() throws Exception
   {
      shell.executeCommand("ls");
      assertThat(shell.getText()).contains(MAIN_FOLDER);
      shell.executeCommand("cd " + MAIN_FOLDER);

      shell.executeCommand("ls");
      assertThat(shell.getText()).contains("src").contains(REST_SERVICE_GRS);

      shell.executeCommand("cat " + REST_SERVICE_GRS);
      assertThat(shell.getText()).contains("import org.exoplatform.sample.Employee;")
         .contains("@Path(\"/classpath-test\")").contains("public class HelloWorld {");

      shell.executeCommand("clear");
      shell.type("cd " + "sr");
      shell.type(Keys.TAB);
      assertThat(shell.getText()).contains("cd src/");


      shell.type("o" + Keys.TAB);
      shell.type("exo" + Keys.TAB);
      shell.type("sa" + Keys.TAB);
      shell.executeCommand();
      shell.executeCommand("pwd");
      assertThat(shell.getText()).contains("/" + MAIN_FOLDER + "/src/org/exoplatform/sample");

      
      shell.executeCommand("mkdir ../../client");
      shell.executeCommand("cd ../../client");
      shell.executeCommand("pwd");
      assertThat(shell.getText()).contains("/" + MAIN_FOLDER + "/src/org/client");

      
      shell.executeCommand("clear");
      
      shell.executeCommand("cd /");
      shell.type("rm /" + MAIN_FOLDER +"/" + "s" + Keys.TAB);
      shell.type("o" + Keys.TAB);
      shell.type("cl" + Keys.TAB);
      shell.executeCommand();
      shell.executeCommand("clear");
      
      shell.executeCommand("ls /" + MAIN_FOLDER + "/src/org");
      assertThat(shell.getText()).doesNotContain("client");
      
   }
}
