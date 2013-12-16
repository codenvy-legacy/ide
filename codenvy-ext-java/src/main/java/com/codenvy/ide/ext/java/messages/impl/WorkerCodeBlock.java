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
package com.codenvy.ide.ext.java.messages.impl;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Jso;


/**
 * Java code block implementation
 *
 * @author Evgen Vidolob
 */
public class WorkerCodeBlock extends Jso {

    protected WorkerCodeBlock() {
    }

    /** {@inheritDoc} */
    public native final String getType()/*-{
        return this[0];
    }-*/;

    /**
     * @param type
     *         the type to set
     */
    public native final WorkerCodeBlock setType(String type) /*-{
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
    public native final WorkerCodeBlock setOffset(int offset) /*-{
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
    public native final WorkerCodeBlock setLength(int length) /*-{
        this[2] = length;
        return this;
    }-*/;

    /** {@inheritDoc} */
    public native final Array<WorkerCodeBlock> getChildren() /*-{
        return this[3];
    }-*/;

    /**
     * @param children
     *         the children to set
     */
    public native final WorkerCodeBlock setChildren(Array<WorkerCodeBlock> children) /*-{
        this[3] = children;
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
    public native final WorkerCodeBlock setName(String name) /*-{
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
    public native final WorkerCodeBlock setModifiers(int modifiers) /*-{
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
    public native final WorkerCodeBlock setJavaType(String javaType) /*-{
        this[7] = javaType;
    }-*/;

    /** {@inheritDoc} */
    public final String getId() {
        return getType() + getName() + getOffset() + getLength();
    }


    public static native WorkerCodeBlock make() /*-{
        return [];
    }-*/;
}
