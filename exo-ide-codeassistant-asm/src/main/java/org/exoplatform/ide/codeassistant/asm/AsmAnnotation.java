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
import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationParameter;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.Iterator;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class AsmAnnotation implements Annotation {

    private AnnotationNode annotationNode;

    /** @param annotationNode */
    public AsmAnnotation(AnnotationNode annotationNode) {
        this.annotationNode = annotationNode;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#getTypeName() */
    @Override
    public String getTypeName() {
        return annotationNode.desc;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#getAnnotationParameters() */
    @Override
    public AnnotationParameter[] getAnnotationParameters() {
        if (annotationNode.values == null)
            return new AnnotationParameter[0];

        AnnotationParameter[] param = new AnnotationParameter[annotationNode.values.size() / 2];
        int i = 0;
        for (Iterator iterator = annotationNode.values.iterator(); iterator.hasNext(); ) {
            Object type = iterator.next();
            param[i] = new AsmAnnotationParameter(type.toString(), iterator.next());
            i++;
        }
        return param;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#setTypeName() */
    @Override
    public void setTypeName(String name) {
        throw new UnsupportedOperationException("Set not supported");
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.Annotation#setAnnotationParameters(org.exoplatform.ide.codeassistant.jvm.shared
     * .AnnotationParameter[]) */
    @Override
    public void setAnnotationParameters(AnnotationParameter[] parameters) {
        throw new UnsupportedOperationException("Set not supported");
    }

}
