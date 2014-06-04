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
 * Request to move or rename a file or directory.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MoveRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface MoveRequest extends GitRequest {
    /** @return source */
    String getSource();
    
    void setSource(String source);

    /** @return target */
    String getTarget();
    
    void setTarget(String target);
}