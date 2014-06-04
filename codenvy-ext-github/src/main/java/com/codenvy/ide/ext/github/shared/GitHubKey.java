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
package com.codenvy.ide.ext.github.shared;

import com.codenvy.dto.shared.DTO;

/**
 * GitHub SSH key, taken from API v3.
 *
 * @author Vladyslav Zhukovskii
 */
@DTO
public interface GitHubKey {
    int getId();

    void setId(int id);

    String getKey();

    void setKey(String key);

    String getUrl();

    void setUrl(String url);

    String getTitle();

    void setTitle(String title);
}
