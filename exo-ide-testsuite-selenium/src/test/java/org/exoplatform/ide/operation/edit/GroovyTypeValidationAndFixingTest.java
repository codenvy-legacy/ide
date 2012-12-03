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
package org.exoplatform.ide.operation.edit;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: Dec 6, 2010 $
 *
 */
public class GroovyTypeValidationAndFixingTest extends BaseTest
{

   private final static String SERVICE_FILE_NAME = "java-type-validation-and-fixing.groovy";

   private final static String TEMPLATE_FILE_NAME = "java-type-validation-and-fixing.gtmpl";

   private final static String PROJECT = GroovyTypeValidationAndFixingTest.class.getSimpleName();

   private final static String ERROR_10_SERVICE_FILE =
      "ManyToOne' cannot be resolved to a type; 'Mandatory' cannot be resolved to a type; ";

   private final static String ERROR_11_SERVICE_FILE = "'Property' cannot be resolved to a type; ";

   private final static String ERROR_14_SERVICE_FILE = "'POST' cannot be resolved to a type; ";

   private final static String ERROR_16_SERVICE_FILE =
      "'Base64' cannot be resolved to a type; 'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; ";

   private final static String ERROR_17_SERVICE_FILE = "'Base64' cannot be resolved to a type; ";

   private final static String ERROR_39_SERVICE_FILE = "'ChromatticSession' cannot be resolved to a type; ";

   private final static String ERROR_41_SERVICE_FILE = "'ChromatticSession' cannot be resolved to a type; ";

   @BeforeClass
   public static void setUp()
   {
      String serviceFilePath = "src/test/resources/org/exoplatform/ide/operation/edit/" + SERVICE_FILE_NAME;
      String templateFilePath = "src/test/resources/org/exoplatform/ide/operation/edit/" + TEMPLATE_FILE_NAME;

      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, SERVICE_FILE_NAME, MimeType.GROOVY_SERVICE, serviceFilePath);
         VirtualFileSystemUtils.createFileFromLocal(link, TEMPLATE_FILE_NAME, MimeType.GROOVY_TEMPLATE,
            templateFilePath);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void testServiceFile() throws Exception
   {
      //step 1 open project and walidation error marks 
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + SERVICE_FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + SERVICE_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + SERVICE_FILE_NAME);
      //wait last number of string
      IDE.ERROR_MARKS.selectIframeWitErrorMarks(0);
      IDE.ERROR_MARKS.waitLineNumberAppearance(43);
      IDE.selectMainFrame();
      // test  error marks after open test file
      firstTestErrorMarks();

      //step 2 click on mark in 16 string-position, fix error and check changes
      IDE.ERROR_MARKS.selectIframeWitErrorMarks(0);
      IDE.ERROR_MARKS.clickOnErorMarker(16);
      IDE.selectMainFrame();
      IDE.ERROR_MARKS.waitDeclarationFormOpen("Base64");
      IDE.ERROR_MARKS.downMoveCursorInDeclForm(10);
      IDE.ERROR_MARKS.waitFqnDeclarationIsAppear("-java.util.prefs");
      IDE.ERROR_MARKS.selectAndInsertFqn("-java.util.prefs");
      IDE.ERROR_MARKS.waitErrorMarkerIsDisAppear(16);
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith(
         "// simple groovy script\n" + "import Path\n" + "import javax.ws.rs.GET\n" + "import some.pack.String\n"
            + "import javax.inject.Inject\n" + "import java.util.prefs.Base64\n"));

      //need for disappear values after fix of error marker 16
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      testAfterFixFirstError();

      //step 3 delete all code and  type new test-code. Tests error-markers
      // edit text
      IDE.EDITOR.deleteFileContent(0);
      // add test text
      IDE.EDITOR.typeTextIntoEditor(0, "Integer1 d \n" + "@POST \n"
         + "public Base64 hello(@PathParam(\"name\") Base64 name) {}");
      IDE.ERROR_MARKS.selectIframeWitErrorMarks(0);
      IDE.ERROR_MARKS.waitErrorMarkerIsAppear(3);
      testAfterClearAndTypeNewCode();

      //step 4 fix third error and check states in code and
      // marker-position
      IDE.ERROR_MARKS.selectIframeWitErrorMarks(0);
      IDE.ERROR_MARKS.clickOnErorMarker(3);
      IDE.selectMainFrame();
      IDE.ERROR_MARKS.waitDeclarationFormOpen("Base64");
      IDE.ERROR_MARKS.downMoveCursorInDeclForm(10);
      IDE.ERROR_MARKS.waitFqnDeclarationIsAppear("-java.util.prefs");
      IDE.ERROR_MARKS.selectAndInsertFqn("-java.util.prefs");
      testAfterFixFileWithNewCode();

      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

   // IDE-499: "Recognize error "cannot resolve to a type" within the Groovy Template file in the Code Editor."
   @Test
   public void testTemplateFile() throws Exception
   {
      // step 1 Open template file with test content, wait as file is parse
      //and check error marker positions of test-file
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEMPLATE_FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEMPLATE_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEMPLATE_FILE_NAME);
      IDE.ERROR_MARKS.selectIframeWitErrorMarks(0);
      IDE.ERROR_MARKS.waitErrorMarkerIsAppear(12);

      checkErrorMarkerAfterFirsOpeningInGtmpl();

      //step 2 click on error-marker 11 fix error in code
      // and check changes
      IDE.ERROR_MARKS.selectIframeWitErrorMarks(0);
      IDE.ERROR_MARKS.clickOnErorMarker(11);
      IDE.selectMainFrame();
      IDE.ERROR_MARKS.waitDeclarationFormOpen("Base64");
      IDE.ERROR_MARKS.downMoveCursorInDeclForm(10);

      IDE.ERROR_MARKS.waitFqnDeclarationIsAppear("-java.util.prefs");
      IDE.ERROR_MARKS.selectAndInsertFqn("-java.util.prefs");
      IDE.ERROR_MARKS.selectIframeWitErrorMarks(0);
      IDE.ERROR_MARKS.waitErrorMarkerIsDisAppear(11);
      IDE.ERROR_MARKS.waitErrorMarkerIsAppear(14);
      IDE.selectMainFrame();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith("<%\n" + "  import java.util.prefs.Base64\n" + "%>\n"));
      IDE.ERROR_MARKS.selectIframeWitErrorMarks(0);
      assertTrue(IDE.ERROR_MARKS.isErrorMarkerShow(12));
      assertTrue(IDE.ERROR_MARKS.isErrorMarkerShow(13));
      assertTrue(IDE.ERROR_MARKS.isErrorMarkerShow(9));
   }

   //--------------------------------------------------------------
   /**
    * check error-markers after first opening
    * test groovy template file 
    */
   private void checkErrorMarkerAfterFirsOpeningInGtmpl()
   {
      assertEquals(IDE.ERROR_MARKS.getTextFromErorMarker(6), "'Path' cannot be resolved to a type; ");
      assertFalse(IDE.ERROR_MARKS.isErrorMarkerShow(7));
      assertFalse(IDE.ERROR_MARKS.isErrorMarkerShow(8));
      assertEquals(IDE.ERROR_MARKS.getTextFromErorMarker(10), "'Path' cannot be resolved to a type; ");
      assertEquals(
         IDE.ERROR_MARKS.getTextFromErorMarker(11),
         "'Base64' cannot be resolved to a type; 'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; ");
      assertEquals(IDE.ERROR_MARKS.getTextFromErorMarker(12), "'Base64' cannot be resolved to a type; ");
      assertFalse(IDE.ERROR_MARKS.isErrorMarkerShow(13));
      assertFalse(IDE.ERROR_MARKS.isErrorMarkerShow(14));
      assertFalse(IDE.ERROR_MARKS.isErrorMarkerShow(17));
      assertFalse(IDE.ERROR_MARKS.isErrorMarkerShow(19));
      assertFalse(IDE.ERROR_MARKS.isErrorMarkerShow(21));
      IDE.selectMainFrame();
   }

   /**
    * chek states after fix error in marker #3
    * @throws Exception
    */
   private void testAfterFixFileWithNewCode() throws Exception
   {
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith("import java.util.prefs.Base64\n"));
      IDE.ERROR_MARKS.selectIframeWitErrorMarks(0);
      IDE.ERROR_MARKS.waitErrorMarkerIsAppear(1);
      assertTrue(IDE.ERROR_MARKS.isErrorMarkerShow(1));
      IDE.ERROR_MARKS.waitErrorMarkerIsAppear(2);
      assertTrue(IDE.ERROR_MARKS.isErrorMarkerShow(2));
      IDE.ERROR_MARKS.waitErrorMarkerIsAppear(3);
      assertTrue(IDE.ERROR_MARKS.isErrorMarkerShow(3));
      IDE.selectMainFrame();
   }

   /**
    * check erorrs in new code (after clear)
    */
   private void testAfterClearAndTypeNewCode()
   {
      assertEquals("'Integer1' cannot be resolved to a type; ", IDE.ERROR_MARKS.getTextFromErorMarker(1));
      assertEquals("'POST' cannot be resolved to a type; ", IDE.ERROR_MARKS.getTextFromErorMarker(2));
      assertEquals(
         "'Base64' cannot be resolved to a type; 'PathParam' cannot be resolved to a type; 'Base64' cannot be resolved to a type; ",
         IDE.ERROR_MARKS.getTextFromErorMarker(3));
      IDE.selectMainFrame();
   }

   /**
    * check position error markers after 
    * open of the test-file
    * @throws InterruptedException
    */
   private void firstTestErrorMarks() throws InterruptedException
   {
      IDE.ERROR_MARKS.selectIframeWitErrorMarks(0);
      assertFalse(IDE.ERROR_MARKS.isErrorMarkerShow(4));
      assertFalse(IDE.ERROR_MARKS.isErrorMarkerShow(6));
      assertFalse(IDE.ERROR_MARKS.isErrorMarkerShow(7));
      assertFalse(IDE.ERROR_MARKS.isErrorMarkerShow(8));

      assertEquals(ERROR_11_SERVICE_FILE, IDE.ERROR_MARKS.getTextFromErorMarker(11));
      assertEquals(ERROR_14_SERVICE_FILE, IDE.ERROR_MARKS.getTextFromErorMarker(14));
      assertEquals(ERROR_16_SERVICE_FILE, IDE.ERROR_MARKS.getTextFromErorMarker(16));
      assertEquals(ERROR_17_SERVICE_FILE, IDE.ERROR_MARKS.getTextFromErorMarker(17));
      assertEquals(ERROR_39_SERVICE_FILE, IDE.ERROR_MARKS.getTextFromErorMarker(39));
      assertEquals(ERROR_41_SERVICE_FILE, IDE.ERROR_MARKS.getTextFromErorMarker(41));
      IDE.selectMainFrame();
   }

   /**
    * check position error marks after fix in 16 string 
    * @throws InterruptedException
    */
   private void testAfterFixFirstError() throws InterruptedException
   {
      IDE.ERROR_MARKS.selectIframeWitErrorMarks(0);
      IDE.ERROR_MARKS.waitChangesInErrorMarker(
         "'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; ", 17);

      assertEquals("'PathParam' cannot be resolved to a type; 'ExoLogger' cannot be resolved to a type; ",
         IDE.ERROR_MARKS.getTextFromErorMarker(17));

      assertFalse(IDE.ERROR_MARKS.isErrorMarkerShow(18));
      assertFalse(IDE.ERROR_MARKS.isErrorMarkerShow(18));
      assertFalse(IDE.ERROR_MARKS.isErrorMarkerShow(37));
      assertTrue(IDE.ERROR_MARKS.isErrorMarkerShow(40));
      assertTrue(IDE.ERROR_MARKS.isErrorMarkerShow(42));
      IDE.selectMainFrame();
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }
}
