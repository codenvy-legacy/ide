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
package org.exoplatform.ide.git.client;

import junit.framework.Test;
import junit.framework.TestCase;

import com.google.gwt.junit.tools.GWTTestSuite;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 27, 2011 12:28:59 PM anya $
 */
public class GwtMarshallerTestSuite extends TestCase {
    public static Test suite() {
        GWTTestSuite suite = new GWTTestSuite("Codenvy Git Marshaller GWT Tests");
        suite.addTestSuite(AddRequestMarshallerGwtTest.class);
        suite.addTestSuite(BranchesMarshallerGwtTest.class);
        suite.addTestSuite(CloneMarshallerGwtTest.class);
        suite.addTestSuite(CommitMarshallerGwtTest.class);
        suite.addTestSuite(FetchMarshallerGwtTest.class);
        suite.addTestSuite(InitMarshallerGwtTest.class);
        suite.addTestSuite(LogMarshallerGwtTest.class);
        suite.addTestSuite(PushMarshallerGwtTest.class);
        suite.addTestSuite(PullMarshallerGwtTest.class);
        suite.addTestSuite(RemotesMarshallerGwtTest.class);
        suite.addTestSuite(RemoveMarshallerGwtTest.class);
        suite.addTestSuite(ResetMarshallerGwtTest.class);
        suite.addTestSuite(DiffMarshallerGwtTest.class);
        return suite;
    }
}
