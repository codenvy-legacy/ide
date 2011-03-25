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
package org.exoplatform.ide.git.server.jgit;

import org.exoplatform.ide.git.shared.AddRequest;

import java.io.File;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AddTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class AddTest extends BaseTest
{
   public void testNoAdd() throws Exception
   {
      File workDir = getDefaultRepository().getWorkTree();
      File file1 = addFile(workDir, "testNoAdd", CONTENT);
      checkNoFilesInCache(getDefaultRepository(), file1);
   }

   public void testUpdate() throws Exception
   {
      File workDir = getDefaultRepository().getWorkTree();
      File file1 = addFile(workDir, "testUpdate", CONTENT);
      AddRequest addRequest = new AddRequest();
      addRequest.setUpdate(true);
      getDefaultConnection().add(addRequest);
      // File not added in index. Existed file re-indexed if modified.
      checkNoFilesInCache(getDefaultRepository(), file1);
   }

   public void testAdd() throws Exception
   {
      File workDir = getDefaultRepository().getWorkTree();
      File file1 = addFile(workDir, "testAdd", CONTENT);
      AddRequest addRequest = new AddRequest();
      getDefaultConnection().add(addRequest);
      checkFilesInCache(getDefaultRepository(), file1);
   }

   public void testAddAfterRemove() throws Exception
   {
      File workDir = getDefaultRepository().getWorkTree();
      File readMe = new File(workDir, "README.txt");
      String relativePath = calculateRelativePath(workDir, readMe);
      checkFilesInCache(getDefaultRepository(), readMe);
      readMe.delete();
      AddRequest addRequest = new AddRequest();
      // If 'update' is 'true' then removed files should be removed from index.
      addRequest.setUpdate(true);
      getDefaultConnection().add(addRequest);
      checkNoFilesInCache(getDefaultRepository(), relativePath);
   }
}
