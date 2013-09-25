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
package com.codenvy.ide.ext.java.client.editor.outline;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.texteditor.api.outline.CodeBlock;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaCodeBlock implements CodeBlock {

    private JsonArray<CodeBlock> children = JsonCollections.createArray();

    private CodeBlock parent;

    private String type;

    private int offset;

    private int length;

    private int modifiers;

    private String name;

    private String javaType;

    /**
     *
     */
    public JavaCodeBlock() {

    }

    /**
     * @param children
     * @param parent
     * @param type
     * @param offset
     * @param length
     */
    public JavaCodeBlock(CodeBlock parent, String type, int offset, int length) {
        super();
        this.parent = parent;
        this.type = type;
        this.offset = offset;
        this.length = length;
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return type;
    }

    /** {@inheritDoc} */
    @Override
    public int getOffset() {
        return offset;
    }

    /** {@inheritDoc} */
    @Override
    public int getLength() {
        return length;
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<CodeBlock> getChildren() {
        return children;
    }

    /** {@inheritDoc} */
    @Override
    public CodeBlock getParent() {
        return parent;
    }

    /**
     * @param children
     *         the children to set
     */
    public void setChildren(JsonArray<CodeBlock> children) {
        this.children = children;
    }

    /**
     * @param parent
     *         the parent to set
     */
    public void setParent(CodeBlock parent) {
        this.parent = parent;
    }

    /**
     * @param type
     *         the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param offset
     *         the offset to set
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * @param length
     *         the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /** @return the modifiers */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * @param modifiers
     *         the modifiers to set
     */
    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *         the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the javaType */
    public String getJavaType() {
        return javaType;
    }

    /**
     * @param javaType
     *         the javaType to set
     */
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    /** {@inheritDoc} */
    @Override
    public String getId() {
        return type + name + offset + length;
    }

}
