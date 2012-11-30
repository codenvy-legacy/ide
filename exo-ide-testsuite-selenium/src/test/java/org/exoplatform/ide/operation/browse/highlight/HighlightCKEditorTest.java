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
package org.exoplatform.ide.operation.browse.highlight;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 15, 2010 $
 * 
 */
public class HighlightCKEditorTest extends BaseTest
{

   private static String PROJECT = HighlightCKEditorTest.class.getSimpleName();

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/";

   private static String HTML_FILE_NAME = "newHtmlFile.html";

   private static String GADGET_FILE_NAME = "newGroovyFile.groovy";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, HTML_FILE_NAME, MimeType.TEXT_HTML, PATH + HTML_FILE_NAME);

         VirtualFileSystemUtils.createFileFromLocal(link, GADGET_FILE_NAME, MimeType.GOOGLE_GADGET, PATH
            + GADGET_FILE_NAME);
      }
      catch (IOException e)
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
   public void testHighlightCKEdditor() throws Exception
   {
     
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      
      //step 1 open file after close 'welcome' tab      
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + GADGET_FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + HTML_FILE_NAME);
      
      IDE.EDITOR.clickCloseEditorButton(0);
      IDE.EDITOR.waitTabNotPresent(0);

      //step 2 check highlight googlegadget in ckeditor
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + GADGET_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GADGET_FILE_NAME);
      IDE.EDITOR.clickDesignButton();
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GADGET_FILE_NAME);
      IDE.EDITOR.isHighlighterInEditor(0);

      
      IDE.EDITOR.forcedClosureFile(0);
      
      //TODO uncommit after fix issue IDE-1421
      //IDE.EDITOR.waitTabNotPresent(0);
      //      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + HTML_FILE_NAME);
      //      IDE.EDITOR.waitActiveFile(PROJECT + "/" + HTML_FILE_NAME);
      //      IDE.EDITOR.clickDesignButton();
      //      IDE.EDITOR.isHighlighterInCKEditor(1);

   }

}
