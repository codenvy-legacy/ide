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
package org.exoplatform.ide.operation.gtmpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Test for preview of groovy template.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/

public class GroovyTemplatePreviewTest extends BaseTest
{

   private static final String PROJECT = GroovyTemplatePreviewTest.class.getSimpleName();
   
   private final static String FILE_NAME = "GroovyTemplatePreviewTest.gtmpl";

   private static String GTMPL = "<html><body><% import org.exoplatform.services.security.Identity\n"
      + " import org.exoplatform.services.security.ConversationState\n "
      + " ConversationState curentState = ConversationState.getCurrent();\n"
      + " if (curentState != null){ Identity identity = curentState.getIdentity();\n"
      + " 3.times { println \"Hello \" + identity.getUserId()}}%><br></body></html>";

   @BeforeClass
   public static void setUp()
   {

      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFile(link, FILE_NAME, MimeType.GROOVY_TEMPLATE, GTMPL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
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
         e.printStackTrace();
      }
   }

   @Test
   public void testGtmplPreview() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      
      //open file
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitTabPresent(1);
      
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_GROOVY_TEMPLATE_PREVIEW);
      IDE.PREVIEW.waitGtmplPreviewOpened();
      IDE.PREVIEW.selectPreviewIFrame();
      assertTrue(selenium().isTextPresent("root"));
      IDE.selectMainFrame();
      //XXX Switch frames doesn't work with Google Chrome WebDriver without sleep.
      //Issue - http://code.google.com/p/selenium/issues/detail?id=1969
      Thread.sleep(500);
      
      //close preview tab and open again
      IDE.PREVIEW.closeView();
      IDE.PREVIEW.waitGtmplPreviewClosed();
      assertFalse(IDE.PREVIEW.isGtmplPreviewOpened());
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_GROOVY_TEMPLATE_PREVIEW);
      IDE.PREVIEW.waitGtmplPreviewOpened();
      IDE.PREVIEW.selectPreviewIFrame();
      assertTrue(selenium().isTextPresent("root"));
      IDE.selectMainFrame();
   }

}
