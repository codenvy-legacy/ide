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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 27, 2011 11:42:28 AM anya $
 */
public class BaseGwtTest extends GWTTestCase {

    /** @see com.google.gwt.junit.client.GWTTestCase#getModuleName() */
    @Override
    public String getModuleName() {
        return "org.exoplatform.ide.git.GitTest";
    }

    /**
     * Build {@link JavaScriptObject} from string.
     *
     * @param json
     *         string that contains object
     * @return {@link JavaScriptObject}
     */
    protected static native JavaScriptObject build(String json) /*-{
        try {
            var object = eval('(' + json + ')');
            return object;
        } catch (e) {
            return null;
        }
    }-*/;
}
