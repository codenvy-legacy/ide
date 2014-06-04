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
 * Request to commit current state of index in new commit.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CommitRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface CommitRequest extends GitRequest {
    /** @return commit message */
    String getMessage();
    
    void setMessage(String message);
    
    CommitRequest withMessage(String message);

    /** @return <code>true</code> if need automatically stage files that have been modified and deleted */
    boolean isAll();
    
    void setAll(boolean isAll);
    
    CommitRequest withAll(boolean all);

    /** @return <code>true</code> in case when commit is amending a previous commit. */
    boolean isAmend();
    
    void setAmend(boolean isAmend);
    
    CommitRequest withAmend(boolean amend);
}