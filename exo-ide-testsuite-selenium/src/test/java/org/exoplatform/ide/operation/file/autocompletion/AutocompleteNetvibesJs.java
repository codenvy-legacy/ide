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
package org.exoplatform.ide.operation.file.autocompletion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Autocomplete;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: AutocompleteNetvibesJs.java Jan 24, 2011 11:35:17 AM vereshchaka $
 *
 */
public class AutocompleteNetvibesJs extends BaseTest
{
   private static final String FOLDER_NAME = AutocompleteNetvibesJs.class.getSimpleName();
   
   private static final String NETVIBES_NAME = "Netvibes.html";
   
   private static final String NETVIBES_CONTENT = "<script type=\"text/javascript\">\n\n\n\n</script>";
   
   @Before
   public void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WORKSPACE_URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(NETVIBES_CONTENT.getBytes(), MimeType.UWA_WIDGET, 
            WORKSPACE_URL + FOLDER_NAME + "/" + NETVIBES_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
   
   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WORKSPACE_URL + FOLDER_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
   
   @Test
   public void testAutocomplateList() throws Exception
   {
      waitForRootElement();
      selectItemInWorkspaceTree(FOLDER_NAME);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      
      /*
       * 1. Open netvibes file.
       */
      openFileFromNavigationTreeWithCodeEditor(NETVIBES_NAME, false);
      
      /*
       * 2. Go inside <code><script></code> tag.
       */
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      
      /*
       * 3. Type text to file
       */
      typeTextIntoEditor(0, "var name = \"ivan\"\n");
      
      /*
       * 4. Press ctrl+enter to call autocomplete form.
       */
      Autocomplete.openForm(0);
      
      /*
       * Check, that all UWA snippets are present.
       */
      Autocomplete.checkElementPresent("name");
      Autocomplete.checkElementPresent("UWA");
      Autocomplete.checkElementPresent("flash");
      Autocomplete.checkElementPresent("pager");
      Autocomplete.checkElementPresent("tabs");
      Autocomplete.checkElementPresent("thumbnailed");
      
      /*
       * 5. Move down, and check, that javadoc form appeared with hint.
       */
      Autocomplete.moveCursorDown(1);
      Thread.sleep(TestConstants.SLEEP);
      
      /*
       * Check, that javadoc (description) panel appeared.
       */
      assertTrue(selenium.isElementPresent(Autocomplete.Locators.JAVADOC_DIV));
      assertEquals(UWA_CONTENT, selenium.getText(Autocomplete.Locators.JAVADOC_DIV));
      
      Autocomplete.moveCursorDown(1);
      Thread.sleep(TestConstants.SLEEP);
      
      assertTrue(selenium.isElementPresent(Autocomplete.Locators.JAVADOC_DIV));
      assertEquals(FLASH_CONTENT, selenium.getText(Autocomplete.Locators.JAVADOC_DIV));
      
      Autocomplete.moveCursorDown(4);
      Thread.sleep(TestConstants.SLEEP);
      
      assertTrue(selenium.isElementPresent(Autocomplete.Locators.JAVADOC_DIV));
      assertEquals(PAGER_CONTENT, selenium.getText(Autocomplete.Locators.JAVADOC_DIV));
      
      Autocomplete.moveCursorDown(1);
      Thread.sleep(TestConstants.SLEEP);
      
      assertTrue(selenium.isElementPresent(Autocomplete.Locators.JAVADOC_DIV));
      assertEquals(TABS_CONTENT, selenium.getText(Autocomplete.Locators.JAVADOC_DIV));
      
      Autocomplete.moveCursorDown(1);
      Thread.sleep(TestConstants.SLEEP);
      
      assertTrue(selenium.isElementPresent(Autocomplete.Locators.JAVADOC_DIV));
      assertEquals(THUMBNAILED_CONTENT, selenium.getText(Autocomplete.Locators.JAVADOC_DIV));
   }
   
   //--------Netvibes Snippets----------
   
   private static final String UWA_CONTENT = "// Json request snippet ////////////////////////////////////////////////////////////////////////////////\n"
      + "////////////////////////////////////////////////////////////////////////////////////////////////////////\n\n"
      + "// Params:\n"
      + "// * String: Json data url to fetch\n"
      + "// * Function: Callback\n"
      + "UWA.Data.getJson('http://api.twitter.com/1/statuses/public_timeline.json', function(jsonData){\n"
      + " // process your data (jsonData is a JS object\n"
      + "});";
   
   private static final String FLASH_CONTENT = "// Create flash object\n"
      + "var flashObject = new UWA.Controls.Flash({\n"
      + " 'url': 'http://www.youtube.com/v/6xJx1OPqQQg',\n" + " 'width': 480,\n"
      + " 'height': 385\n" + "});\n\n"
      + "// Inject into Dom\n"
      + "flashObject.inject(widget.body);";
   
   private static final String PAGER_CONTENT = "// Create Pager Control snippet ////////////////////////////////////////////////\n"
      + "////////////////////////////////////////////////////////////////////////////////\n\n"
      + "// Data we want to paginate\n"
      + "var dataSample = ['one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine', 'ten'];\n\n"
      + "// Items per page\n"
      + "var limit = 3;\n\n"
      + "// Starting offset\n"
      + "var offset = 0;\n\n"
      + "// Function to display data and pager\n"
      + "var display = function(offset){\n\n"
      + " // Display items\n"
      + " var text = '';\n"
      + " for (var i = offset, l = offset + limit; i<l && i<dataSample.length; i++){\n"
      + " text += dataSample[i] + ' ';\n"
      + " }\n"
      + " widget.body.setContent(text);\n\n"
      + " // Create UWA Pager Control\n"
      + " var pager = new UWA.Controls.Pager({\n"
      + " 'limit': limit, // items per page\n"
      + " 'offset': offset, // offset in our data\n"
      + " 'dataArray': dataSample // our data\n"
      + " });\n\n"
      + " // Define callback on page change\n"
      + " pager.onChange = function(newOffset){\n"
      + " // Recall display method with new offset\n"
      + " display(newOffset);\n"
      + " };\n\n"
      + " // Add pager to the widget\n"
      + " pager.inject(widget.body);\n"
      + "}\n\n"
      + "// Display data and pager\n" + "display(offset);";
   
   private static final String TABS_CONTENT = "// Create Tabview Control snippet //////////////////////////////////////////////\n"
      + "////////////////////////////////////////////////////////////////////////////////\n\n"
      + "// Create UWA TabView Control\n"
      + "var tabs = new UWA.Controls.TabView();\n\n"
      + "// Create tabs\n"
      + "tabs.addTab('tab1', {text: 'Tab One'});\n"
      + "tabs.addTab('tab2', {text: 'Tab Two', icon: 'http://cdn.netvibes.com/img/ipod.png', customInfo: 'custom'});\n"
      + "tabs.addTab('tab3', [{text: 'Tab Three A', icon: 'http://cdn.netvibes.com/img/ipod.png'}, {text: 'Tab Three B'}]);\n\n"
      + "// Fill tabs with our content\n"
      + "tabs.setContent('tab1', '<p>Tab #1</p>');\n"
      + "tabs.setContent('tab2', '<p>Tab #2</p>');\n"
      + "tabs.setContent('tab3', widget.createElement('p', {'text':'Tab #3'}));\n\n"
      + "// Observe activeTabChange events\n"
      + "// * String - tabName: internal name of new current tab\n"
      + "// * Object - tabData: Object you passed in to create the tab\n"
      + "tabs.observe('activeTabChange', function(tabName, tabData){\n"
      + " // We can dynamically change tab's content:\n"
      + " // -----------------------------------------\n\n"
      + " // get current Tab content (returns a HTMLElement)\n"
      + " var currentContent = tabs.getTabContent(tabName);\n\n"
      + " // remove infoDiv if previously created\n"
      + " var removeInfoDiv = currentContent.getElementsByTagName('div');\n"
      + " if (removeInfoDiv.length!=0){\n"
      + " removeInfoDiv[0].remove();\n"
      + " }\n\n"
      + " // Add some info from tabData\n"
      + " var moreInfo = widget.createElement('div').inject(currentContent);\n"
      + " moreInfo.setText(tabData.text);\n"
      + "});\n\n"
      + "// Select first tab\n"
      + "tabs.selectTab('tab1');\n\n"
      + "// Inject tabs in your dom\n"
      + "// * HTMLElement - where to inject your tabs\n"
      + "tabs.inject(widget.body);";
   
   private static final String THUMBNAILED_CONTENT = "// Create thumbnailed List snippet /////////////////////////////////////////////\n"
      + "////////////////////////////////////////////////////////////////////////////////\n\n"
      + "/*\n"
      + " Generated HTML:\n"
      + " ---------------\n"
      + " <div class=\"nv-thumbnailedList\">\n"
      + " <div class=\"item odd\">\n"
      + " <a href=\"http://www.google.com\"><img src=\"http://www.google.fr/images/logos/ps_logo2.png\" class=\"thumbnail\"></a>\n"
      + " <h3><a href=\"http://www.google.com\">#1: Google</a></h3>\n"
      + " <p class=\"description\">\n"
      + " Lorem ipsum ...\n"
      + " </p>\n"
      + " </div>\n"
      + " <div class=\"item even\">\n"
      + " <a href=\"http://www.yahoo.com\"><img src=\"http://l.yimg.com/a/i/ww/met/yahoo_logo_us_061509.png\" class=\"thumbnail\"></a>\n"
      + " <h3><a href=\"http://www.yahoo.com\">#2: Yahoo</a></h3>\n"
      + " <p class=\"description\">\n"
      + " Lorem ipsum ...\n"
      + " </p>\n"
      + " </div>\n"
      + " <div class=\"item odd\">\n"
      + " <a href=\"http://www.bing.com\"><img src=\"http://upload.wikimedia.org/wikipedia/commons/thumb/8/8c/Bing_Brand_Logo.PNG/230px-Bing_Brand_Logo.PNG\" class=\"thumbnail\"></a>\n"
      + " <h3><a href=\"http://www.bing.com\">#3: Bing</a></h3>\n"
      + " <p class=\"description\">\n"
      + " Lorem ipsum ...\n"
      + " </p>\n"
      + " </div>\n"
      + " </div>\n"
      + "*/\n\n"
      + "// Function to create an item\n"
      + "// returns HTMLElement\n"
      + "var getItem = function(title, link, description, imgUrl){\n"
      + " var containerElm = widget.createElement('div', {'class' : 'item'});\n\n"
      + " var imageElm = widget.createElement('a', {'href' : link}).inject(containerElm);\n"
      + " var titleElm = widget.createElement('h3').inject(containerElm);\n"
      + " var descriptionElm = widget.createElement('p', {'class' : 'description'}).inject(containerElm);\n\n"
      + " imageElm.setHTML('<img src=\"' + imgUrl + '\" class=\"thumbnail\" />');\n"
      + " titleElm.setHTML('<a href=\"' + link + '\">' + title + '</a>');\n"
      + " descriptionElm.setHTML(description);\n\n"
      + " return containerElm;\n"
      + "}\n\n"
      + "// Sample fake description\n"
      + "var description = \"Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\";\n\n"
      + "// Create list container and inject into the widget\n"
      + "var listElm = widget.createElement('div', {'class' : 'nv-thumbnailedList'});\n"
      + "listElm.inject(widget.body);\n\n"
      + "// Create item1\n"
      + "var item1 = getItem('#1: Google', 'http://www.google.com', description, 'http://www.google.fr/images/logos/ps_logo2.png');\n"
      + "item1.addClassName('odd');\n"
      + "item1.inject(listElm);\n\n"
      + "// Create item2\n"
      + "var item2 = getItem('#2: Yahoo', 'http://www.yahoo.com', description, 'http://l.yimg.com/a/i/ww/met/yahoo_logo_us_061509.png');\n"
      + "item2.addClassName('even');\n"
      + "item2.inject(listElm);\n\n"
      + "// Create item3\n"
      + "var item3 = getItem('#3: Bing', 'http://www.bing.com', description, 'http://upload.wikimedia.org/wikipedia/commons/thumb/8/8c/Bing_Brand_Logo.PNG/230px-Bing_Brand_Logo.PNG');\n"
      + "item3.addClassName('odd');\n" + "item3.inject(listElm);";

}
