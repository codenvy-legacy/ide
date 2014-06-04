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
package com.codenvy.ide.ext.java.client.editor.outline;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.texteditor.api.outline.CodeBlock;


/**
 * Java code block implementation
 *
 * @author Evgen Vidolob
 */
public class JavaCodeBlock extends Jso implements CodeBlock {

    protected JavaCodeBlock() {
    }

    /** {@inheritDoc} */
    public native final String getType()/*-{
        return this[0];
    }-*/;

    /**
     * @param type
     *         the type to set
     */
    public native final JavaCodeBlock setType(String type) /*-{
        this[0] = type;
        return this;
    }-*/;

    /** {@inheritDoc} */
    public native final int getOffset() /*-{
        return this[1];
    }-*/;

    /**
     * @param offset
     *         the offset to set
     */
    public native final JavaCodeBlock setOffset(int offset) /*-{
        this[1] = offset;
        return this;
    }-*/;

    /** {@inheritDoc} */
    public native final int getLength() /*-{
        return this[2];
    }-*/;

    /**
     * @param length
     *         the length to set
     */
    public native final JavaCodeBlock setLength(int length) /*-{
        this[2] = length;
        return this;
    }-*/;

    /** {@inheritDoc} */
    public native final Array<CodeBlock> getChildren() /*-{
        return this[3];
    }-*/;

    /**
     * @param children
     *         the children to set
     */
    public native final JavaCodeBlock setChildren(Array<JavaCodeBlock> children) /*-{
        this[3] = children;
        return this;
    }-*/;

    /** {@inheritDoc} */
    public native final JavaCodeBlock getParent() /*-{
        return this[4];
    }-*/;

    /**
     * @param parent
     *         the parent to set
     */
    public native final JavaCodeBlock setParent(JavaCodeBlock parent) /*-{
        this[4] = parent;
        return this;
    }-*/;

    /** @return the name */
    public native final String getName() /*-{
        return this[5];
    }-*/;

    /**
     * @param name
     *         the name to set
     */
    public native final JavaCodeBlock setName(String name) /*-{
        this[5] = name;
        return this;
    }-*/;

    /** @return the modifiers */
    public native final int getModifiers() /*-{
        return this[6];
    }-*/;


    /**
     * @param modifiers
     *         the modifiers to set
     */
    public native final JavaCodeBlock setModifiers(int modifiers) /*-{
        this[6] = modifiers;
    }-*/;

    /** @return the javaType */
    public native final String getJavaType() /*-{
        return this[7];
    }-*/;

    /**
     * @param javaType
     *         the javaType to set
     */
    public native final JavaCodeBlock setJavaType(String javaType) /*-{
        this[7] = javaType;
    }-*/;

    /** {@inheritDoc} */
    public final String getId() {
        return getType() + getName() + getOffset() + getLength();
    }


    public static native JavaCodeBlock make() /*-{
        return [];
    }-*/;
}
