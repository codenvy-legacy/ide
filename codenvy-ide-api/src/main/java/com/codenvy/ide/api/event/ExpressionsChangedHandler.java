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
package com.codenvy.ide.api.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handles ExpressionsEvent Event
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface ExpressionsChangedHandler extends EventHandler {
    /**
     * The value of the Core Expressions changed. Event contains the list of IDs and new values
     *
     * @param event
     */
    void onExpressionsChanged(ExpressionsChangedEvent event);

}
