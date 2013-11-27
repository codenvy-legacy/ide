/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.templates.api;

import com.codenvy.ide.runtime.Assert;

/**
 * A template consisting of a name and a pattern.
 * <p>
 * Clients may instantiate this class. May become final in the future.
 * </p>
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @since 3.0
 */
public class Template {

    private String id;

    /** The name of this template */
    private/* final */ String fName;

    /** A description of this template */
    private/* final */ String fDescription;

    /** The name of the context type of this template */
    private/* final */ String fContextTypeId;

    /** The template pattern. */
    private/* final */ String fPattern;

    /**
     * The auto insertable property.
     *
     * @since 3.1
     */
    private final boolean fIsAutoInsertable;

    /** Creates an empty template. */
    public Template() {
        this("", "", "", "", "", true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    /**
     * Creates a copy of a template.
     *
     * @param template
     *         the template to copy
     */
    public Template(Template template) {
        this(template.getId(), template.getName(), template.getDescription(), template.getContextTypeId(), template
                .getPattern(), template.isAutoInsertable());
    }

    /**
     * Creates a template.
     *
     * @param name
     *         the name of the template
     * @param description
     *         the description of the template
     * @param contextTypeId
     *         the id of the context type in which the template can be applied
     * @param pattern
     *         the template pattern
     * @param isAutoInsertable
     *         the auto insertable property of the template
     * @since 3.1
     */
    public Template(String id, String name, String description, String contextTypeId, String pattern,
                    boolean isAutoInsertable) {
        this.id = id;
        Assert.isNotNull(description);
        fDescription = description;
        fName = name;
        Assert.isNotNull(contextTypeId);
        fContextTypeId = contextTypeId;
        fPattern = pattern;
        fIsAutoInsertable = isAutoInsertable;
    }

    /*
     * @see Object#hashCode()
     */
    public int hashCode() {
        return fName.hashCode() ^ fPattern.hashCode() ^ fContextTypeId.hashCode();
    }

    /**
     * Returns the description of the template.
     *
     * @return the description of the template
     */
    public String getDescription() {
        return fDescription;
    }

    /**
     * Returns the id of the context type in which the template can be applied.
     *
     * @return the id of the context type in which the template can be applied
     */
    public String getContextTypeId() {
        return fContextTypeId;
    }

    /**
     * Returns the name of the template.
     *
     * @return the name of the template
     */
    public String getName() {
        return fName;
    }

    /**
     * Returns the template pattern.
     *
     * @return the template pattern
     */
    public String getPattern() {
        return fPattern;
    }

    /**
     * Returns <code>true</code> if template is enabled and matches the context, <code>false</code> otherwise.
     *
     * @param prefix
     *         the prefix (e.g. inside a document) to match
     * @param contextTypeId
     *         the context type id to match
     * @return <code>true</code> if template is enabled and matches the context, <code>false</code> otherwise
     */
    public boolean matches(String prefix, String contextTypeId) {
        return fContextTypeId.equals(contextTypeId);
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if (!(o instanceof Template))
            return false;

        Template t = (Template)o;
        if (t == this)
            return true;

        return t.fName.equals(fName) && t.fPattern.equals(fPattern) && t.fContextTypeId.equals(fContextTypeId)
               && t.fDescription.equals(fDescription) && t.fIsAutoInsertable == fIsAutoInsertable;
    }

    /**
     * Returns the auto insertable property of the template.
     *
     * @return the auto insertable property of the template
     * @since 3.1
     */
    public boolean isAutoInsertable() {
        return fIsAutoInsertable;
    }

    /**
     * @param fPattern
     *         the fPattern to set
     */
    public void setPattern(String fPattern) {
        this.fPattern = fPattern;
    }

    /**
     * Template id
     *
     * @return the id
     */
    public String getId() {
        return id;
    }
}
