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

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.lib.UserConfig;
import org.eclipse.jgit.storage.file.FileRepository;
import org.exoplatform.ide.git.server.jgit.JGitConnection;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.InitRequest;

import java.io.File;
import java.net.URL;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: InitTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class InitTest extends BaseTest
{
   private File workDir;

   @Override
   protected void setUp() throws Exception
   {
      URL testCls = Thread.currentThread().getContextClassLoader().getResource(".");
      File target = new File(testCls.toURI()).getParentFile();
      workDir = new File(target, "InitRepoTest");
      forClean.add(workDir);
   }

   public void testInitRepo() throws Exception
   {
      JGitConnection client = new JGitConnection(new FileRepository(new File(workDir, ".git")));
      client.init(new InitRequest(null/* .git directory already set. Not need to pass it in this implementation. */, // 
         false, //
         new GitUser("andrey", "andrey@mail.com")));
      Repository repository = client.getRepository();
      assertNotNull(repository);
      StoredConfig config = repository.getConfig();
      UserConfig userConfig = config.get(UserConfig.KEY);
      assertEquals("andrey", userConfig.getAuthorName());
      assertEquals("andrey", userConfig.getCommitterName());
      assertEquals("andrey@mail.com", userConfig.getAuthorEmail());
      assertEquals("andrey@mail.com", userConfig.getCommitterEmail());
      System.out.println(config.toText());
   }
}
