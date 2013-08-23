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
