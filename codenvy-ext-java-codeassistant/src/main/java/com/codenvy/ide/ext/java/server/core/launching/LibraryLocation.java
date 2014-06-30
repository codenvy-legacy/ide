/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.server.core.launching;

import org.eclipse.core.runtime.IPath;

import java.io.File;
import java.net.URL;


/**
 * The location of a library (for example rt.jar).
 */
public final class LibraryLocation {
    private IPath fSystemLibrary;
    private IPath fSystemLibrarySource;
    private IPath fPackageRootPath;
    private URL   fJavadocLocation;

    /**
     * Creates a new library location.
     *
     * @param libraryPath    The location of the JAR containing java.lang.Object
     * 					Must not be <code>null</code>.
     * @param sourcePath    The location of the zip file containing the sources for <code>library</code>
     * 					Must not be <code>null</code> (Use Path.EMPTY instead)
     * @param packageRoot The path inside the <code>source</code> zip file where packages names
     * 					  begin. If the source for java.lang.Object source is found at
     * 					  "src/java/lang/Object.java" in the zip file, the
     * 					  packageRoot should be "src"
     * 					  Must not be <code>null</code>. (Use Path.EMPTY or IPath.ROOT)
     * @throws IllegalArgumentException    If the library path is <code>null</code>.
     */
    public LibraryLocation(IPath libraryPath, IPath sourcePath, IPath packageRoot) {
        this(libraryPath, sourcePath, packageRoot, null);
    }

    /**
     * Creates a new library location.
	 * 
	 * @param libraryPath	The location of the JAR containing java.lang.Object
	 * 					Must not be <code>null</code>.
	 * @param sourcePath	The location of the zip file containing the sources for <code>library</code>
	 * 					Must not be <code>null</code> (Use Path.EMPTY instead)
	 * @param packageRoot The path inside the <code>source</code> zip file where packages names
	 * 					  begin. If the source for java.lang.Object source is found at 
	 * 					  "src/java/lang/Object.java" in the zip file, the 
	 * 					  packageRoot should be "src"
	 * 					  Must not be <code>null</code>. (Use Path.EMPTY or IPath.ROOT)
	 * @param javadocLocation The location of the javadoc for <code>library</code>
	 * @throws	IllegalArgumentException	If the library path is <code>null</code>.
	 */
	public LibraryLocation(IPath libraryPath, IPath sourcePath, IPath packageRoot, URL javadocLocation) {
		if (libraryPath == null)
			throw new IllegalArgumentException("library cannot be null");

		fSystemLibrary= libraryPath;
		fSystemLibrarySource= sourcePath;
		fPackageRootPath= packageRoot;
		fJavadocLocation= javadocLocation;
	}		
		
	/**
	 * Returns the JRE library jar location.
	 * 
	 * @return The JRE library jar location.
	 */
	public IPath getSystemLibraryPath() {
		return fSystemLibrary;
	}
	
	/**
	 * Returns the JRE library source zip location.
	 * 
	 * @return The JRE library source zip location.
	 */
	public IPath getSystemLibrarySourcePath() {
		return fSystemLibrarySource;
	}	
	
	/**
	 * Returns the path to the default package in the sources zip file
	 * 
	 * @return The path to the default package in the sources zip file.
	 */
	public IPath getPackageRootPath() {
		return fPackageRootPath;
	}
	
	/**
	 * Returns the Javadoc location associated with this Library location.
	 * 
	 * @return a url pointing to the Javadoc location associated with
	 * 	this Library location, or <code>null</code> if none
	 * @since 3.1
	 */
	public URL getJavadocLocation() {
		return fJavadocLocation;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LibraryLocation) {
			LibraryLocation lib = (LibraryLocation)obj;
			return getSystemLibraryPath().equals(lib.getSystemLibraryPath()) 
				&& equals(getSystemLibrarySourcePath(), lib.getSystemLibrarySourcePath())
				&& equals(getPackageRootPath(), lib.getPackageRootPath())
				&& sameURL(getJavadocLocation(), lib.getJavadocLocation());
		} 
		return false;
	}

	@Override
	public int hashCode() {
		return getSystemLibraryPath().hashCode();
	}
	
	/**
	 * Returns whether the given paths are equal - either may be <code>null</code>.
	 * @param path1 path to be compared
	 * @param path2 path to be compared
	 * @return whether the given paths are equal
	 */
	protected boolean equals(IPath path1, IPath path2) {
		return equalsOrNull(path1, path2);
	}
	
	/**
	 * Returns whether the given objects are equal - either may be <code>null</code>.
	 * @param o1 object to be compared
	 * @param o2 object to be compared
	 * @return whether the given objects are equal or both null
	 * @since 3.1
	 */	
	private boolean equalsOrNull(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		if (o2 == null) {
			return false;
		}
		return o1.equals(o2);
	}

	/**
	 * Sets the JRE library source zip location.
	 * 
	 * @param source the source to set
	 * @since 3.4
	 */
	public void setSystemLibrarySource(IPath source) {
		fSystemLibrarySource = source;
	}

    /**
     * Compares two URL for equality, but do not connect to do DNS resolution
     *
     * @param url1 a given URL
     * @param url2 another given URL to compare to url1
     *
     * @return <code>true</code> if the URLs are equal, <code>false</code> otherwise
     */
    public static boolean sameURL(URL url1, URL url2) {
        if (url1 == url2) {
            return true;
        }
        if (url1 == null ^ url2 == null) {
            return false;
        }
        // check if URL are file: URL as we may have two URL pointing to the same doc location
        // but with different representation - (i.e. file:/C;/ and file:C:/)
        final boolean isFile1 = "file".equalsIgnoreCase(url1.getProtocol());//$NON-NLS-1$
        final boolean isFile2 = "file".equalsIgnoreCase(url2.getProtocol());//$NON-NLS-1$
        if (isFile1 && isFile2) {
            File file1 = new File(url1.getFile());
            File file2 = new File(url2.getFile());
            return file1.equals(file2);
        }
        // URL1 XOR URL2 is a file, return false. (They either both need to be files, or neither)
        if (isFile1 ^ isFile2) {
            return false;
        }
        return getExternalForm(url1).equals(getExternalForm(url2));
    }

    /**
     * Gets the external form of this URL. In particular, it trims any white space,
     * removes a trailing slash and creates a lower case string.
     * @param url the URL to get the {@link String} value of
     * @return the lower-case {@link String} form of the given URL
     */
    private static String getExternalForm(URL url) {
        String externalForm = url.toExternalForm();
        if (externalForm == null)
            return ""; //$NON-NLS-1$
        externalForm = externalForm.trim();
        if (externalForm.endsWith("/")) { //$NON-NLS-1$
            // Remove the trailing slash
            externalForm = externalForm.substring(0, externalForm.length() - 1);
        }
        return externalForm.toLowerCase();

    }
}
