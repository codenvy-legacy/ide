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
package org.exoplatform.ide.client.framework.discovery;

import java.util.List;

/**
 * Interface describe list of REST-services.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: RestServicesList.java Mar 26, 2012 11:22:53 AM azatsarynnyy $
 */
public interface RestServicesList {
    /**
     * Returns the list of REST-services.
     *
     * @return the root resources
     */
    public List<IRestService> getRootResources();

    /**
     * Set the list of REST-services.
     *
     * @param rootResources
     *         the root resources
     */
    public void setRootResources(List<IRestService> rootResources);
}
