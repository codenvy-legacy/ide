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
package com.codenvy.ide.extension.android.server;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
public class AndroidRunnerApplication extends Application {
    private final Set<Class<?>> classes;

    public AndroidRunnerApplication() {
        classes = new HashSet<Class<?>>(1);
        classes.add(AndroidRunnerService.class);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}
