/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.operation.java.refactoing;

import org.exoplatform.ide.BaseTest;

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public class RefactService extends BaseTest
{

   public void openRefacrProject(String project) throws Exception
   {

      IDE.PROJECT.EXPLORER.waitOpened();
      // step 1 Open project
      IDE.PROJECT.OPEN.openProject(project);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("src/main/java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("helloworld");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("refact");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("helloworld");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("GreetingController.java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("GreetingController.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("refact");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("RefactMethods.java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("RefactMethods.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }

   public void openOneJavaClassForRefactoring(String project) throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      // step 1 Open project
      IDE.PROJECT.OPEN.openProject(project);
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("src/main/java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("src/main/java");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("helloworld");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("refact");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("helloworld");
      IDE.PROJECT.PACKAGE_EXPLORER.waitItemInPackageExplorer("GreetingController.java");
      IDE.PROJECT.PACKAGE_EXPLORER.openItemWithDoubleClick("GreetingController.java");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }
}
