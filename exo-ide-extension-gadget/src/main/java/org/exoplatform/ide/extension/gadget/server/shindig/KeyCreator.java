/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.gadget.server.shindig;

import org.exoplatform.container.monitor.jvm.J2EEServerInfo;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by The eXo Platform SAS.
 * @author  <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version  $Id: $
 */
public class KeyCreator
{

   public static Log log = ExoLogger.getLogger("org.exoplatform.ide.shindig.KeyCreator");

   public static void createKeyFile()
   {
      File keyFile = new File(getKeyFilePath());
      if (!keyFile.exists())
      {
         File fic = keyFile.getAbsoluteFile();
         log.debug("No key file found at path " + fic + " generating a new key and saving it");
         String key = generateKey();
         Writer out = null;
         try
         {
            out = new FileWriter(keyFile);
            out.write(key);
            out.write('\n');
            out.flush();
            log.info("Generated key file " + fic + " for eXo Gadgets");
         }
         catch (IOException e)
         {
            log.error("Coult not create key file " + fic, e);
         }
         finally
         {
            try
            {
               out.close();
            }
            catch (IOException e)
            {
            }
         }
      }
      else if (!keyFile.isFile())
      {
         log.debug("Found key file " + keyFile.getAbsolutePath() + " but it's not a file");
      }
      else
      {
         log.info("Found key file " + keyFile.getAbsolutePath() + " for gadgets security");
      }
   }

   public static String getKeyFilePath()
   {
      //      /*
      //       * For now uses "gatein.gadgets.securityTokenKeyFile" variable according to IDE-951.
      //       */
      //      String keyFilePath = System.getProperty("gatein.gadgets.securityTokenKeyFile");
      //      log.info("Path to key file > " + keyFilePath);
      //      return keyFilePath;

      J2EEServerInfo info = new J2EEServerInfo();
      String confPath = info.getExoConfigurationDirectory();
      File keyFile = null;

      if (confPath != null)
      {
         File confDir = new File(confPath);
         if (confDir != null && confDir.exists() && confDir.isDirectory())
         {
            keyFile = new File(confDir, "key.txt");
         }
      }

      if (keyFile == null)
      {
         keyFile = new File("key.txt");
      }

      return keyFile.getAbsolutePath();
   }

   /**
    * Generate a key of 32 bytes encoded in base64. The generation is based on
    * {@link SecureRandom} seeded with the current time.
    *
    * @return the key
    */
   private static String generateKey()
   {
      try
      {
         SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
         random.setSeed(System.currentTimeMillis());
         byte bytes[] = new byte[32];
         random.nextBytes(bytes);
         BASE64Encoder encoder = new BASE64Encoder();
         return encoder.encode(bytes);
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new AssertionError(e);
      }
   }
}