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
package com.codenvy.ide.ext.java.worker.env;

import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryAnnotation;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryMethod;
import com.codenvy.ide.ext.java.worker.env.json.AnnotationJso;
import com.codenvy.ide.ext.java.worker.env.json.MethodJso;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evgen Vidolob
 */
public class BinaryMethod implements IBinaryMethod {
    private List<IBinaryAnnotation[]> parameterAnnotations;
    private       MethodJso                 jso;

    public BinaryMethod(MethodJso jso) {
        this.jso = jso;
        if (jso.getParameterAnnotations() != null) {
            parameterAnnotations = new ArrayList<IBinaryAnnotation[]>();
            for (int i = 0; i < jso.getParameterAnnotations().size(); i++) {
                parameterAnnotations.add(getBinaryAnnotations(jso.getParameterAnnotations().get(i)));
            }
        }
    }

    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return getBinaryAnnotations(jso.getAnnotations());
    }

    private IBinaryAnnotation[] getBinaryAnnotations(JsoArray<AnnotationJso> annotations) {
        if (annotations == null) return null;
        IBinaryAnnotation[] binaryAnnotations = new IBinaryAnnotation[annotations.size()];
        for (int i = 0; i < annotations.size(); i++) {
            binaryAnnotations[i] = new BinaryAnnotation(annotations.get(i));
        }
        return binaryAnnotations;
    }

    @Override
    public Object getDefaultValue() {
        if (jso.getDefaultValue() == null) return null;
        return Util.getDefaultValue(jso.getDefaultValue());
    }

    @Override
    public char[][] getExceptionTypeNames() {
        if (jso.getExceptionTypeNames() == null) return null;
        return Util.arrayStringToCharArray(jso.getExceptionTypeNames());
    }

    @Override
    public char[] getGenericSignature() {
        if (jso.getGenericSignature() == null) return null;
        return jso.getGenericSignature().toCharArray();
    }

    @Override
    public char[] getMethodDescriptor() {
        if (jso.getMethodDescriptor() == null) return null;
        return jso.getMethodDescriptor().toCharArray();
    }

    @Override
    public IBinaryAnnotation[] getParameterAnnotations(int index) {
        if(parameterAnnotations == null) return null;
        return parameterAnnotations.get(index);
    }

    @Override
    public char[] getSelector() {
        if (jso.getSelector() == null) return null;
        return jso.getSelector().toCharArray();
    }

    @Override
    public long getTagBits() {
        return Long.parseLong(jso.getTagBits());
    }

    @Override
    public boolean isClinit() {
        return jso.isClinit();
    }

    @Override
    public int getModifiers() {
        return jso.getModifiers();
    }

    @Override
    public boolean isConstructor() {
        return jso.isConstructor();
    }

    @Override
    public char[][] getArgumentNames() {
        if (jso.getArgumentNames() == null) return null;
        return Util.arrayStringToCharArray(jso.getArgumentNames());
    }
}
