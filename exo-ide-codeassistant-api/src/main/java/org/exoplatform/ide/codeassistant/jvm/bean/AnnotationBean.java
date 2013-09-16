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
import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class AnnotationBean implements Annotation {

    private String typeName;

    private AnnotationParameter[] annotationParameters;

    /**
     *
     */
    public AnnotationBean() {
    }

    /**
     * @param typeName
     * @param annotationParameters
     */
    public AnnotationBean(String typeName, AnnotationParameter[] annotationParameters) {
        super();
        this.typeName = typeName;
        this.annotationParameters = annotationParameters;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#getTypeName() */
    @Override
    public String getTypeName() {
        return typeName;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#getAnnotationParameters() */
    @Override
    public AnnotationParameter[] getAnnotationParameters() {
        return annotationParameters;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#setTypeName() */
    @Override
    public void setTypeName(String name) {
        typeName = name;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#setAnnotationParameters(org.exoplatform.ide.codeassistant.jvm.shared
     * .AnnotationParameter[]) */
    @Override
    public void setAnnotationParameters(AnnotationParameter[] parameters) {
        annotationParameters = parameters;
    }

}
