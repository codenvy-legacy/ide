/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.core.internal.resources;

import com.codenvy.eclipse.core.resources.IProject;
import com.codenvy.eclipse.core.resources.IResourceRuleFactory;
import com.codenvy.eclipse.resources.WorkspaceResource;

/**
 * The internal abstract superclass of all TeamHook implementations.  This superclass
 * provides access to internal non-API methods that are not available from the API
 * package. Plugin developers should not subclass this class.
 *
 * @see com.codenvy.eclipse.core.resources.team.TeamHook
 */
public class InternalTeamHook {
    /* (non-Javadoc)
     * Internal implementation of TeamHook#setRulesFor(IProject,IResourceRuleFactory)
     */
    protected void setRuleFactory(IProject project, IResourceRuleFactory factory) {
        WorkspaceResource workspace = ((WorkspaceResource)project.getWorkspace());
        ((Rules)workspace.getRuleFactory()).setRuleFactory(project, factory);
    }
}
