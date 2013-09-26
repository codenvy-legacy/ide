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

import com.google.gwt.user.client.Window;
import com.googlecode.gwt.test.patchers.PatchClass;
import com.googlecode.gwt.test.patchers.PatchMethod;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
@PatchClass(Window.class)
public class WindowPatcher {
    public static final String RETURNED_MESSAGE = "returned text";

    @PatchMethod(override = true)
    public static String prompt(String msg, String initialValue) {
        return RETURNED_MESSAGE;
    }

    @PatchMethod(override = true)
    public static boolean confirm(String msg) {
        return true;
    }

    @PatchMethod(override = true)
    public static void alert(String msg) {
        // do nothing
    }
}