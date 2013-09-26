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
package com.codenvy.ide.ext.git.client.patcher;

import com.codenvy.ide.security.oauth.JsOAuthWindow;
import com.googlecode.gwt.test.patchers.PatchClass;
import com.googlecode.gwt.test.patchers.PatchMethod;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@PatchClass(JsOAuthWindow.class)
public class JsOAuthWindowPatcher {

    @PatchMethod(override = true)
    public static void loginWithOAuth(JsOAuthWindow window, String authUrl, String errUrl, int popupHeight, int popupWidth,
                                      int clientHeight, int clientWidth) {
        // do nothing
    }
}