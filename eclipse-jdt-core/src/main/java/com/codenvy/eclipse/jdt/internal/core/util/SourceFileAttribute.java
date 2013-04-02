/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.core.util;

import com.codenvy.eclipse.jdt.core.util.ClassFormatException;
import com.codenvy.eclipse.jdt.core.util.IConstantPool;
import com.codenvy.eclipse.jdt.core.util.IConstantPoolConstant;
import com.codenvy.eclipse.jdt.core.util.IConstantPoolEntry;
import com.codenvy.eclipse.jdt.core.util.ISourceAttribute;

/** Default implementation of ISourceAttribute */
public class SourceFileAttribute extends ClassFileAttribute implements ISourceAttribute {

    private int sourceFileIndex;

    private char[] sourceFileName;

    /**
     * Constructor for SourceFileAttribute.
     *
     * @param classFileBytes
     * @param constantPool
     * @param offset
     * @throws ClassFormatException
     */
    public SourceFileAttribute(byte[] classFileBytes, IConstantPool constantPool, int offset) throws ClassFormatException {
        super(classFileBytes, constantPool, offset);
        this.sourceFileIndex = u2At(classFileBytes, 6, offset);
        IConstantPoolEntry constantPoolEntry = constantPool.decodeEntry(this.sourceFileIndex);
        if (constantPoolEntry.getKind() != IConstantPoolConstant.CONSTANT_Utf8) {
            throw new ClassFormatException(ClassFormatException.INVALID_CONSTANT_POOL_ENTRY);
        }
        this.sourceFileName = constantPoolEntry.getUtf8Value();
    }

    /** @see ISourceAttribute#getSourceFileIndex() */
    public int getSourceFileIndex() {
        return this.sourceFileIndex;
    }

    /** @see ISourceAttribute#getSourceFileName() */
    public char[] getSourceFileName() {
        return this.sourceFileName;
    }

}
