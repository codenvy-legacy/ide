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
 * Interface describe the request of security token.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: TokenRequest.java Mar 21, 2012 11:12:11 AM azatsarynnyy $
 */
public interface TokenRequest {

    /** @return the gadget's URL */
    public String getGadgetURL();

    /**
     * @param gadgetURL
     *         the gadget's URL to set
     */
    public void setGadgetURL(String gadgetURL);

    /** @return the owner */
    public String getOwner();

    /**
     * @param owner
     *         the owner to set
     */
    public void setOwner(String owner);

    /** @return the viewer */
    public String getViewer();

    /**
     * @param viewer
     *         the viewer to set
     */
    public void setViewer(String viewer);

    /** @return the module identifier */
    public Long getModuleId();

    /**
     * @param moduleId
     *         the module identifier to set
     */
    public void setModuleId(Long moduleId);

    /** @return the container */
    public String getContainer();

    /**
     * @param container
     *         the container to set
     */
    public void setContainer(String container);

    /** @return the domain */
    public String getDomain();

    /**
     * @param domain
     *         the domain to set
     */
    public void setDomain(String domain);

}