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
package com.codenvy.ide.ext.java.client.projectmodel;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;

/**
 * Java Compilation unit is a java file that contains top level class and can be compiled.
 *
 * @author Nikolay Zamosenchuk
 */
public class CompilationUnit extends File {
    public static final String TYPE = "java.compilationUnit";

    /** Internal default constructor */
    protected CompilationUnit() {
        super(TYPE);
    }

    /**
     * Constructor for unmarshalling
     *
     * @param itemReference
     */
    protected CompilationUnit(ItemReference itemReference) {
        this();
        init(itemReference);
    }

    /**
     * Get Package containing this {@link CompilationUnit}.
     *
     * @return Parent Package
     */
    public Package getPackage() {
        Folder parent = getParent();
        // check parent is Package
        checkValidParent(parent);

        // return Parent package
        return (Package)parent;
    }

    /**
     * Set Parent Package
     *
     * @param parentPackage
     *         the parentPackage to set
     */
    public void setPackage(Package parentPackage) {
        setParent(parentPackage);
    }

    /** {@inheritDoc} */
    @Override
    public void setParent(Folder parent) {
        // check parent is Package
        checkValidParent(parent);
        // set Package as CompilationUnit's parent element
        super.setParent(parent);
    }

    /**
     * Check that given parent is an instance of {@link Package}
     *
     * @param parent
     */
    protected void checkValidParent(Folder parent) {
        if (!(parent instanceof Package) && !(parent instanceof SourceFolder)) {
            throw new IllegalArgumentException("Invalid CompilationUnit parent. It must be an instance of Package or SourceFolder class");
        }
    }
}
