/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.security.oauth;

import org.exoplatform.ide.security.shared.Token;

/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id: GoogleToken.java Apr 18, 2013 vetal $
 */
public class TokenImpl implements Token {

    private String scope;
    private String token;

    /**
     * @param token
     */
    public TokenImpl(String token) {
        this(token, null);
    }

    
    /**
     * @param scope
     * @param token
     */
    public TokenImpl(String token, String scope) {
        this.token = token;
        this.scope = scope;
    }

    /**
     * @see org.exoplatform.ide.security.shared.Token#getScope()
     */
    @Override
    public String getScope() {
        return scope;
    }

    /**
     * @see org.exoplatform.ide.security.shared.Token#setScope(java.lang.String)
     */
    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
        
    }
    
    


}
