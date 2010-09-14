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

import org.exoplatform.ide.operation.browse.NavigationPanelWithoutSelectedItemTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
@RunWith(Suite.class)
@SuiteClasses({PreviewHtmlFileTest.class, OpenFileWithNonDefaultEditorTest.class,
   OpenAndSaveXmlFileWithNonLatinNameTest.class, DeleteSeveralFilesSimultaniouslyTest.class,
   CreatingAndSavingAsNewFileTest.class, DisplayingWarningMessageTest.class, StoreOpenedFilesHistoryTest.class,
   ShowHideLineNumbersTest.class, CreateSaveAsXmlWithNonLatinNameTest.class, SaveAllFilesTest.class,
   SavingPreviouslyEditedFileTest.class, RenameClosedFileTest.class, RenameOpenedFileTest.class,
   DeletingFilesTest.class, DeletingXmlFileWithNonLatinNameTest.class, OpeningSavingAndClosingTabsWithFilesTest.class,
   NavigationPanelWithoutSelectedItemTest.class})
public class FileTestSuite
{

}
