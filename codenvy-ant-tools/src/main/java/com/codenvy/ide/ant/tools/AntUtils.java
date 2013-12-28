/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ant.tools;

/** @author andrew00x */
public class AntUtils {
    /** Not instantiable. */
    private AntUtils() {
    }

    public static String getAntExecCommand() {
        final java.io.File antHome = getAntHome();
        if (antHome != null) {
            final String ant = "bin" + java.io.File.separatorChar + "ant";
            return new java.io.File(antHome, ant).getAbsolutePath(); // If ant home directory set use it
        } else {
            return "ant"; // otherwise 'ant' should be in PATH variable
        }
    }

    public static java.io.File getAntHome() {
        final String antHomeEnv = System.getenv("ANT_HOME");
        if (antHomeEnv == null) {
            return null;
        }
        java.io.File antHome = new java.io.File(antHomeEnv);
        return antHome.exists() ? antHome : null;
    }
}
