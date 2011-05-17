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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.exoplatform.ide.git.server.DiffPage;
import org.exoplatform.ide.git.shared.DiffRequest;
import org.exoplatform.ide.git.shared.DiffRequest.DiffType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: DiffTest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class DiffTest extends BaseTest
{
   private Git git;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      Repository repository = getDefaultRepository();
      addFile(repository.getWorkTree(), "aaa", "AAA\n");
      new File(repository.getWorkTree(), "README.txt").delete();
      git = new Git(repository);
   }

   public void testDiffNameStatus() throws Exception
   {
      List<String> diff = readDiff(new DiffRequest(null, DiffType.NAME_STATUS, false, 0));
      assertEquals(2, diff.size());
      assertTrue(diff.contains("D\tREADME.txt"));
      assertTrue(diff.contains("A\taaa"));
   }

   public void testDiffNameStatusWithCommits() throws Exception
   {
      git.add().addFilepattern("aaa").call();
      git.rm().addFilepattern("README.txt").call();
      git.commit().setMessage("testDiffNameStatusWithCommits").setAuthor("andrey", "andrey@mail.com").call();
      List<String> diff = readDiff(new DiffRequest(null, DiffType.NAME_STATUS, false, 0, "HEAD^", "HEAD"));
      assertEquals(2, diff.size());
      assertTrue(diff.contains("D\tREADME.txt"));
      assertTrue(diff.contains("A\taaa"));
   }

   public void testDiffNameStatusWithFileFilter() throws Exception
   {
      List<String> diff = readDiff(new DiffRequest(new String[]{"aaa"}, DiffType.NAME_STATUS, false, 0));
      assertEquals(1, diff.size());
      assertTrue(diff.contains("A\taaa"));
   }

   public void testDiffNameStatusWithFileFilterAndCommits() throws Exception
   {
      git.add().addFilepattern("aaa").call();
      git.rm().addFilepattern("README.txt").call();
      git.commit().setMessage("testDiffNameStatusWithFileFilterAndCommits").setAuthor("andrey", "andrey@mail.com")
         .call();
      List<String> diff = readDiff(new DiffRequest(new String[]{"aaa"}, DiffType.NAME_STATUS, false, 0, "HEAD^1", "HEAD"));
      assertEquals(1, diff.size());
      assertTrue(diff.contains("A\taaa"));
   }

   public void testDiffNameOnly() throws Exception
   {
      List<String> diff = readDiff(new DiffRequest(null, DiffType.NAME_ONLY, false, 0));
      assertEquals(2, diff.size());
      assertTrue(diff.contains("README.txt"));
      assertTrue(diff.contains("aaa"));
   }

   public void testDiffNameOnlyWithCommits() throws Exception
   {
      git.add().addFilepattern("aaa").call();
      git.rm().addFilepattern("README.txt").call();
      git.commit().setMessage("testDiffNameOnlyWithCommits").setAuthor("andrey", "andrey@mail.com").call();
      List<String> diff = readDiff(new DiffRequest(null, DiffType.NAME_ONLY, false, 0, "HEAD^1", "HEAD"));
      assertEquals(2, diff.size());
      assertTrue(diff.contains("README.txt"));
      assertTrue(diff.contains("aaa"));
   }

   public void testDiffNameOnlyCached() throws Exception
   {
      git.add().addFilepattern("aaa").call();
      List<String> diff = readDiff(new DiffRequest(null, DiffType.NAME_ONLY, false, 0, "HEAD", true));
      assertEquals(1, diff.size());
      assertTrue(diff.contains("aaa"));
   }

   public void testDiffNameOnlyCachedNoCommit() throws Exception
   {
      git.add().addFilepattern("aaa").call();
      List<String> diff = readDiff(new DiffRequest(null, DiffType.NAME_ONLY, false, 0, null, true));
      assertEquals(1, diff.size());
      assertTrue(diff.contains("aaa"));
   }

   public void testDiffNameOnlyWorkingTree() throws Exception
   {
      List<String> diff = readDiff(new DiffRequest(null, DiffType.NAME_ONLY, false, 0, "HEAD", false));
      assertEquals(2, diff.size());
      assertTrue(diff.contains("README.txt"));
      assertTrue(diff.contains("aaa"));
   }

   public void testDiffNameOnlyWithFileFilter() throws Exception
   {
      List<String> diff = readDiff(new DiffRequest(new String[]{"aaa"}, DiffType.NAME_ONLY, false, 0));
      assertEquals(1, diff.size());
      assertTrue(diff.contains("aaa"));
   }

   public void testDiffNameOnlyWithFileFilterAndCommits() throws Exception
   {
      git.add().addFilepattern("aaa").call();
      git.rm().addFilepattern("README.txt").call();
      git.commit().setMessage("testDiffNameOnlyWithFileFilterAndCommits").setAuthor("andrey", "andrey@mail.com").call();
      List<String> diff = readDiff(new DiffRequest(new String[]{"aaa"}, DiffType.NAME_ONLY, false, 0, "HEAD^1", "HEAD"));
      assertEquals(1, diff.size());
      assertTrue(diff.contains("aaa"));
   }

   public void testDiffRaw() throws Exception
   {
      DiffRequest request = new DiffRequest(null, DiffType.RAW, false, 0);
      DiffPage diffPage = (DiffPage)getDefaultConnection().diff(request);
      // TODO
      diffPage.writeTo(System.out);
   }

   public void testDiffRawWithCommits() throws Exception
   {
      git.add().addFilepattern("aaa").call();
      git.rm().addFilepattern("README.txt").call();
      git.commit().setMessage("testDiffRawWithCommits").call();
      DiffRequest request = new DiffRequest(null, DiffType.RAW, false, 0, "HEAD^1", "HEAD");
      DiffPage diffPage = (DiffPage)getDefaultConnection().diff(request);
      // TODO
      diffPage.writeTo(System.out);
   }

   private List<String> readDiff(DiffRequest request) throws Exception
   {
      DiffPage diffPage = getDefaultConnection().diff(request);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      diffPage.writeTo(out);
      BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(out.toByteArray())));

      String line = null;
      List<String> diff = new ArrayList<String>();
      while ((line = reader.readLine()) != null)
         diff.add(line);

      return diff;
   }
}
