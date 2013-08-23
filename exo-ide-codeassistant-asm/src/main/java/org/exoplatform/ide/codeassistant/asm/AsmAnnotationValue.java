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
package org.exoplatform.ide.codeassistant.asm;

import org.exoplatform.ide.codeassistant.jvm.shared.Annotation;
import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import java.lang.reflect.Array;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class AsmAnnotationValue implements AnnotationValue {

    private Object defaultValue;

    /** @param defaultValue */
    public AsmAnnotationValue(Object defaultValue) {
        super();
        this.defaultValue = defaultValue;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getPrimitiveType() */
    @Override
    public String[] getPrimitiveType() {
        Class<? extends Object> clazz = defaultValue.getClass();
        if (clazz == Byte.class || //
            clazz == Boolean.class || //
            clazz == Character.class || //
            clazz == Short.class || //
            clazz == Integer.class || //
            clazz == Long.class || //
            clazz == Float.class || //
            clazz == Double.class || //
            clazz == String.class //
                ) {
            return new String[]{clazz.getSimpleName(), defaultValue.toString()};
        }
        return null;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getArrayType() */
    @Override
    public String[] getArrayType() {
        if (defaultValue instanceof List<?>) {
            List<?> list = (List<?>)defaultValue;
            if (list.size() == 0)
                return new String[0];

            if (list.get(0) instanceof AnnotationNode)
                return null;

            String[] arr = new String[list.size() + 1];
            for (int i = 0; i < list.size(); i++) {
                arr[i + 1] = list.get(i).toString();
            }
            arr[0] = list.get(0).getClass().getSimpleName();
            return arr;
        } else if (defaultValue.getClass().isArray()) {
            //this is enum
            if (String.class == defaultValue.getClass().getComponentType())
                return null;

            String[] arr = new String[Array.getLength(defaultValue) + 1];
            for (int i = 0; i < Array.getLength(defaultValue); i++) {
                arr[i + 1] = Array.get(defaultValue, i).toString();
            }
            arr[0] = Array.get(defaultValue, 0).getClass().getSimpleName();
            return arr;
        }
        return null;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getClassSignature() */
    @Override
    public String getClassSignature() {
        if (defaultValue instanceof Type) {
            return ((Type)defaultValue).getDescriptor();
        }
        return null;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getEnumConstant() */
    @Override
    public String[] getEnumConstant() {
        if (defaultValue instanceof String[]) {
            return (String[])defaultValue;
        } else
            return null;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getAnnotation() */
    @Override
    public Annotation getAnnotation() {
        if (defaultValue instanceof AnnotationNode) {
            return new AsmAnnotation((AnnotationNode)defaultValue);
        }
        return null;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getAnnotationArray() */
    @Override
    public Annotation[] getAnnotations() {
        if (defaultValue instanceof List<?>) {
            List<?> list = (List<?>)defaultValue;
            if (list.size() == 0)
                return new Annotation[0];
            if (list.get(0) instanceof AnnotationNode) {
                Annotation[] ann = new Annotation[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    ann[i] = new AsmAnnotation((AnnotationNode)list.get(i));
                }
                return ann;
            } else
                return null;
        }
        return null;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setPrimitiveType(java.lang.String[]) */
    @Override
    public void setPrimitiveType(String[] value) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setArrayType(java.lang.String[]) */
    @Override
    public void setArrayType(String[] value) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setClassSignature() */
    @Override
    public void setClassSignature(String value) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setEnumConstant(java.lang.String[]) */
    @Override
    public void setEnumConstant(String[] value) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setAnnotation(org.exoplatform.ide.codeassistant.jvm.shared
     * .Annotation) */
    @Override
    public void setAnnotation(Annotation annotation) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setAnnotationArray(org.exoplatform.ide.codeassistant.jvm.shared
     * .Annotation[]) */
    @Override
    public void setAnnotations(Annotation[] annotation) {
        throw new UnsupportedOperationException("Set not supported");
    }

}
