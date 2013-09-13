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
package org.exoplatform.ide.extension.gadget.server.shindig.oauth;

/**
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class TokenResponse {
    public static final String SECURITY_TOKEN = "securityToken";

    public static final String GADGET_URL = "gadgetURL";

    public static final String MODULE_ID = "moduleId";

    private String securityToken;

    private String gadgetURL;

    private Long moduleId;

    public TokenResponse() {
    }

    public TokenResponse(String securityToken, String gadgetURL, Long moduleId) {
        this.securityToken = securityToken;
        this.gadgetURL = gadgetURL;
        this.moduleId = moduleId;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public String getGadgetURL() {
        return gadgetURL;
    }

    public void setGadgetURL(String gadgetURL) {
        this.gadgetURL = gadgetURL;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    @Override
    public String toString() {
        String json =
                "{\"" + GADGET_URL + "\":\"" + gadgetURL + "\",\"" + SECURITY_TOKEN + "\":\"" + securityToken + "\",\""
                + MODULE_ID + "\":\"" + moduleId + "\"}";
        return json;
    }
}
