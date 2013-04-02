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