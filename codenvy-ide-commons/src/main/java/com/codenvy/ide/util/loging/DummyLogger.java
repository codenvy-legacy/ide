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
package com.codenvy.ide.util.loging;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
class DummyLogger implements Logger {

    /** @see com.codenvy.ide.util.loging.Logger#debug(java.lang.Class, java.lang.Object[]) */
    @Override
    public void debug(Class<?> clazz, Object... args) {
    }

    /** @see com.codenvy.ide.util.loging.Logger#error(java.lang.Class, java.lang.Object[]) */
    @Override
    public void error(Class<?> clazz, Object... args) {
    }

    /** @see com.codenvy.ide.util.loging.Logger#info(java.lang.Class, java.lang.Object[]) */
    @Override
    public void info(Class<?> clazz, Object... args) {
    }

    /** @see com.codenvy.ide.util.loging.Logger#isLoggingEnabled() */
    @Override
    public boolean isLoggingEnabled() {
        return false;
    }

    /** @see com.codenvy.ide.util.loging.Logger#warn(java.lang.Class, java.lang.Object[]) */
    @Override
    public void warn(Class<?> clazz, Object... args) {
    }

}
