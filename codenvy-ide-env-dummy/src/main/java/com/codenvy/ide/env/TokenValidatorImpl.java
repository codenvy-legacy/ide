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
package com.codenvy.ide.env;

import com.codenvy.api.core.ConflictException;
import com.codenvy.api.user.server.TokenValidator;

/**
 * Temporary dummy implementation of {@link TokenValidator}.
 * 
 * @author Ann Shumilova
 */
public class TokenValidatorImpl implements TokenValidator {

    /** {@inheritDoc} */
    @Override
    public String validateToken(String token) throws ConflictException {
        //TODO
        return "codenvy@codenvy.com";
    }

}
