/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.git.server.jgit.checkout;

import static org.eclipse.jgit.lib.Constants.LOGS;
import static org.eclipse.jgit.lib.Constants.R_HEADS;
import static org.eclipse.jgit.lib.Constants.R_REFS;
import static org.eclipse.jgit.lib.Constants.R_TAGS;

import org.eclipse.jgit.internal.JGitText;
import org.eclipse.jgit.lib.BaseRepositoryBuilder;
import org.eclipse.jgit.lib.ConfigConstants;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class FileRepositoryCE extends FileRepository
{
   final BaseRepositoryBuilder options;

   public FileRepositoryCE(File gitDir) throws IOException
   {
      this(new FileRepositoryBuilder().setGitDir(gitDir).setup());
   }

   public FileRepositoryCE(BaseRepositoryBuilder options) throws IOException
   {
      super(options);
      this.options = options;
   }

   public void create(boolean bare) throws IOException
   {
      final FileBasedConfig cfg = getConfig();
      if (cfg.getFile().exists())
      {
         throw new IllegalStateException(MessageFormat.format(JGitText.get().repositoryAlreadyExists, getDirectory()));
      }
      FileUtils.mkdirs(getDirectory(), true);

      /* --------------IDE-1433----------------- */
      FS fs = getFS();
      File gitDir = getDirectory();
      File refsDir = fs.resolve(gitDir, R_REFS);
      File logsDir = fs.resolve(gitDir, LOGS);
      File logsRefsDir = fs.resolve(gitDir, LOGS + '/' + R_REFS);

      FileUtils.mkdir(refsDir, true);
      FileUtils.mkdir(logsDir, true);
      FileUtils.mkdir(logsRefsDir, true);

      FileUtils.mkdir(new File(refsDir, R_HEADS.substring(R_REFS.length())), true);
      FileUtils.mkdir(new File(refsDir, R_TAGS.substring(R_REFS.length())), true);
      FileUtils.mkdir(new File(logsRefsDir, R_HEADS.substring(R_REFS.length())), true);

      File objects = options.getObjectDirectory();
      File infoDirectory = new File(objects, "info");
      File packDirectory = new File(objects, "pack");
      FileUtils.mkdirs(objects, true);
      FileUtils.mkdir(infoDirectory, true);
      FileUtils.mkdir(packDirectory, true);

      FileUtils.mkdir(new File(getDirectory(), "branches"), true);
      FileUtils.mkdir(new File(getDirectory(), "hooks"), true);
      /* --------------------------------------- */

      RefUpdate head = updateRef(Constants.HEAD);
      head.disableRefLog();
      head.link(Constants.R_HEADS + Constants.MASTER);

      final boolean fileMode;
      if (getFS().supportsExecute())
      {
         File tmp = File.createTempFile("try", "execute", getDirectory());

         getFS().setExecute(tmp, true);
         final boolean on = getFS().canExecute(tmp);

         getFS().setExecute(tmp, false);
         final boolean off = getFS().canExecute(tmp);
         FileUtils.delete(tmp);

         fileMode = on && !off;
      }
      else
      {
         fileMode = false;
      }

      cfg.setInt(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_REPO_FORMAT_VERSION, 0);
      cfg.setBoolean(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_FILEMODE, fileMode);
      if (bare)
      {
         cfg.setBoolean(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_BARE, true);
      }
      cfg.setBoolean(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_LOGALLREFUPDATES, !bare);
      cfg.setBoolean(ConfigConstants.CONFIG_CORE_SECTION, null, ConfigConstants.CONFIG_KEY_AUTOCRLF, false);
      cfg.save();
   }
}
