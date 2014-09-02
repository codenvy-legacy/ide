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
package com.codenvy.ide.api.projecttree.generic;

/**
 * An interface defines methods common to all nodes
 * represent an item retrieved from Codenvy Project API.
 *
 * @author Artem Zatsarynnyy
 */
public interface StorableNode {
    /** Returns name of the item which this node represents. */
    String getName();

    /** Returns path of the item which this node represents. */
    String getPath();

    /** Returns node's project. */
    ProjectRootNode getProject();
}
