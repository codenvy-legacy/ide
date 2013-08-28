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
package org.exoplatform.ide.client.framework.application;

import org.exoplatform.gwtframework.ui.client.component.GWTLoader;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDELoader {

    private static GWTLoader loader;

    public static GWTLoader get() {
        if (loader == null) {
            loader = new GWTLoader() {
            };
        }

        return loader;
    }

    public static GWTLoader getInstance() {
        return get();
    }

    public static void show() {
        get().show();
    }

    public static void show(String message) {
        get().setMessage(message);
        get().show();
    }

    public static void hide() {
        get().hide();
    }

}
