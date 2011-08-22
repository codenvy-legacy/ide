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
package org.exoplatform.ide.shell.client;

import com.google.gwt.user.client.Window.Location;

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.webdav.WebDavVirtualFileSystem;
import org.exoplatform.ide.shell.client.model.ShellConfiguration;
import org.exoplatform.ide.shell.shared.CLIResource;

import java.util.HashMap;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 12, 2011 evgen $
 *
 */
public class ShellInitializer
{

   private static native String getConfigurationURL()/*-{
		return $wnd.configurationURL;
   }-*/;

   private static native String getCookie(String name)/*-{
		var i, x, y, ARRcookies = document.cookie.split(";");
		for (i = 0; i < ARRcookies.length; i++) {
			x = ARRcookies[i].substr(0, ARRcookies[i].indexOf("="));
			y = ARRcookies[i].substr(ARRcookies[i].indexOf("=") + 1);
			x = x.replace(/^\s+|\s+$/g, "");
			if (x == name) {
				return unescape(y);
			}
		}
		return null;
   }-*/;

   public void init()
   {
      ShellService.getService().loadConfiguration(getConfigurationURL(), new AsyncRequestCallback<ShellConfiguration>()
      {

         @Override
         protected void onSuccess(ShellConfiguration result)
         {
            new WebDavVirtualFileSystem(CloudShell.EVENT_BUS, new EmptyLoader(), new HashMap<String, String>(),
               "/rest/private/");

            String entryPoint = getCookie("eXo-IDE-" + result.getUserInfo().getName() + "-entry-point_str");
            if (entryPoint != null && !entryPoint.isEmpty())
            {
               VirtualFileSystem.getInstance().setEnvironmentVariable(EnvironmentVariables.WORKDIR, entryPoint);
               VirtualFileSystem.getInstance().setEnvironmentVariable(EnvironmentVariables.ENTRY_POINT, entryPoint);
            }
            else
            {
               VirtualFileSystem.getInstance().setEnvironmentVariable(EnvironmentVariables.WORKDIR,
                  result.getEntryPoint());
               VirtualFileSystem.getInstance().setEnvironmentVariable(EnvironmentVariables.ENTRY_POINT,
                  result.getEntryPoint());
            }

            VirtualFileSystem.getInstance().setEnvironmentVariable(EnvironmentVariables.USER_NAME,
               result.getUserInfo().getName());
            updateCurrentDir();
            CloudShell.consoleWriter = new ShellPresenter();

            ShellService.getService().getCommands(new AsyncRequestCallback<Set<CLIResource>>()
            {
               @Override
               protected void onSuccess(Set<CLIResource> result)
               {
                  CloudShell.getCommands().addAll(result);
                  login();
               }
            });

         }
      });
   }

   private void login()
   {
      String command = "ws login dev-monit";
      ShellService.getService().login(command, new AsyncRequestCallback<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            CloudShell.console().print("Welcome to eXo Cloud Shell\n" + result);
         }

         /**
          * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
          */
         @Override
         protected void onFailure(Throwable exception)
         {
            CloudShell.console().print(exception.getMessage());
         }
      });
   }

   private void updateCurrentDir()
   {
      String path = Location.getParameter("workdir");
      if (path != null && !path.isEmpty())
      {
         if (path.startsWith("/"))
         {
            path = path.substring(1);
         }
         if (!path.endsWith("/"))
            path = path + "/";
         final Folder f =
            new Folder(VirtualFileSystem.getInstance().getEnvironmentVariable(EnvironmentVariables.ENTRY_POINT) + path);
         VirtualFileSystem.getInstance().getChildren(f, new AsyncRequestCallback<Folder>()
         {

            @Override
            protected void onSuccess(Folder result)
            {
               VirtualFileSystem.getInstance().setEnvironmentVariable(EnvironmentVariables.WORKDIR, result.getHref());
            }

            /**
             * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
             */
            @Override
            protected void onFailure(Throwable exception)
            {
               super.onFailure(exception);
               CloudShell.console().print(CloudShell.messages.cdErrorFolder(f.getName()) + "\n");
            }
         });
      }
   }
}
