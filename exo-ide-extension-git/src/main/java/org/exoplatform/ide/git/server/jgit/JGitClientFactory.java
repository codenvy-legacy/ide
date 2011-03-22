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

import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.exoplatform.ide.git.server.GitClient;
import org.exoplatform.ide.git.server.GitClientFactory;
import org.exoplatform.ide.git.server.GitException;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: JGitClientFactory.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class JGitClientFactory extends GitClientFactory
{
   /**
    * @see org.exoplatform.ide.git.server.GitClientFactory#getClient(java.io.File)
    */
   @Override
   public GitClient getClient(File workDir) throws GitException
   {
      return new JGitClient(createRepository(workDir));
   }
   
   private static Repository createRepository(File workDir) throws GitException
   {
      try
      {
         return new FileRepository(new File(workDir, Constants.DOT_GIT));
      }
      catch (IOException e)
      {
         throw new GitException(e.getMessage(), e);
      }
   }
}
