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

import com.google.gwt.editor.client.Editor.Ignore;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.io.IOException;

/**
 * IDE-156:HotKeys customization.
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class HotkeysInFCKEditorTest extends BaseTest
{
   private static final String PROJECT = HotkeysInFCKEditorTest.class.getSimpleName();

   private static final String TEST_FOLDER = "CK_HotkeysFolder";

   private static final String FILE_NAME = "GoogleGadget.xml";

   private static final String FILE_NAME2 = "GoogleGadget.xml";

   @BeforeClass
   public static void setUp() throws Exception
   {
      try
      {

         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + TEST_FOLDER);
         VirtualFileSystemUtils.put("src/test/resources/org/exoplatform/ide/miscellaneous/GoogleGadget.xml",
            MimeType.GOOGLE_GADGET, WS_URL + PROJECT + "/" + TEST_FOLDER + "/" + FILE_NAME);
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

   /**
    * IDE-156:HotKeys customization ----- 3-5 ------------
    * 
    * @throws Exception
    */

   @Test
   public void testFormatingTextHotkeysForFCKEditor() throws Exception
   {
      //step one open test file, switch to ck_editor,
      //delete content (hotkey ctrl+a, press del), checking
      // press short key ctrl+z and check restore
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FOLDER);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEST_FOLDER);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FOLDER + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEST_FOLDER + "/" + FILE_NAME);
      IDE.EDITOR.waitTabPresent(1);
      //need for redraw design button 
      Thread.sleep(1000);
      IDE.CK_EDITOR.clickDesignButton(0);
      IDE.CK_EDITOR.waitToolsCkEditor(1);
      IDE.CK_EDITOR.waitIsTextPresent("Hello, world!", 1 );
      IDE.CK_EDITOR.deleteFileContentInCKEditor(1);
      IDE.CK_EDITOR.getTextFromCKEditor(1);
      assertEquals("", IDE.CK_EDITOR.getTextFromCKEditor(1));
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, Keys.CONTROL.toString() + "z");
      assertEquals("Hello, world!", IDE.CK_EDITOR.getTextFromCKEditor(1));
      //check bold formating
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, Keys.CONTROL.toString() + "a");
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, Keys.CONTROL.toString() + "b");
      IDE.CK_EDITOR.waitBoldTextPresent("Hello, world!", 1);
      //check bold-italic formating
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, Keys.CONTROL.toString() + "a");
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, Keys.CONTROL.toString() + "i");
      IDE.CK_EDITOR.waitItalicBoldTextPresent("Hello, world!", 1);
      //check italic firmating
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, Keys.CONTROL.toString() + "b");
      IDE.CK_EDITOR.waitItalicTextPresent("Hello, world!", 1);
      //check no formating
      IDE.CK_EDITOR.typeTextIntoCkEditor(1, Keys.CONTROL.toString() + "i");
      assertEquals("Hello, world!", IDE.CK_EDITOR.getTextFromCKEditor(1));
      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

   @Test
   public void testTypicalHotkeysInFCKEditor() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FOLDER + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEST_FOLDER + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEST_FOLDER + "/" + FILE_NAME);
      IDE.CK_EDITOR.clickDesignButton(1);
      IDE.CK_EDITOR.waitToolsCkEditor(2);
      IDE.CK_EDITOR.waitIsTextPresent("Hello, world!", 2);
      IDE.CK_EDITOR.typeTextIntoCkEditor(2, Keys.CONTROL.toString() + "a");
      IDE.CK_EDITOR.typeTextIntoCkEditor(2, Keys.CONTROL.toString() + "c");
      IDE.CK_EDITOR.typeTextIntoCkEditor(2, Keys.CONTROL.toString() + "v");
      IDE.CK_EDITOR.typeTextIntoCkEditor(2, Keys.CONTROL.toString() + "v");
      assertEquals("Hello, world! Hello, world!", IDE.CK_EDITOR.getTextFromCKEditor(2));
      IDE.CK_EDITOR.typeTextIntoCkEditor(2, Keys.CONTROL.toString() + "a");
      IDE.CK_EDITOR.typeTextIntoCkEditor(2, Keys.CONTROL.toString() + "x");
      assertEquals("", IDE.CK_EDITOR.getTextFromCKEditor(2));
      IDE.CK_EDITOR.typeTextIntoCkEditor(2, Keys.CONTROL.toString() + "v");
      assertEquals("Hello, world! Hello, world!", IDE.CK_EDITOR.getTextFromCKEditor(2));
      IDE.CK_EDITOR.typeTextIntoCkEditor(2, Keys.CONTROL.toString() + "s");
      IDE.EDITOR.waitNoContentModificationMark(FILE_NAME);
      IDE.CK_EDITOR.typeTextIntoCkEditor(2, Keys.CONTROL.toString() + "n");
      IDE.TEMPLATES.waitOpened();

   }

}
