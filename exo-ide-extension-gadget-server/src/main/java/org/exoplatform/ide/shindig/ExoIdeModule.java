/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.shindig;

import org.apache.shindig.common.PropertiesModule;
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
 * USE ONLY IN STANDALONE BUILD
 * 
 * 1. This class need for rewrite default location of 
 * shindig properties file. By default it locate in shindig-common.jar 
 * In this properties file described location of container.js file.
 * We can't use container.js from GateIn in standalone build because it use eXoGadgetServer context.
 * We use IDE-application and describe in in our container.js 
 * (see /exo-ide-module-gadget-server/src/main/resources/containers/default-ide/container.js).  
 * 
 * 2. It generate key file for gadget security (OAuth)
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class ExoIdeModule extends PropertiesModule
{
   private static String DEFAULT_PROPERTIES = "ide.shindig.properties";
   
   private Log log = ExoLogger.getLogger(ExoIdeModule.class);

   public ExoIdeModule()
   {
      super(DEFAULT_PROPERTIES);
      File keyFile = new File("key.txt");
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
               e.printStackTrace();
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
