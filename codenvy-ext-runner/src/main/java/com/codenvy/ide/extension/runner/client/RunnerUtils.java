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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Vitaly Parfonov
 */
public class RunnerUtils {

    @Nullable
    public static Link getLink(ApplicationProcessDescriptor processDescriptor, String rel) {
        if (processDescriptor == null)
            return null;
        List<Link> links = processDescriptor.getLinks();
        for (Link link : links) {
            if (link.getRel().equalsIgnoreCase(rel))
                return link;
        }
        return null;
    }
}
