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
package org.exoplatform.ide.extension.java.jdi.server;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.ide.extension.cloudfoundry.server.Cloudfoundry;
import org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryAuthenticator;
import org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryCredentials;
import org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryException;
import org.exoplatform.ide.extension.cloudfoundry.server.DebugMode;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.Instance;
import org.exoplatform.ide.extension.java.jdi.server.model.ApplicationInstanceImpl;
import org.exoplatform.ide.extension.java.jdi.server.model.DebugApplicationInstanceImpl;
import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;
import org.exoplatform.ide.extension.java.jdi.shared.DebugApplicationInstance;
import org.exoplatform.ide.helper.ParsingResponseException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryApplicationRunner implements ApplicationRunner
{
   private static final Random RANDOM = new Random();
   private static final char[] CHARS = new char[36];

   static
   {
      int i = 0;
      for (int c = 48; c <= 57; c++)
      {
         CHARS[i++] = (char)c;
      }
      for (int c = 97; c <= 122; c++)
      {
         CHARS[i++] = (char)c;
      }
   }

   private static String generateAppName(int length)
   {
      StringBuilder b = new StringBuilder(length + 4);
      b.append("app-");
      for (int i = 0; i < length; i++)
      {
         b.append(CHARS[RANDOM.nextInt(CHARS.length)]);
      }
      return b.toString();
   }

   private final Cloudfoundry cloudfoundry;

   public CloudfoundryApplicationRunner(InitParams initParams)
   {
      this(readValueParam(initParams, "cloudfoundry-target"), readValueParam(initParams, "cloudfoundry-token"));
   }

   protected CloudfoundryApplicationRunner(String cfTarget, String cfToken)
   {
      if (cfTarget == null || cfTarget.isEmpty())
      {
         throw new IllegalArgumentException("Cloud Foundry target URL may not be null or empty.");
      }
      if (cfToken == null || cfToken.isEmpty())
      {
         throw new IllegalArgumentException("Cloud Foundry secret token may not be null or empty.");
      }
      cloudfoundry = new Cloudfoundry(new Auth(cfTarget, cfToken));
   }

   private static String readValueParam(InitParams initParams, String name)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(name);
         return vp != null ? vp.getValue() : null;
      }
      return null;
   }

   @Override
   public synchronized ApplicationInstance runApplication(URL war) throws DeployApplicationException
   {
      // Method should be 'synchronized'. At the moment we use just one user as deployer.
      try
      {
         String target = cloudfoundry.getTarget();
         CloudFoundryApplication cfApp = cloudfoundry.createApplication(
            target,
            generateAppName(16),
            "spring",
            null,
            1,
            -1,
            false,
            null,
            null,
            null,
            war);
         return new ApplicationInstanceImpl(cfApp.getName(), cfApp.getUris().get(0), null);
      }
      catch (CloudfoundryException e)
      {
         throw new DeployApplicationException(e.getMessage(), e);
      }
      catch (ParsingResponseException e)
      {
         throw new DeployApplicationException(e.getMessage(), e);
      }
      catch (VirtualFileSystemException e)
      {
         throw new DeployApplicationException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new DeployApplicationException(e.getMessage(), e);
      }
   }

   @Override
   public synchronized DebugApplicationInstance debugApplication(URL war, boolean suspend) throws DeployApplicationException
   {
      // Method should be 'synchronized'. At the moment we use just one user as deployer.
      try
      {
         String target = cloudfoundry.getTarget();
         CloudFoundryApplication cfApp = cloudfoundry.createApplication(
            target,
            generateAppName(16),
            "spring",
            null,
            1,
            -1,
            false,
            suspend ? new DebugMode("suspend") : new DebugMode(),
            null,
            null,
            war);
         String name = cfApp.getName();
         Instance[] instances = cloudfoundry.applicationInstances(target, name, null, null);
         if (instances.length != 1)
         {
            throw new DeployApplicationException("Unable run application in debug mode. ");
         }
         return new DebugApplicationInstanceImpl(
            cfApp.getName(),
            cfApp.getUris().get(0),
            null,
            instances[0].getDebugHost(),
            instances[0].getDebugPort()
         );
      }
      catch (CloudfoundryException e)
      {
         throw new DeployApplicationException(e.getMessage(), e);
      }
      catch (ParsingResponseException e)
      {
         throw new DeployApplicationException(e.getMessage(), e);
      }
      catch (VirtualFileSystemException e)
      {
         throw new DeployApplicationException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new DeployApplicationException(e.getMessage(), e);
      }
   }

   @Override
   public synchronized void stopApplication(String name) throws DeployApplicationException
   {
      // Method should be 'synchronized'. At the moment we use just one user as deployer.
      try
      {
         String target = cloudfoundry.getTarget();
         cloudfoundry.stopApplication(target, name, null, null);
         cloudfoundry.deleteApplication(target, name, null, null, true);
      }
      catch (CloudfoundryException e)
      {
         throw new DeployApplicationException(e.getMessage(), e);
      }
      catch (ParsingResponseException e)
      {
         throw new DeployApplicationException(e.getMessage(), e);
      }
      catch (VirtualFileSystemException e)
      {
         throw new DeployApplicationException(e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new DeployApplicationException(e.getMessage(), e);
      }
   }

   private static class Auth extends CloudfoundryAuthenticator
   {
      private final CloudfoundryCredentials credentials;
      private final String cfTarget;

      public Auth(String cfTarget, String cfToken)
      {
         // We do not use stored cloud foundry credentials.
         // Not need VFS, configuration, etc.
         super(null, null);
         this.cfTarget = cfTarget;
         credentials = new CloudfoundryCredentials();
         credentials.addToken(cfTarget, cfToken);
      }

      @Override
      public String readTarget() throws VirtualFileSystemException, IOException
      {
         return cfTarget;
      }

      @Override
      public CloudfoundryCredentials readCredentials() throws VirtualFileSystemException, IOException
      {
         return credentials;
      }

      @Override
      public void writeTarget(String target) throws VirtualFileSystemException, IOException
      {
         throw new UnsupportedOperationException();
      }

      @Override
      public void writeCredentials(CloudfoundryCredentials credentials) throws VirtualFileSystemException, IOException
      {
         throw new UnsupportedOperationException();
      }
   }
}
