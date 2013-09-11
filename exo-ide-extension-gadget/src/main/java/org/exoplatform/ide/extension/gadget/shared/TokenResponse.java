/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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