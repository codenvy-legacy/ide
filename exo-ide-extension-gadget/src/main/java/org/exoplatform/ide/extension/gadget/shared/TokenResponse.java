/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.gadget.shared;

/**
 * Interface describe the response of security token.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: TokenResponse.java Mar 21, 2012 11:12:11 AM azatsarynnyy $
 */
public interface TokenResponse {

    /**
     * Returns the security token.
     *
     * @return the security token
     */
    public String getSecurityToken();

    /**
     * Change the security token.
     *
     * @param securityToken
     *         the security token
     */
    public void setSecurityToken(String securityToken);

    /**
     * Returns the gadget's URL.
     *
     * @return the gadget's URL
     */
    public String getGadgetURL();

    /**
     * Set the gadget's URL.
     *
     * @param gadgetURL
     *         the gadget's URL
     */
    public void setGadgetURL(String gadgetURL);

    /**
     * Change the module identifier.
     * <p/>
     * Used double because the Java long type cannot be represented in JavaScript as a numeric type.
     * http://code.google.com/webtoolkit/doc/latest/DevGuideCodingBasicsJSNI.html#important
     *
     * @param moduleId
     *         the module identifier
     */
    public void setModuleId(double moduleId);

    /**
     * Returns the module identifier.
     * <p/>
     * Used double because the Java long type cannot be represented in JavaScript as a numeric type.
     * http://code.google.com/webtoolkit/doc/latest/DevGuideCodingBasicsJSNI.html#important
     *
     * @return the module ID
     */
    public double getModuleId();

}