/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.miscellaneous;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 22, 2010 3:49:01 PM evgen $
 * 
 */
public class RestServicesDiscoveryTest extends BaseTest
{
   private static final String PROJECT = RestServicesDiscoveryTest.class.getSimpleName();

   private final static String FILE_NAME = "Rest.grs";

   private static final String UNIQUE_NAME_PREFIX = "_" + System.currentTimeMillis();

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/miscellaneous/rest_service_discovery.groovy";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.GROOVY_SERVICE, filePath);
      }
      catch (Exception e)
      {
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   public void testRestServicesDiscovery() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      // open file
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile();

      IDE.EDITOR.moveCursorDown(10);
      IDE.EDITOR.moveCursorRight(19);
      IDE.EDITOR.typeTextIntoEditor(UNIQUE_NAME_PREFIX);

      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(FILE_NAME);

      // deploy rest service
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      IDE.OUTPUT.waitForMessageShow(1, 30);
      String mess = IDE.OUTPUT.getOutputMessage(1);

      assertTrue(mess.contains("[INFO]"));
      assertTrue(mess.contains(FILE_NAME + " deployed successfully."));

      // run rest service discovery command
      IDE.MENU.runCommand(MenuCommands.Help.HELP, MenuCommands.Help.REST_SERVICES);
      IDE.REST_SERVICE_DISCOVERY.waitOpened();
      IDE.REST_SERVICE_DISCOVERY.waitOkButtonAppeared();
      //      Thread.sleep(20000);
      IDE.REST_SERVICE_DISCOVERY.waitForItem("/aa");
      IDE.REST_SERVICE_DISCOVERY.clickOpenCloseButton("/aa");
      IDE.REST_SERVICE_DISCOVERY.waitForItem("/aa/testServ");
      IDE.REST_SERVICE_DISCOVERY.clickOpenCloseButton("/aa/testServ");
      final String optionsId = IDE.REST_SERVICE_DISCOVERY.getItemId("/aa/testServ") + ":OPTIONS";
      IDE.REST_SERVICE_DISCOVERY.waitForItemById(optionsId);
      IDE.REST_SERVICE_DISCOVERY.selectItemById(optionsId);

      // check elements on opened form
      assertTrue(IDE.REST_SERVICE_DISCOVERY.isMethodFieldPresent());
      assertTrue(IDE.REST_SERVICE_DISCOVERY.isRequestFieldPresent());
      assertTrue(IDE.REST_SERVICE_DISCOVERY.isParametersTablePresent());
      assertFalse(IDE.REST_SERVICE_DISCOVERY.isRequestFieldEnabled());
      assertEquals("/aa/testServ/", IDE.REST_SERVICE_DISCOVERY.getTextFromMethodField());
      assertEquals("n/a", IDE.REST_SERVICE_DISCOVERY.getTextFromRequestField());

      IDE.REST_SERVICE_DISCOVERY.clickOkButton();
      IDE.REST_SERVICE_DISCOVERY.waitClosed();

      //undeploy rest service
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
      IDE.OUTPUT.waitForMessageShow(1, 30);
      mess = IDE.OUTPUT.getOutputMessage(1);
      assertTrue(mess.contains("[INFO]"));
      assertTrue(mess.contains(FILE_NAME + " undeployed successfully."));

   }
}
