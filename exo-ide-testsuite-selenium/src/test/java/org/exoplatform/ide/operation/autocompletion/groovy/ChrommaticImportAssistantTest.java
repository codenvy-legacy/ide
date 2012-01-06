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
package org.exoplatform.ide.operation.autocompletion.groovy;

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: ChrommaticImportAssistant Jan 24, 2011 5:42:31 PM evgen $
 */
public class ChrommaticImportAssistantTest extends CodeAssistantBaseTest
{

   private final static String FILE_NAME = "importChrommatic.groovy";

   @BeforeClass
   public static void setUp() throws IOException
   {
      createProject(ChrommaticImportAssistantTest.class.getSimpleName());
      String serviceFilePath =
         "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/importAssistantChrommatic.groovy";
      VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), FILE_NAME,
         MimeType.CHROMATTIC_DATA_OBJECT, serviceFilePath);
   }

   @Before
   public void openFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(projectName + "/" + FILE_NAME);
   }

   @Test
   public void testChrommaticImportAssistant() throws Exception
   {
      //sleep to give editor parse file content
      Thread.sleep(TestConstants.SLEEP);
      IDE.CODEASSISTANT.clickOnLineNumer(2);

      IDE.CODEASSISTANT.waitForImportAssistForOpened();

      IDE.CODEASSISTANT.selectImportProposal("Base64");
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("import java.util.prefs.Base64"));
   }

}
