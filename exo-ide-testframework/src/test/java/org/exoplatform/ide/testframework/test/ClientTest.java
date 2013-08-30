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
package org.exoplatform.ide.testframework.test;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class ClientTest extends GWTTestCase {

    protected static final int SLEEP_TIME = 10000;

    @Override
    public String getModuleName() {
        return "org.exoplatform.ideall.testframework.IDEallTestFramework";
    }

    protected void sleepTest() {
        delayTestFinish(SLEEP_TIME);
    }

}
