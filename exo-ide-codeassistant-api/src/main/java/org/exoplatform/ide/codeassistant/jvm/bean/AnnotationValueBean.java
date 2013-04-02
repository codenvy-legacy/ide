/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.codeassistant.jvm.bean;

import org.exoplatform.ide.codeassistant.jvm.shared.Annotation;
import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class AnnotationValueBean implements AnnotationValue {

    private String[] primitiveType;

    private String[] arrayType;

    private String classSignature;

    private String[] enumConstant;

    private Annotation annotation;

    private Annotation[] annotations;

    /**
     *
     */
    public AnnotationValueBean() {
    }

    /**
     * @param primitiveType
     * @param arrayType
     * @param classSignature
     * @param enumConstant
     * @param annotation
     */
    public AnnotationValueBean(String[] primitiveType, String[] arrayType, String classSignature, String[] enumConstant,
                               Annotation annotation, Annotation[] annotations) {
        super();
        this.primitiveType = primitiveType;
        this.arrayType = arrayType;
        this.classSignature = classSignature;
        this.enumConstant = enumConstant;
        this.annotation = annotation;
        this.annotations = annotations;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getPrimitiveType() */
    @Override
    public String[] getPrimitiveType() {
        return primitiveType;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getArrayType() */
    @Override
    public String[] getArrayType() {
        return arrayType;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getClassSignature() */
    @Override
    public String getClassSignature() {
        return classSignature;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getEnumConstant() */
    @Override
    public String[] getEnumConstant() {
        return enumConstant;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getAnnotation() */
    @Override
    public Annotation getAnnotation() {
        return annotation;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setPrimitiveType(java.lang.String[]) */
    @Override
    public void setPrimitiveType(String[] value) {
        primitiveType = value;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setArrayType(java.lang.String[]) */
    @Override
    public void setArrayType(String[] value) {
        arrayType = value;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setClassSignature(java.lang.String) */
    @Override
    public void setClassSignature(String value) {
        classSignature = value;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setEnumConstant(java.lang.String[]) */
    @Override
    public void setEnumConstant(String[] value) {
        enumConstant = value;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setAnnotation(org.exoplatform.ide.codeassistant.jvm.shared
     * .Annotation) */
    @Override
    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#getAnnotationArray() */
    @Override
    public Annotation[] getAnnotations() {
        return annotations;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue#setAnnotationArray(org.exoplatform.ide.codeassistant.jvm.shared
     * .Annotation[]) */
    @Override
    public void setAnnotations(Annotation[] annotations) {
        this.annotations = annotations;
    }

}
