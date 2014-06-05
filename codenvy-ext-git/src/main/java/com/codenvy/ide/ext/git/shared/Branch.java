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
package com.codenvy.ide.ext.git.shared;

import com.codenvy.dto.shared.DTO;

/**
 * Git branch description.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Branch.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface Branch {
    /** @return full name of branch, e.g. 'refs/heads/master' */
    String getName();

    /** @return <code>true</code> if branch is checked out and false otherwise */
    boolean isActive();

    /** @return display name of branch, e.g. 'refs/heads/master' -> 'master' */
    String getDisplayName();

    /** @return <code>true</code> if branch is a remote branch */
    boolean isRemote();
    
    Branch withName(String name);
    
    Branch withDisplayName(String displayName);

    Branch withActive(boolean isActive);
    
    Branch withRemote(boolean isRemote);
}