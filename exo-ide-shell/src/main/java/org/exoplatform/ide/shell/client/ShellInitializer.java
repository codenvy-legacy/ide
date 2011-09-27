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

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window.Location;

import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.shell.client.ShellPresenter.Display;
import org.exoplatform.ide.shell.client.marshal.ShellConfigurationUnmarshaller;
import org.exoplatform.ide.shell.client.marshal.StringUnmarshaller;
import org.exoplatform.ide.shell.client.model.ShellConfiguration;
import org.exoplatform.ide.shell.shared.CLIResource;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.VFSInfoUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.HashSet;
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
      try
      {
         ShellService.getService().loadConfiguration(getConfigurationURL(),
            new AsyncRequestCallback<ShellConfiguration>(new ShellConfigurationUnmarshaller(new ShellConfiguration()))
            {

               @Override
               protected void onSuccess(ShellConfiguration result)
               {
                  String entryPoint = getCookie("eXo-IDE-" + result.getUserInfo().getName() + "-entry-point_str");
                  if (entryPoint != null && !entryPoint.isEmpty())
                  {
                     Environment.get().saveValue(EnvironmentVariables.ENTRY_POINT, entryPoint);
                  }
                  else
                  {
                     Environment.get().saveValue(EnvironmentVariables.ENTRY_POINT, result.getEntryPoint());
                  }
                  Environment.get().saveValue(EnvironmentVariables.USER_NAME, result.getUserInfo().getName());
                  updateCurrentDir();
                  Display console = GWT.create(Display.class);
                  CloudShell.consoleWriter = console;
                  new ShellPresenter(console);
                  try
                  {
                     new VirtualFileSystem(Environment.get().getValue(EnvironmentVariables.ENTRY_POINT) + "/")
                        .init(new AsyncRequestCallback<VirtualFileSystemInfo>(new VFSInfoUnmarshaller(
                           new VirtualFileSystemInfo()))
                        {

                           @Override
                           protected void onSuccess(VirtualFileSystemInfo result)
                           {
                              Environment.get().setCurrentFolder((FolderModel)result.getRoot());
                              getCommands();
                           }

                           @Override
                           protected void onFailure(Throwable exception)
                           {
                              //TODO
                           }
                        });
                  }
                  catch (RequestException e)
                  {
                     e.printStackTrace();
                  }

               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  exception.printStackTrace();
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   private void getCommands()
   {
      try
      {
         ShellService.getService().getCommands(
            new AsyncRequestCallback<Set<CLIResource>>(new CLIResourceUnmarshaller(new HashSet<CLIResource>()))
            {
               @Override
               protected void onSuccess(Set<CLIResource> result)
               {
                  CloudShell.getCommands().addAll(result);
                  login();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  // TODO Auto-generated method stub
               }
            });
      }
      catch (RequestException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   private void login()
   {
      String command = "ws login dev-monit";
      try
      {
         ShellService.getService().login(command,
            new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {

               @Override
               protected void onSuccess(StringBuilder result)
               {
                  CloudShell.console().print("Welcome to eXo Cloud Shell\n" + result.toString());
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
      catch (RequestException e)
      {
         e.printStackTrace();
      }
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
         //         final FolderModel f =
         //            new FolderModel(VirtualFileSystem.getInstance().getEnvironmentVariable(EnvironmentVariables.ENTRY_POINT) + path);
         //         VirtualFileSystem.getInstance().getChildren(f, new AsyncRequestCallback<Folder>()
         //         {
         //
         //            @Override
         //            protected void onSuccess(Folder result)
         //            {
         //               VirtualFileSystem.getInstance().setEnvironmentVariable(EnvironmentVariables.WORKDIR, result.getHref());
         //            }
         //
         //            /**
         //             * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
         //             */
         //            @Override
         //            protected void onFailure(Throwable exception)
         //            {
         //               super.onFailure(exception);
         //               CloudShell.console().print(CloudShell.messages.cdErrorFolder(f.getName()) + "\n");
         //            }
         //         });
      }
   }
}
