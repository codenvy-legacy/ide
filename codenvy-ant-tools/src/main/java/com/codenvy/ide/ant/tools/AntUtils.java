/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
