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
 * Request to create new branch.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchCreateRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface BranchCreateRequest extends GitRequest {
    /** @return name of branch to be created */
    String getName();
    
    void setName(String name);
    
    BranchCreateRequest withName(String name);

    /** @return name of a commit at which to start the new branch. If <code>null</code> the HEAD will be used */
    String getStartPoint();
    
    void setStartPoint(String startPoint);
    
    BranchCreateRequest withStartPoint(String startPoint);
}