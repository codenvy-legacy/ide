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
package org.exoplatform.ide.extension.googleappengine.client;

import org.exoplatform.ide.security.shared.Token;

/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id: GaeTools.java Apr 18, 2013 vetal $
 */
public class GaeTools {

    public static final String APPENGINE_ADMIN_SCOPE = "https://www.googleapis.com/auth/appengine.admin";

    public static boolean isAuthenticatedInAppEngine(Token token) {
        if (token == null || token.getToken() == null || token.getToken().isEmpty())
            return false;
        if (token.getScope() == null || token.getScope().isEmpty())
            return false;
        return token.getScope().contains(APPENGINE_ADMIN_SCOPE);
    }


}
