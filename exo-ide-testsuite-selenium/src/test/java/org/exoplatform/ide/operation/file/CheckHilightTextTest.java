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

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * 
 *
 */
public class CheckHilightTextTest extends BaseTest
{

   private final static String PROJECT = CheckHilightTextTest.class.getSimpleName();

   private final static String HTML_FILE_NAME = "newHtmlFile.html";

   private final static String CSS_FILE_NAME = "newCssFile.css";

   private final static String JS_FILE_NAME = "newJavaScriptFile.js";

   private final static String GADGET_FILE_NAME = "newGoogleGadget.gadget";

   private final static String GROOVY_FILE_NAME = "newGroovyFile.groovy";

   private final static String XML_FILE_NAME = "newXMLFile.xml";

   private final static String TXT_FILE_NAME = "newTextFile.txt";

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.put(PATH + HTML_FILE_NAME, MimeType.TEXT_HTML, WS_URL + PROJECT + "/" + HTML_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + CSS_FILE_NAME, MimeType.TEXT_CSS, WS_URL + PROJECT + "/" + CSS_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + JS_FILE_NAME, MimeType.APPLICATION_JAVASCRIPT, WS_URL + PROJECT + "/"
            + JS_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + GADGET_FILE_NAME, MimeType.GOOGLE_GADGET, WS_URL + PROJECT + "/"
            + GADGET_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + GROOVY_FILE_NAME, MimeType.GROOVY_SERVICE, WS_URL + PROJECT + "/"
            + GROOVY_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + XML_FILE_NAME, MimeType.TEXT_XML, WS_URL + PROJECT + "/" + XML_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + TXT_FILE_NAME, MimeType.TEXT_PLAIN, WS_URL + PROJECT + "/" + TXT_FILE_NAME);
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
      catch (Exception e)
      {
      }
   }

   @Before
   public void openProject() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + XML_FILE_NAME);
      IDE.LOADER.waitClosed();
      IDE.EDITOR.waitTabPresent(0);
      IDE.LOADER.waitClosed();
      IDE.WELCOME_PAGE.close();
      IDE.WELCOME_PAGE.waitClose();
   }

   @Test
   public void checkXML() throws InterruptedException, Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + XML_FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + XML_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + XML_FILE_NAME);

      /*
       *1. Check highlighting XML
       */
      checkHilightXML();
      IDE.EDITOR.closeFile(0);
      IDE.EDITOR.waitTabNotPresent(0);

      /*
      * 2. Check highlighting TXT
      */
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TXT_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TXT_FILE_NAME);
      checkHiligtTXT();
      IDE.EDITOR.closeFile(0);
      IDE.EDITOR.waitTabNotPresent(0);

      /*
      * 3. Check highlighting JavaScript
      */
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + JS_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + JS_FILE_NAME);
      checkHilightJavaScript();
      IDE.EDITOR.closeFile(0);
      IDE.EDITOR.waitTabNotPresent(0);

      /*
       * 4. Check highlighting in HTML
       */
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + HTML_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + HTML_FILE_NAME);
      checkHilightHTML();
      IDE.EDITOR.closeFile(0);
      IDE.EDITOR.waitTabNotPresent(0);

      /*
       * 5. Check highlighting in GROOVY FILE
       */
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + GROOVY_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GROOVY_FILE_NAME);
      checkHilightGroovy();
      IDE.EDITOR.closeFile(0);
      IDE.EDITOR.waitTabNotPresent(0);

      /*
       * 6. Check highlighting in GOOGLE_GADGET
       */
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + GADGET_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + GADGET_FILE_NAME);
      checkHiligtGoogleGadget();
      IDE.EDITOR.closeFile(0);
      IDE.EDITOR.waitTabNotPresent(0);

      /*
       * 7. Check highlighting in CSS
       */
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + CSS_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + CSS_FILE_NAME);
      chekHilightingInCssFile();
      IDE.EDITOR.closeFile(0);
      IDE.EDITOR.waitTabNotPresent(0);
   }

   /**
    * checking key tags in  test - XML file 
    * @throws Exception
    */
   public void checkHilightXML() throws Exception
   {
      //check color highlight in tags "Module" "xml" "userpref" and not highlight "xml-text"
      // for searching elements used xpath, because check color highlight and location text in DOM
      IDE.EDITOR.selectIFrameWithEditor(0);
      driver.findElement(By.xpath("//body[@class=\"editbox\"]/span[1][@class='xml-processing' and text()=\"<?xml \"]"))
         .isDisplayed();
      driver.findElement(By.xpath("//body[@class='editbox']/span[4][@class='xml-tagname' and text()=\"Module\"]"))
         .isDisplayed();
      driver.findElement(By.xpath("//body[@class='editbox']/span[8][@class='xml-tagname' and text()=\"UserPref\"]"))
         .isDisplayed();
      driver
         .findElement(
            By.xpath("//body[@class='editbox']/span[10][@class='xml-text' and text()='name=\"last_location\" datatype=\"hidden\"']"))
         .isDisplayed();
      IDE.selectMainFrame();
   }

   /**
    * checking color highlight in TXT file
    * @throws Exception
    */
   public void checkHiligtTXT() throws Exception
   {
      //check color highlight in "text content" - word
      // for searching elements used xpath, because check color highlight and location text in DOM
      IDE.EDITOR.selectIFrameWithEditor(1);
      driver.findElement(By.xpath("//body[@class='editbox']/span[@class='xml-text' and text()=\"text content\"]"))
         .isDisplayed();
      IDE.selectMainFrame();
   }

   /**
    * checking key tags in  test - Java Script file
    * @throws Exception
    */
   public void checkHilightJavaScript() throws Exception
   {
      IDE.EDITOR.selectIFrameWithEditor(2);
      //chek next elements in example file: var, undo,redo, x+y, 44.4
      driver
         .findElement(
            By.xpath("//body[@class='editbox']/span[1][@class='js-comment' and text()=\"//Here you see some JavaScript code. Mess around with it to get\"]"))
         .isDisplayed();
      driver.findElement(By.xpath("//body[@class='editbox']/span[5][@class='js-keyword' and text()=\"var \"]"))
         .isDisplayed();
      driver.findElement(By.xpath("//body[@class='editbox']/span[21][@class='js-string' and text()='\"undo\"']"))
         .isDisplayed();
      driver.findElement(By.xpath("//body[@class='editbox']/span[26][@class='js-string' and text()='\"redo\"']"))
         .isDisplayed();
      driver.findElement(By.xpath("//body[@class='editbox']/span[44][@class='js-keyword' and text()=\"var \"]"))
         .isDisplayed();
      driver.findElement(By.xpath("//body[@class='editbox']/span[45][@class='js-variabledef' and text()=\"y\"]"))
         .isDisplayed();
      driver.findElement(By.xpath("//body[@class='editbox']/span[46][@class='js-operator' and text()=\"=\"]"))
         .isDisplayed();
      driver.findElement(By.xpath("//body[@class='editbox']/span[47][@class='js-atom' and text()=\"44.4\"]"))
         .isDisplayed();
      driver.findElement(By.xpath("//body[@class='editbox']/span[51][@class='js-localvariable' and text()=\"x\"]"))
         .isDisplayed();
      driver.findElement(By.xpath("//body[@class='editbox']/span[52][@class='js-operator' and text()=\"+\"]"))
         .isDisplayed();
      driver.findElement(By.xpath("//body[@class='editbox']/span[53][@class='js-localvariable' and text()=\"y\"]"))
         .isDisplayed();
      IDE.selectMainFrame();
   }

   /**
    * check keys elements in the JavaScript file
    * @throws Exception
    */
   public void checkHilightHTML() throws Exception
   {
      IDE.EDITOR.selectIFrameWithEditor(3);
      //chek next elements in example file: open and closed tags html, function foo (bar, bar), 1px, #448888
      driver.findElement(By.xpath("//body[@class='editbox']/span[1][@class='xml-punctuation' and text()=\"<\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[2][@class='xml-tagname' and text()=\"html\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[3][@class='xml-punctuation' and text()=\">\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[23][@class='js-keyword' and text()=\"function \"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[24][@class='js-variable' and text()=\"foo\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[25][@class='js-punctuation' and text()=\"(\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[26][@class='js-variabledef' and text()=\"bar\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[66][@class='css-unit' and text()=\"1px \"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[96][@class='css-colorcode' and text()=\"#448888\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[121][@class='xml-punctuation' and text()=\"</\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[122][@class='xml-tagname' and text()=\"html\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[123][@class='xml-punctuation' and text()=\">\"]"));
      IDE.selectMainFrame();
   }

   /**
    * check keys elements in the GROOVY file
    * @throws Exception
    */
   public void checkHilightGroovy() throws Exception
   {
      IDE.EDITOR.selectIFrameWithEditor(4);
      //chek next elements in example file: import, public class, @Path, ("hello world")
      driver.findElement(By
         .xpath("//body[@class='editbox']/span[1][@class='groovyComment' and text()=\"//simple groovy script\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[10][@class='javaKeyword' and text()=\"import \"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[32][@class='javaModifier' and text()=\"public \"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[33][@class='javaType' and text()=\"class \"]"));

      driver.findElement(By.xpath("//body[@class='editbox']/span[37][@class='javaAnnotation' and text()=\"@Path \"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[40][@class='groovyString' and text()=\"h\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[41][@class='groovyString' and text()=\"e\"]"));
      IDE.selectMainFrame();
   }

   /**
    * check keys elements in the GOOGLE GADGET file
    * @throws Exception
    */
   public void checkHiligtGoogleGadget() throws Exception
   {
      IDE.EDITOR.selectIFrameWithEditor(5);
      //chek next elements in example file: <?xml, <Module>,</Module> 
      driver.findElement(By.xpath("//body[@class='editbox']/span[1][@class='xml-processing' and text()=\"<?xml \"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[3][@class='xml-punctuation' and text()=\"<\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[4][@class='xml-tagname' and text()=\"Module\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[5][@class='xml-punctuation' and text()=\">\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[16][@class='xml-attname' and text()=\"type\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[17][@class='xml-punctuation' and text()=\"=\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[18][@class='xml-attribute' and text()='\"html\"']"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[39][@class='js-variable' and text()=\"alert\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[40][@class='js-punctuation' and text()=\"(\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[41][@class='js-string' and text()=\"'quux'\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[103][@class='css-colorcode' and text()=\"#448888\"]"));
      IDE.selectMainFrame();
   }

   /**
    * check keys elements in the CSS file
    * @throws Exception
    */
   public void chekHilightingInCssFile() throws Exception
   {
      IDE.EDITOR.selectIFrameWithEditor(6);
      // chek next elements in example file: /*Some example CSS*,"6em, #000, bold, !important
      driver.findElement(By
         .xpath("//body[@class='editbox']/span[1][@class='css-comment' and text()=\"/*Some example CSS*/\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[2][@class='css-at' and text()=\"@import \"]"));

      driver.findElement(By.xpath("//body[@class='editbox']/span[16][@class='css-unit' and text()=\"6em\"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[29][@class='css-colorcode' and text()=\"#000\"]"));
      driver.findElement(By
         .xpath("//body[@class='editbox']/span[33][@class='css-selector' and text()=\"#navigation \"]"));
      driver.findElement(By.xpath("//body[@class='editbox']/span[39][@class='css-value' and text()=\"bold\"]"));
      driver.findElement(By
         .xpath("//body[@class='editbox']/span[45][@class='css-important' and text()=\"!important\"]"));
      IDE.selectMainFrame();
   }

}
