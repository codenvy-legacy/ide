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
package com.codenvy.ide.ext.java.server.internal.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import java.io.File;

/**
 * A package fragment root that corresponds to a .jar or .zip.
 *
 * <p>NOTE: The only visible entries from a .jar or .zip package fragment root
 * are .class files.
 * <p>NOTE: A jar package fragment root may or may not have an associated resource.
 * @author Evgen Vidolob
 */
public class JarPackageFragmentRoot extends PackageFragmentRoot {

    /**
     * The path to the jar file
     * (a workspace relative path if the jar is internal,
     * or an OS path if the jar is external)
     */
    protected final IPath jarPath;

    protected JarPackageFragmentRoot(File file, JavaProject project) {
        super(file, project);
        jarPath = new Path(file.getPath());
    }

    @Override
    public boolean isArchive() {
        return true;
    }

    @Override
    public IPath getPath() {
        return jarPath;
    }
}
