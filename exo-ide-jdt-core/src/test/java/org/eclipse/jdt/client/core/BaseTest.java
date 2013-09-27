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
package org.eclipse.jdt.client.core;

import com.googlecode.gwt.test.GwtTest;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 4:00:10 PM 34360 2009-07-22 23:58:59Z evgen $
 */
public abstract class BaseTest extends GwtTest {

    /**
     *
     */
    public BaseTest() {
        super();
    }

    /** @see com.google.gwt.junit.client.GWTTestCase#getModuleName() */
    @Override
    public String getModuleName() {
        return "org.eclipse.jdt.IdeJdt";
    }

}