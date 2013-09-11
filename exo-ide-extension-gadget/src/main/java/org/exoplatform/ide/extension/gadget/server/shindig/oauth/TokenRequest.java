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
public class TokenRequest {

    /**
     *
     */
    public static final String GADGET_URL = "gadgetURL";

    /**
     *
     */
    public static final String OWNER = "owner";

    /**
     *
     */
    public static final String VIEWER = "viewer";

    /**
     *
     */
    public static final String MODULE_ID = "moduleId";

    /**
     *
     */
    public static final String CONTAINER = "container";

    /**
     *
     */
    public static final String DOMAIN = "domain";

    /**
     *
     */
    private String gadgetURL;

    /**
     *
     */
    private String owner;

    /**
     *
     */
    private String viewer;

    /**
     *
     */
    private Long moduleId;

    /**
     *
     */
    private String container;

    /**
     *
     */
    private String domain;

    /**
     *
     */
    public TokenRequest() {
    }

    /**
     * @param gadgetURL
     *         the gadget URL
     * @param owner
     *         gadget owner
     * @param viewer
     *         gadget viewer
     * @param moduleId
     *         moduleId
     * @param container
     *         container that is issuing the token
     * @param domain
     *         domain to use for signed fetch with default signed fetch key.
     */
    public TokenRequest(String gadgetURL, String owner, String viewer, Long moduleId, String container, String domain) {
        this.gadgetURL = gadgetURL;
        this.owner = owner;
        this.viewer = viewer;
        this.moduleId = moduleId;
        this.container = container;
        this.domain = domain;
    }

    /** @return the gadgetURL */
    public String getGadgetURL() {
        return gadgetURL;
    }

    /**
     * @param gadgetURL
     *         the gadgetURL to set
     */
    public void setGadgetURL(String gadgetURL) {
        this.gadgetURL = gadgetURL;
    }

    /** @return the owner */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner
     *         the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /** @return the viewer */
    public String getViewer() {
        return viewer;
    }

    /**
     * @param viewer
     *         the viewer to set
     */
    public void setViewer(String viewer) {
        this.viewer = viewer;
    }

    /** @return the moduleId */
    public Long getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId
     *         the moduleId to set
     */
    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    /** @return the container */
    public String getContainer() {
        return container;
    }

    /**
     * @param container
     *         the container to set
     */
    public void setContainer(String container) {
        this.container = container;
    }

    /** @return the domain */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain
     *         the domain to set
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        String json =
                "{\"" + GADGET_URL + "\":\"" + gadgetURL + "\",\"" + OWNER + "\":\"" + owner + "\",\"" + VIEWER + "\":\""
                + viewer + "\",\"" + MODULE_ID + "\":" + moduleId + ",\"" + CONTAINER + "\":\"" + container + "\",\""
                + DOMAIN + "\":\"" + domain + "\"}";
        return json;

    }

}
