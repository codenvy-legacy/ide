/*
 * C7opyright (C) 2011 eXo Platform SAS.
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

package org.exoplatform.ide.client.application;

import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.workspace.event.SwitchVFSEvent;
import org.exoplatform.ide.client.workspace.event.SwitchVFSHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.VFSInfoUnmarshaller;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class VirtualFileSystemSwitcher implements SwitchVFSHandler, ConfigurationReceivedSuccessfullyHandler
{
   
   private String vfsBaseUrl;
   
   private String vfsId;
   
   public VirtualFileSystemSwitcher() {
      IDE.addHandler(SwitchVFSEvent.TYPE, this);
      IDE.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
   }

   @Override
   public void onSwitchVFS(SwitchVFSEvent event)
   {
      IDE.fireEvent(new VfsChangedEvent(null));

      vfsId = event.getVfsID();
      String workspaceUrl =
         (vfsBaseUrl.endsWith("/")) ? vfsBaseUrl + vfsId : vfsBaseUrl + "/" + vfsId;

      try {
         new VirtualFileSystem(workspaceUrl).init(new AsyncRequestCallback<VirtualFileSystemInfo>(
            new VFSInfoUnmarshaller(new VirtualFileSystemInfo()))
         {
            @Override
            protected void onSuccess(VirtualFileSystemInfo result)
            {
               IDE.fireEvent(new VfsChangedEvent(result));               
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               Dialogs.getInstance().showError("Workspace " + vfsId + " not found.");
            }
         });               
         
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   @Override
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      vfsBaseUrl = event.getConfiguration().getVfsBaseUrl();
   }

}
