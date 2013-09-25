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
package com.codenvy.ide.core;

import com.google.gwt.core.client.Callback;

/**
 * Components that have to be started on application's startup
 * must implement this interface. Please don't directly implement this interface
 * used {@link ComponentImpl} instead.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface Component {
    /**
     * Starts Component. It should send corresponding Event, when started.
     * Please refer for {@link ComponentImpl}
     *
     * @throws Exception
     */
    public void start(Callback<Component, ComponentException> callback);
}
