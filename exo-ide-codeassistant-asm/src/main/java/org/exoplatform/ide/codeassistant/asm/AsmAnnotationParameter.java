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

import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter;
import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class AsmAnnotationParameter implements AnnotationParameter {

    private String name;

    private Object value;

    /**
     * @param name
     * @param value
     */
    public AsmAnnotationParameter(String name, Object value) {
        super();
        this.name = name;
        this.value = value;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter#getName() */
    @Override
    public String getName() {
        return name;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter#getValue() */
    @Override
    public AnnotationValue getValue() {
        return new AsmAnnotationValue(value);
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter#setName(java.lang.String) */
    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter#setValue(org.exoplatform.ide.codeassistant.jvm.shared
     * .AnnotationValue) */
    @Override
    public void setValue(AnnotationValue value) {
        throw new UnsupportedOperationException("Set not supported");
    }

}
