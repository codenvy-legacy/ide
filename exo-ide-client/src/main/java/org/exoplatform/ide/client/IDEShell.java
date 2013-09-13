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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.shared.UmbrellaException;

import org.exoplatform.gwtframework.commons.util.Log;

import java.util.Set;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class IDEShell implements EntryPoint {

    public void onModuleLoad() {
        GWT.setUncaughtExceptionHandler(new H());
        new IDE();
    }

    private class H implements UncaughtExceptionHandler {

        public void onUncaughtException(Throwable e) {
            Log.info(e.getMessage());
            if (e instanceof UmbrellaException) {
                Set<Throwable> set = ((UmbrellaException)e).getCauses();
                for (Throwable t : set) {
                    Log.info(t.getMessage());
                    t.printStackTrace();
                }
            } else {
                e.printStackTrace();
            }
        }
    }

}
