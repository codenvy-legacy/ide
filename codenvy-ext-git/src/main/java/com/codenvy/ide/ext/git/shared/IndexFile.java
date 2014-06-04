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
 * Git file in index. Used for work with index (remove, reset).
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 13, 2011 11:57:38 AM anya $
 */
@DTO
public interface IndexFile {
    /** @return the indexed if <code>true</code> file is in index */
    boolean isIndexed();
    
    void setIndexed(boolean indexed);
    
    IndexFile withIndexed(boolean indexed);

    /** @return file path */
    String getPath();
    
    void setPath(String path);
    
    IndexFile withPath(String path);
}