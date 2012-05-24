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
package org.exoplatform.ide.extension.googleappengine.server.ext;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.extension.googleappengine.server.AppEngineClient;
import org.exoplatform.ide.extension.googleappengine.server.Utils;
import org.picocontainer.Startable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class AppEngineEnv implements Startable
{
   private final String sdkDirPath;
   private final String sdkZipPath;

   public AppEngineEnv(InitParams params)
   {
      this(readValueParam(params, "appengine-sdk-dir"), readValueParam(params, "appengine-sdk-zip"));
   }

   private static String readValueParam(InitParams initParams, String paramName)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(paramName);
         if (vp != null)
         {
            return vp.getValue();
         }
      }
      return null;
   }

   protected AppEngineEnv(String sdkDirPath, String sdkZipPath)
   {
      this.sdkDirPath = sdkDirPath;
      this.sdkZipPath = sdkZipPath;
   }

   @Override
   public void start()
   {
      try
      {
         File sdkDir = new File(sdkDirPath);
         if (!sdkDir.exists())
         {
            File sdkZip = new File(sdkZipPath);
            if (!sdkZip.exists())
            {
               throw new RuntimeException("Google App Engine SDK not found  at " + sdkZipPath);
            }
            if (!sdkDir.mkdirs())
            {
               throw new RuntimeException("Unable create folder " + sdkDirPath);
            }
            Utils.unzip(sdkZip, sdkDir);
         }
         Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
         addURL.setAccessible(true);
         addURL.invoke(getClassLoader(), new File(sdkDir, "lib/appengine-tools-api.jar").toURI().toURL());
         System.setProperty("appengine.sdk.root", sdkDirPath);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      catch (InvocationTargetException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      catch (NoSuchMethodException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e.getMessage(), e);
      }
   }

   protected abstract URLClassLoader getClassLoader();

   @Override
   public void stop()
   {
   }
}
