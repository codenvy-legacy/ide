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
package org.exoplatform.ide.davmount;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.File;
import java.io.IOException;

public class DavFSMounter
{
   private static final Log LOG = ExoLogger.getLogger(DavFSMounter.class.getName());

   private static final String MOUNT_COMMAND = "mount.davfs.command";

   public static boolean isDavFsInstalled()
   {
      String[] cmd = {"/bin/sh", "-c", "sudo /usr/sbin/mount.davfs "};
      try
      {
         Process p1 = Runtime.getRuntime().exec(cmd);
         p1.waitFor();
         final String stdout = new String(ByteStreams.toByteArray(p1.getInputStream()));
         final String stderror = new String(ByteStreams.toByteArray(p1.getErrorStream()));
         // if(!)
         final boolean davFsInstalled = stdout.contains("Usage");
         if (!davFsInstalled)
         {
            LOG.error(stderror);
         }
         return davFsInstalled;
      }
      catch (IOException e)
      {
      }
      catch (InterruptedException e)
      {
      }
      return false;
   }

   public static boolean isWindows()
   {

      String os = System.getProperty("os.name").toLowerCase();
      // windows
      return os.indexOf("win") >= 0;

   }

   public final boolean enabled;

   /**
    * 
    */

   private final String mountCommand;

   public DavFSMounter()
   {
      super();
      this.enabled = checkEnv();
      final String mountProperty = System.getProperty(MOUNT_COMMAND);
      if (mountProperty != null && mountProperty.length() > 0)
      {
         mountCommand = mountProperty;
      }
      else
      {
         String userName = System.getProperty("user.name");
         mountCommand = "sudo /usr/sbin/mount.davfs -o uid=" + userName + ",user,rw ";
      }

      LOG.debug("Environment check '{}'", this.enabled);
      LOG.debug("Mount command '{}'", this.mountCommand);
      LOG.debug("Davfs check command 'sudo mount.davfs '");
   }

   public boolean mount(String webdavserver, String dir, String userName, String password, boolean waitForResponse)
      throws IOException, InterruptedException
   {
      if (enabled)
      {
         File passFile = File.createTempFile("tmp", "tmp");
         passFile.deleteOnExit();
         try
         {
            Files.write((userName + "\n" + password).getBytes(), passFile);
            LOG.info("shouldBindDirectory");

            StringBuilder executeCommand = new StringBuilder();
            executeCommand.append(mountCommand);
            executeCommand.append(webdavserver).append(" ");
            executeCommand.append(dir).append(" < ").append(passFile);

            String[] cmd = {"/bin/sh", "-c", executeCommand.toString()};

            StringBuffer result = new StringBuffer();
            for (String element : cmd)
            {
               result.append(element);
               // result.append( optional separator );
            }
            LOG.info("Execute" + result.toString());
            Process p1 = Runtime.getRuntime().exec(cmd);

            if (waitForResponse)
            {
               LOG.info(new String(ByteStreams.toByteArray(p1.getInputStream())));
               LOG.info(new String(ByteStreams.toByteArray(p1.getErrorStream())));
               p1.waitFor();
               return p1.exitValue() == 0;
            }
            else
            {
               return true;
            }

         }
         finally
         {
            if (!passFile.delete())
            {
               LOG.warn("Unable to delete file {}", passFile.getAbsoluteFile());
            };
         }
      }
      else
      {
         LOG.warn("DavFSMounter  can't mount davfs dir in current environment");
      }
      return false;

   }

   public boolean umount(String dir, boolean waitForResponse) throws IOException, InterruptedException
   {
      if (enabled)
      {
         String[] cmd = {"/bin/sh", "-c", "sudo /usr/sbin/umount.davfs " + dir};
         Process p1 = Runtime.getRuntime().exec(cmd);

         if (waitForResponse)
         {
            LOG.info(new String(ByteStreams.toByteArray(p1.getInputStream())));
            LOG.info(new String(ByteStreams.toByteArray(p1.getErrorStream())));
            p1.waitFor();
            return p1.exitValue() == 0;
         }
         else
         {
            return true;
         }
      }
      else
      {
         LOG.warn("DavFSMounter  can't umount davfs dir in current environment");
      }
      return false;

   }

   private boolean checkEnv()
   {
      if (isWindows())
      {
         LOG.warn("DavFSMounter unable to work under 'Windows' OS");
         return false;
      }
      if (!isDavFsInstalled())
      {
         LOG.warn("davfs not installed");
         return false;
      }
      return true;
   }

}
