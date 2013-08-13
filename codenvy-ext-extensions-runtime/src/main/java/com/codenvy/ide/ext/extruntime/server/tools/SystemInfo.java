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
package com.codenvy.ide.ext.extruntime.server.tools;

/**
 * Provides information about operations system.
 *
 * @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a>
 */
public class SystemInfo {
    public static final  String  OS      = System.getProperty("os.name").toLowerCase();
    private static final boolean linux   = OS.startsWith("linux");
    private static final boolean windows = OS.startsWith("windows");
    private static final boolean unix    = !windows;

    public static boolean isLinux() {
        return linux;
    }

    public static boolean isWindows() {
        return windows;
    }

    public static boolean isUnix() {
        return unix;
    }

    private SystemInfo() {
    }
}
