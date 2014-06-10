/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
