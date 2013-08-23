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

package org.exoplatform.ide.client;

/**
 * This class was creates to helps developers to use native browser's alert function. When any file is opened, CKEditor overwrites
 * native function and any trying to do alert prevents to opens CKEditor's dialog window.
 * <p/>
 * Call init() function at the start of application to remember browser's alert function and then use alert(...) like
 * Window.alert(...)
 * <p/>
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Alert {

    public static final native void init() /*-{
        var alertFunc = $wnd.alert;
        $wnd.nativeAlertFunction = alertFunc;
    }-*/;

    public static final native void alert(String message) /*-{
        $wnd.nativeAlertFunction(message);
    }-*/;

}
