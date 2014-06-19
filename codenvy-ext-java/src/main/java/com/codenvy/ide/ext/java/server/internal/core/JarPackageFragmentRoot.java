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
