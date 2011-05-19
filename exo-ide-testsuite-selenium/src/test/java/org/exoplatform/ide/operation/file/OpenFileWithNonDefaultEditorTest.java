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
package org.exoplatform.ide.operation.file;

import java.io.IOException;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Navigation;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * IDE-109 Open file with non-default editor.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OpenFileWithNonDefaultEditorTest extends BaseTest
{

   private static String FOLDER = OpenFileWithNonDefaultEditorTest.class.getSimpleName();

   private static String FILE = "newHtmlFile.html";

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/";

   @BeforeClass
   public static void setUp()
   {
      deleteCookies();
      cleanRegistry();

      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER);
         VirtualFileSystemUtils.put(PATH + FILE, MimeType.TEXT_HTML, WS_URL + FOLDER + "/" + FILE);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Clean up cookie, registry, repository after each test of in the each class:<br>
    *   - selenium.deleteAllVisibleCookies();<br>
    *   - cleanRegistry();<br>
    *   - cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");<>
    * @throws IOException
    */
   @After
   public void testTearDown() throws IOException
   {
      try
      {
         deleteCookies();
         cleanRegistry();

         //cleanRepository(WS_URL);

         VirtualFileSystemUtils.delete(WS_URL + FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testOpenFileWithNonDefaultEditor() throws Exception
   {
      waitForRootElement();

      /*
       * 1. Open Folder
       */
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER + "/");
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);

      /*
       * 2. Select and open html file by doubleclicking
       */
      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER + "/" + FILE);

      /*
       * 3. Codemirror Editor must be opened 
       */
      IDE.EDITOR.checkCodeEditorOpened(0);

      /*
       * 4. Close editor
       */
      IDE.EDITOR.closeFile(0);

      /*
       * 5. Run File > Open With
       */
      IDE.OPENWITH.callFromMenu();

      /*
       * 6. Select CKEditor and click Open
       */
      IDE.OPENWITH.selectEditor(Navigation.Editor.CKEDITOR.getName());
      IDE.OPENWITH.clickOpenButton();
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      /*
       * 7. CKEditor must be opened
       */
      IDE.EDITOR.checkCkEditorOpened(0);

      /*
       * 8. Close editor
       */
      IDE.EDITOR.closeFile(0);

      /*
       * 9. Open html file by doubleclicking
       */
      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER + "/" + FILE);

      /*
       * 10. Check Codemirror must be opened
       */
      IDE.EDITOR.checkCodeEditorOpened(0);

      /*
       * 11. Run File > Open With
       */
      IDE.OPENWITH.callFromMenu();

      /*
       * 12. Select CKEditor, check "Use as default editor" and click Open
       */
      IDE.OPENWITH.selectEditor(Navigation.Editor.CKEDITOR.getName());
      IDE.OPENWITH.clickUseAsDefaultCheckBox();
      IDE.OPENWITH.clickOpenButton();

      /*
       * 13. Click Open when IDE asks for reopen file
       */
      IDE.ASK_DIALOG.assertOpened("IDE");
      IDE.ASK_DIALOG.clickYes();
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      /*
       * 14. Check CKEditor must be opened
       */
      IDE.EDITOR.checkCkEditorOpened(0);

      /*
       * 15. Close editor
       */
      IDE.EDITOR.closeFile(0);

      /*
       * 16. Open html file by doubleclicking
       */
      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER + "/" + FILE);

      /*
       * 17. Check CKEditor must be opened
       */
      IDE.EDITOR.checkCkEditorOpened(0);

      /*
       * 18. Close editor
       */
      IDE.EDITOR.closeTabIgnoringChanges(0);
   }

}
