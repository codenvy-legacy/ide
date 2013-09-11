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
package org.exoplatform.ide.client.framework.annotation;

import java.util.HashMap;
import java.util.List;

/**
 * Class is used to represent the the class type's annotation values, kept in {@link HashMap}, where key is class's FQN, values -
 * the list of annotation values.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 21, 2010 $
 */
public abstract class ClassAnnotationMap {
    /** Class annotation map. */
    protected HashMap<String, List<String>> classAnnotations = new HashMap<String, List<String>>();

    /** @return {@link HashMap} class annotation map */
    public HashMap<String, List<String>> getClassAnnotations() {
        return classAnnotations;
    }
}
