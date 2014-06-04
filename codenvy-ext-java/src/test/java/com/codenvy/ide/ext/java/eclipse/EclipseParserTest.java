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
