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
package org.exoplatform.ide.extension.java.server;

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorageClient;
import org.exoplatform.ide.extension.maven.server.BuilderClient;
import org.exoplatform.ide.vfs.shared.Item;

import java.io.IOException;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: GetDepZipTask.java Oct 25, 2012 vetal $
 *
 */
public class GetDepZipTask extends BuildTask
{
   private String downloadUrl;

   private boolean isDone = false;

   public GetDepZipTask(String dependencyList, Item project, String copyId, BuilderClient client,
      CodeAssistantStorageClient storageClient)
   {
      super(dependencyList, project, copyId, client, storageClient);
   }

   @Override
   protected void buildSuccess(String downloadUrl) throws IOException
   {
      this.downloadUrl = downloadUrl;
      isDone = true;
   }

   public String getDownloadUrl()
   {
      return downloadUrl;
   }

   public boolean isDone()
   {
      return isDone;
   }

}
