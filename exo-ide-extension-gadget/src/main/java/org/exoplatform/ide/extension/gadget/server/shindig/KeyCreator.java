/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.extension.gadget.server.shindig;

import sun.misc.BASE64Encoder;

import org.exoplatform.container.monitor.jvm.J2EEServerInfo;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class KeyCreator {

    public static Log log = ExoLogger.getLogger("org.exoplatform.ide.shindig.KeyCreator");

    public static void createKeyFile() {
        File keyFile = new File(getKeyFilePath());
        if (!keyFile.exists()) {
            File fic = keyFile.getAbsoluteFile();
            log.debug("No key file found at path " + fic + " generating a new key and saving it");
            String key = generateKey();
            Writer out = null;
            try {
                out = new FileWriter(keyFile);
                out.write(key);
                out.write('\n');
                out.flush();
                log.info("Generated key file " + fic + " for eXo Gadgets");
            } catch (IOException e) {
                log.error("Coult not create key file " + fic, e);
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        } else if (!keyFile.isFile()) {
            log.debug("Found key file " + keyFile.getAbsolutePath() + " but it's not a file");
        } else {
            log.info("Found key file " + keyFile.getAbsolutePath() + " for gadgets security");
        }
    }

    public static String getKeyFilePath() {
        // /*
        // * For now uses "gatein.gadgets.securityTokenKeyFile" variable according to IDE-951.
        // */
        // String keyFilePath = System.getProperty("gatein.gadgets.securityTokenKeyFile");
        // log.info("Path to key file > " + keyFilePath);
        // return keyFilePath;

        J2EEServerInfo info = new J2EEServerInfo();
        String confPath = info.getExoConfigurationDirectory();
        File keyFile = null;

        if (confPath != null) {
            File confDir = new File(confPath);
            if (confDir != null && confDir.exists() && confDir.isDirectory()) {
                keyFile = new File(confDir, "key.txt");
            }
        }

        if (keyFile == null) {
            keyFile = new File("key.txt");
        }

        return keyFile.getAbsolutePath();
    }

    /**
     * Generate a key of 32 bytes encoded in base64. The generation is based on {@link SecureRandom} seeded with the current time.
     *
     * @return the key
     */
    private static String generateKey() {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(System.currentTimeMillis());
            byte bytes[] = new byte[32];
            random.nextBytes(bytes);
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }
}