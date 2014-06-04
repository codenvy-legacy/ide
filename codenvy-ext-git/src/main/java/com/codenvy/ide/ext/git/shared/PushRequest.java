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

import java.util.List;

/**
 * Request to update remote refs using local refs. In other words send changes from local repository to remote one.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PushRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
@DTO
public interface PushRequest extends GitRequest {
    /** @return list of refspec to push */
    List<String> getRefSpec();
    
    void setRefSpec(List<String> refSpec);
    
    PushRequest withRefSpec(List<String> refspec);

    /** @return remote repository. URI or name is acceptable. If not specified then 'origin' will be used */
    String getRemote();
    
    void setRemote(String remote);

    PushRequest withRemote(String remote);
    
    /** @return force or not push operation */
    boolean isForce();
    
    void setForce(boolean isForce);
    
    PushRequest withForce(boolean force);

    /** @return time (in seconds) to wait without data transfer occurring before aborting pushing data to remote repository */
    int getTimeout();
    
    void setTimeout(int timeout);
    
    PushRequest withTimeout(int timeout);
}