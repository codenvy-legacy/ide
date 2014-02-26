/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.ext.java.eclipse;


import junit.framework.Assert;

import com.codenvy.ide.ext.java.server.core.launching.LibraryLocation;
import com.codenvy.ide.ext.java.server.core.launching.StandardVMType;
import com.codenvy.vfs.impl.fs.LocalFileSystemTest;

import org.junit.Test;

/**
 * @author Evgen Vidolob
 */
public class EclipseParserTest  extends LocalFileSystemTest{

    @Test
    public void testFindRT_JAR() throws Exception {
        StandardVMType vmType = new StandardVMType();
        LibraryLocation[] locations = vmType.getDefaultLibraryLocations(vmType.detectInstallLocation());
        boolean isRtJarFound = false;
        for (LibraryLocation l : locations) {
            if (l.getSystemLibraryPath().toOSString().contains("rt.jar")) {
                isRtJarFound = true;
                break;
            }
        }
        Assert.assertTrue(isRtJarFound);
    }
}
