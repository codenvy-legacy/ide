/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.debug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Build;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public class DebugCheckStopedDebugAfterClosingProject extends DebuggerServices
{
   private static final String PROJECT = DebugCheckStopedDebugAfterClosingProject.class.getSimpleName();

   protected static Map<String, Link> project;

   @Before
   public void before()
   {

      try
      {
         project =
            VirtualFileSystemUtils.importZipProject(PROJECT,
               "src/test/resources/org/exoplatform/ide/debug/change-variable-proj.zip");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }


   }

   @After
   public void tearDown() throws IOException, InterruptedException
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }

   @Test
   public void setBreackPointTest() throws Exception
   {
      //step 1 run debug app and wait while build finish
      openProjectAndClass(PROJECT);

      //step 2 run debug app and wait while build finish
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEBUG_APPLICATION);
      IDE.BUILD.waitOpened();
      String builderMessage = IDE.BUILD.getOutputMessage();
      assertTrue(builderMessage.startsWith(Build.Messages.BUILDING_PROJECT));
      IDE.PROGRESS_BAR.waitProgressBarControlClose();

      //step 3 check opening output panel and wait debug control 
      IDE.DEBUGER.waitOpened();
      IDE.DEBUGER.waitTabOfDebuger();
      IDE.DEBUGER.clickOnDebugerTab();
      IDE.DEBUGER.waitTabOfDebuger();
      isDebugerButtonsWithoutBreakPoints();
      IDE.JAVAEDITOR.selectTab("GreetingController.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();

      //TODO after resolve issue IDE-2179 method should be completed
      IDE.DEBUGER.setBreakPoint(22);
      IDE.DEBUGER.waitToggleBreackPointIsSet(22);
      IDE.DEBUGER.waitBreackPointsTabContainetWithSpecifiedValue("[line :");
      assertTrue(IDE.DEBUGER.getTextFromBreackPointTabContainer().contains(
         "helloworld.GreetingController - [line : 22]"));

      //step 3 close the curent project and check closing
      IDE.MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.CLOSE_PROJECT);
      IDE.ASK_DIALOG.waitOpened();
      IDE.ASK_DIALOG.clickYes();
      IDE.ASK_DIALOG.waitClosed();
      IDE.DEBUGER.waitDebugerIsClosed();
      IDE.OUTPUT.waitForSubTextPresent("stopped.");
      assertTrue(IDE.OUTPUT.getAllMessagesFromOutput().endsWith("stopped."));
      IDE.OUTPUT.clickClearButton();
    }

   /**
    * @throws Exception
    */
   private void openProjectAndClass(String project) throws Exception
   {
      IDE.PROJECT.OPEN.openProject(project);
      if (IDE.ASK_DIALOG.isOpened())
      {
         IDE.ASK_DIALOG.clickYes();
         IDE.ASK_DIALOG.waitClosed();
      }
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("src/main/java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("helloworld");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("helloworld");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("GreetingController.java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("GreetingController.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }
   
   

  
   
}
