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
package org.exoplatform.ide.operation.autocompletion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for javascript autocomplete form
 * inside "script" tag in Netvibes files.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: AutocompleteNetvibesJsTest.java Jan 24, 2011 11:35:17 AM vereshchaka $
 *
 */
public class AutocompleteNetvibesJsTest extends CodeAssistantBaseTest
{
   private static final String NETVIBES_NAME = "Netvibes.html";

   private static final String NETVIBES_CONTENT = "<script type=\"text/javascript\">\n\n\n\n</script>";

   @BeforeClass
   public static void setUp() throws IOException
   {
      createProject(AutocompleteNetvibesJsTest.class.getSimpleName());

      VirtualFileSystemUtils.createFile(project.get(Link.REL_CREATE_FILE), NETVIBES_NAME, MimeType.UWA_WIDGET,
         NETVIBES_CONTENT);
   }

   @Before
   public void openFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/" + NETVIBES_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/" + NETVIBES_NAME);
      IDE.EDITOR.waitActiveFile(projectName + "/" + NETVIBES_NAME);
   }

   /**
    * Test, that autocomplete form contains all netvibes snippets.
    * Also check javadoc form, which displays hint for selected snippet.
    * @throws Exception
    */
   @Test
   public void testAutocomplateList() throws Exception
   {

      /*
       * 2. Go inside <code><script></code> tag.
       */
      IDE.EDITOR.moveCursorDown(0, 1);

      /*
       * 3. Type text to file
       */
      IDE.EDITOR.typeTextIntoEditor(0, "var name = \"ivan\"\n");

      /*
       * 4. Press ctrl+enter to call autocomplete form.
       */

      IDE.CODEASSISTANT.openForm();

      /*
       * Check, that all UWA snippets are present.
       */
      IDE.CODEASSISTANT.checkElementPresent("name");
      IDE.CODEASSISTANT.checkElementPresent("flash");
      IDE.CODEASSISTANT.checkElementPresent("jsonrequest");
      IDE.CODEASSISTANT.checkElementPresent("pager");
      IDE.CODEASSISTANT.checkElementPresent("tabs");
      IDE.CODEASSISTANT.checkElementPresent("thumbnailed");

      /*
       * 5. Move down, and check, that javadoc form appeared with hint.
       */
      IDE.CODEASSISTANT.typeToInput("flash");
      IDE.CODEASSISTANT.waitForDocPanelOpened();

      /*
       * Check, that javadoc (description) panel appeared.
       */

      Assert.assertTrue(IDE.CODEASSISTANT.getDocPanelText().startsWith(FLASH_CONTENT));

      //      Autocomplete.moveCursorDown(4);
      //      Thread.sleep(TestConstants.SLEEP);
      IDE.CODEASSISTANT.typeToInput("jsonrequest", true);

      IDE.CODEASSISTANT.waitForDocPanelOpened();
      Assert.assertTrue(IDE.CODEASSISTANT.getDocPanelText().startsWith(JSON_REQUEST_CONTENT));

      IDE.CODEASSISTANT.typeToInput("pager", true);

      IDE.CODEASSISTANT.waitForDocPanelOpened();
      Assert.assertTrue(IDE.CODEASSISTANT.getDocPanelText().startsWith(PAGER_CONTENT));

      IDE.CODEASSISTANT.typeToInput("tabs", true);

      IDE.CODEASSISTANT.waitForDocPanelOpened();
      Assert.assertTrue(IDE.CODEASSISTANT.getDocPanelText().startsWith(TABS_CONTENT));

      IDE.CODEASSISTANT.typeToInput("thumbnailed", true);

      IDE.CODEASSISTANT.waitForDocPanelOpened();

      IDE.CODEASSISTANT.closeForm();
   }

   /**
    * Test, that Flass template (snippet) inserted correctly.
    * @throws Exception
    */
   @Test
   public void testInsertNetvibesFlashTemplate() throws Exception
   {
      testSnippetInAutocomplete("flash", FLASH_CONTENT);
   }

   /**
    * Test, that Json Request template (snippet) inserted correctly.
    * @throws Exception
    */
   @Test
   public void testInsertNetvibesJsonRequestTemplate() throws Exception
   {
      testSnippetInAutocomplete("json", JSON_REQUEST_CONTENT);
   }

   /**
    * Test, that Pager template (snippet) inserted correctly.
    * @throws Exception
    */
   @Test
   public void testInsertNetvibesPagerTemplate() throws Exception
   {
      testSnippetInAutocomplete("pager", PAGER_CONTENT);
   }

   /**
    * Test, that Tabs template (snippet) inserted correctly.
    * @throws Exception
    */
   @Test
   public void testInsertNetvibesTabsTemplate() throws Exception
   {
      testSnippetInAutocomplete("tabs", TABS_CONTENT);
   }

   /**
    * Test, that Thumbnailed template (snippet) inserted correctly.
    * @throws Exception
    */
   @Test
   public void testInsertNetvibesThumbnailedTemplate() throws Exception
   {
      testSnippetInAutocomplete("thumbnailed", THUMBNAILED_CONTENT);
   }

   /**
    * Open netvibes file (with one tag "script".
    * Type some text and call autocomplete form.
    * Move down the autocomplete list on <code>rowNumber</code> rows
    * and press enter.
    * Check, that text in file contains snippet.
    * 
    * @param rowNumber - number of rows to move down in autocomplete list
    * @param snippetTemplate - text of snippet, that will be inserted
    * @throws Exception
    */
   private void testSnippetInAutocomplete(String name, String snippetTemplate) throws Exception
   {

      /*
       * 2. Go inside <code><script></code> tag.
       */
      IDE.EDITOR.moveCursorDown(0, 1);

      /*
       * 3. Type text to file
       */
      IDE.EDITOR.typeTextIntoEditor(0, "var name = \"ivan\"\n");

      /*
       * 4. Press ctrl+enter to call autocomplete form.
       */
      IDE.CODEASSISTANT.openForm();

      /*
       * 5. Move down, and click enter
       */
      IDE.CODEASSISTANT.typeToInput(name);

      IDE.CODEASSISTANT.insertSelectedItem();

      checkText(IDE.EDITOR.getTextFromCodeEditor(0), snippetTemplate);
   }

   /**
    * Compares two text in such way:
    * split both strings of lines (by \n)
    * and compare each line (before trim them).
    * 
    * @param fileContent
    * @param snippetContent
    */
   private void checkText(String fileContent, String snippetContent)
   {
      final String[] fileLines = fileContent.split("\n");
      final String[] snippetLines = snippetContent.split("\n");
      int index = -1;
      for (int i = 0; i < fileLines.length; i++)
      {
         if (fileLines[i].trim().equals(snippetLines[0].trim()))
         {
            index = i;
            break;
         }
      }
      if (index < 0)
      {
         fail("File content doesn't contains snippet content");
      }
      for (int i = 0; i < snippetLines.length; i++)
      {
         assertEquals(snippetLines[i].trim(), fileLines[i + index].trim());
      }
   }

   //--------Netvibes Snippets----------

   private static final String JSON_REQUEST_CONTENT =
      "// Json request snippet ////////////////////////////////////////////////////////////////////////////////\n"
         + "////////////////////////////////////////////////////////////////////////////////////////////////////////\n\n"
         + "// Params:\n" + "// * String: Json data url to fetch\n" + "// * Function: Callback\n"
         + "UWA.Data.getJson('http://api.twitter.com/1/statuses/public_timeline.json', function(jsonData){\n";

   private static final String FLASH_CONTENT = "// Create flash object\n"
      + "var flashObject = new UWA.Controls.Flash({";

   private static final String PAGER_CONTENT =
      "// Create Pager Control snippet ////////////////////////////////////////////////\n"
         + "////////////////////////////////////////////////////////////////////////////////\n\n"
         + "// Data we want to paginate";

   private static final String TABS_CONTENT =
      "// Create Tabview Control snippet //////////////////////////////////////////////\n"
         + "////////////////////////////////////////////////////////////////////////////////\n\n"
         + "// Create UWA TabView Control";

   private static final String THUMBNAILED_CONTENT =
      "// Create thumbnailed List snippet /////////////////////////////////////////////\n"
         + "////////////////////////////////////////////////////////////////////////////////\n\n" + "/*\n"
         + " Generated HTML:\n" + " ---------------\n" + " <div class=\"nv-thumbnailedList\">";

}
