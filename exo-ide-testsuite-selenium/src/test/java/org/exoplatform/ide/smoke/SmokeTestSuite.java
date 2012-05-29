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
package org.exoplatform.ide.smoke;

import org.exoplatform.ide.extension.maven.client.BuildSuccessedTest;
import org.exoplatform.ide.git.AddTest;
import org.exoplatform.ide.git.InitRepositoryTest;
import org.exoplatform.ide.git.PushTest;
import org.exoplatform.ide.operation.autocompletion.java.JavaCodeAssistantTest;
import org.exoplatform.ide.operation.cutcopy.CopyFoldersAndFilesTest;
import org.exoplatform.ide.operation.edit.JavaTypeValidationAndFixingTest;
import org.exoplatform.ide.operation.edit.outline.CodeOutLineJavaTest;
import org.exoplatform.ide.operation.file.CreatingAndSavingAsNewFileTest;
import org.exoplatform.ide.operation.restservice.RESTServiceDeployUndeployTest;
import org.exoplatform.ide.operation.upload.UploadingZippedFolderTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
@RunWith(Suite.class)
@SuiteClasses({CopyFoldersAndFilesTest.class, CreatingAndSavingAsNewFileTest.class, JavaCodeAssistantTest.class,
   CodeOutLineJavaTest.class, JavaTypeValidationAndFixingTest.class, InitRepositoryTest.class, AddTest.class,
   PushTest.class, RESTServiceDeployUndeployTest.class, BuildSuccessedTest.class, UploadingZippedFolderTest.class })
public class SmokeTestSuite
{

}
