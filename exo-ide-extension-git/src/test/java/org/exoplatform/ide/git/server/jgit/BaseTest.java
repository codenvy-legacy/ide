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
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.exoplatform.ide.git.server.GitClient;
import org.exoplatform.ide.git.server.jgit.JGitClient;
import org.exoplatform.ide.git.shared.Branch;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BaseTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public abstract class BaseTest extends junit.framework.TestCase
{
   protected static final String CONTENT = "GIT REPOSITORY\n";
   private static final String REPO1 = "repository1";

   protected List<File> forClean = new ArrayList<File>(3);

   private File repoDir;

   private FileRepository repository;

   private GitClient client;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      // Create folder for test repository.
      URL testCls = Thread.currentThread().getContextClassLoader().getResource(".");
      File target = new File(testCls.toURI()).getParentFile();
      repoDir = new File(target, REPO1);
      forClean.add(repoDir);

      // Create repository.
      repository =
         new FileRepositoryBuilder().setGitDir(new File(repoDir + "/.git")).readEnvironment().findGitDir().build();
      repository.create();

      // Create file in repository.
      addFile(repoDir, "README.txt", CONTENT);

      // Add file in git index and commit.
      Git git = new Git(repository);
      git.add().addFilepattern(".").call();
      git.commit().setMessage("init").setAuthor("andrey", "andrey@mail.com").call();

      client = new JGitClient(repository);
   }

   @Override
   protected void tearDown() throws Exception
   {
      GitClient thisClient = getClient();
      if (thisClient != null)
         thisClient.close();
      for (File file : forClean)
         delete(file);
      forClean.clear();
      super.tearDown();
   }

   //

   protected Repository getRepository()
   {
      return repository;
   }

   protected GitClient getClient()
   {
      return client;
   }

   protected void delete(File fileOrDir)
   {
      if (!fileOrDir.exists())
         return;

      if (fileOrDir.isDirectory())
      {
         final File[] fileList = fileOrDir.listFiles();
         if (fileList != null)
         {
            for (File i : fileList)
               delete(i);
         }
      }
      if (!fileOrDir.delete())
      {
         for (int attempt = 0; fileOrDir.exists() && attempt < 5; attempt++)
         {
            try
            {
               Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
               // ignore
            }
            if (fileOrDir.delete())
               break;
         }
         if (fileOrDir.exists())
            throw new RuntimeException("Unable delete " + fileOrDir.getAbsolutePath());
      }
   }

   protected File addFile(File parent, String name, String content) throws IOException
   {
      File file = new File(parent, name);
      FileWriter writer = null;
      try
      {
         writer = new FileWriter(file);
         writer.write(content);
         writer.flush();
      }
      finally
      {
         if (writer != null)
            writer.close();
      }
      return file;
   }

   protected String calculateRelativePath(File dir, File file)
   {
      String fileAbsolutePath = file.getAbsolutePath();
      String dirAbsolutePath = dir.getAbsolutePath();
      if (!fileAbsolutePath.startsWith(dirAbsolutePath))
         throw new IllegalArgumentException("File " + file.getAbsolutePath() + " is not under directory "
            + dir.getAbsolutePath());
      String relativePath = fileAbsolutePath.substring(dirAbsolutePath.length());
      if (relativePath.startsWith("/"))
         relativePath = relativePath.substring(1);
      return relativePath;
   }

   protected String readFile(File file) throws IOException
   {
      if (file.isDirectory())
         throw new IllegalArgumentException("Con't read content from directory " + file.getAbsolutePath());
      FileReader reader = null;
      StringBuilder content = new StringBuilder();
      try
      {
         reader = new FileReader(file);
         int ch = -1;
         while ((ch = reader.read()) != -1)
            content.append((char)ch);
      }
      finally
      {
         if (reader != null)
            reader.close();
      }
      return content.toString();
   }

   protected void checkFilesInCache(File... files) throws IOException
   {
      String[] sFiles = new String[files.length];
      for (int i = 0; i < files.length; i++)
      {
         File file = files[i];
         String path = calculateRelativePath(repoDir, file);
         sFiles[i] = path;
      }
      checkFilesInCache(sFiles);
   }

   protected void checkFilesInCache(String... files) throws IOException
   {
      DirCache dirCache = null;
      try
      {
         dirCache = repository.lockDirCache();
         for (int i = 0; i < files.length; i++)
         {
            String path = files[i];
            DirCacheEntry e = dirCache.getEntry(path);
            assertNotNull("File " + path + " not found in index. ", e);
            assertEquals(path, e.getPathString());
         }
      }
      finally
      {
         if (dirCache != null)
            dirCache.unlock();
      }
   }

   protected void checkNoFilesInCache(File... files) throws IOException
   {
      String[] sFiles = new String[files.length];
      for (int i = 0; i < files.length; i++)
      {
         File file = files[i];
         String path = calculateRelativePath(repoDir, file);
         sFiles[i] = path;
      }
      checkNoFilesInCache(sFiles);
   }

   protected void checkNoFilesInCache(String... files) throws IOException
   {
      DirCache dirCache = null;
      try
      {
         dirCache = repository.lockDirCache();
         for (int i = 0; i < files.length; i++)
         {
            String path = files[i];
            DirCacheEntry e = dirCache.getEntry(path);
            assertNull("File must not be in index. ", e);
         }
      }
      finally
      {
         if (dirCache != null)
            dirCache.unlock();
      }
   }

   protected void validateBranchList(List<Branch> toValidate, List<Branch> pattern)
   {
      l1 : for (Branch tb : toValidate)
      {
         for (Branch pb : pattern)
         {
            if (tb.getName().equals(pb.getName()) //
               && tb.getDisplayName().equals(pb.getDisplayName()) //
               && tb.isActive() == pb.isActive())
               continue l1;
         }
         fail("List of branches is not matches to expected. Branch " + tb + " is not expected in result. ");
      }
   }
}
