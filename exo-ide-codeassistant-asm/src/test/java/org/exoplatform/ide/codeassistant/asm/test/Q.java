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
package org.exoplatform.ide.codeassistant.asm.test;

import java.io.IOException;
import java.util.ConcurrentModificationException;

public class Q {

    class U {
    }

    public U getInnerClass(Class<? extends U> t) {
        return null;
    }

    public void methodWithExceptions(Class<?> t) throws IOException, ConcurrentModificationException {
    }

    public <T extends ConcurrentModificationException> T methodWithExceptionAsParams(ConcurrentModificationException a) {
        return null;
    }

}
