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
 * Request to create new tag.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TagCreateRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface TagCreateRequest extends GitRequest {
    /** @return name of tag to create */
    String getName();
    
    void setName(String name);

    /** @return commit to make tag. If <code>null</code> then HEAD is used */
    String getCommit();
    
    void setCommit(String commit);

    /** @return message for tag */
    String getMessage();
    
    void setMessage(String message);

    /** @return force create tag operation */
    boolean isForce();
    
    void setForce(boolean isForce);
}