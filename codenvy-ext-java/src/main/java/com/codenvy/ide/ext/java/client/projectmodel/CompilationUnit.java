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
package com.codenvy.ide.ext.java.client.projectmodel;

import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.google.gwt.json.client.JSONObject;

/**
 * Java Compilation unit is a .java file that contains top level class and can be compiled
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
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
     * @param itemObject
     */
    protected CompilationUnit(JSONObject itemObject) {
        this();
        init(itemObject);
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
