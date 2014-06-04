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
package com.codenvy.ide.ext.java.jdi.client.fqn;

import com.codenvy.ide.api.resources.model.File;

import javax.validation.constraints.NotNull;

/**
 * @author Evgen Vidolob
 */
public interface FqnResolver {
    @NotNull
    String resolveFqn(@NotNull File file);
}