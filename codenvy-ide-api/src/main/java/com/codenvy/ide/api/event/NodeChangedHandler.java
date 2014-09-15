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
 * A handler for receiving {@link NodeChangedEvent}s.
 *
 * @author Artem Zatsarynnyy
 */
public interface NodeChangedHandler extends EventHandler {

    /**
     * Invoked when a node was renamed.
     *
     * @param event
     *         an event specifying the details about the renamed node
     */
    void onNodeRenamed(NodeChangedEvent event);

    /**
     * Invoked when a node is added to or removed from a parent node.
     *
     * @param event
     *         an event specifying the details about the whose children have changed
     */
    void onNodeChildrenChanged(NodeChangedEvent event);
}