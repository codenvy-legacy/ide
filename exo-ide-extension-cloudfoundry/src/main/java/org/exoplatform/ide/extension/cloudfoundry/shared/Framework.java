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
package org.exoplatform.ide.extension.cloudfoundry.shared;

import java.util.List;

/**
 * Framework info.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: Framework.java Mar 16, 2012 5:14:15 PM azatsarynnyy $
 */
public interface Framework {
    /**
     * Get the framework name.
     *
     * @return framework name
     */
    String getName();

    /**
     * Set the framework name.
     *
     * @param name
     *         framework name
     */
    void setName(String name);

    List<Runtime> getRuntimes();

    void setRuntimes(List<Runtime> runtimes);

    /**
     * Get framework description.
     *
     * @return framework description
     */
    String getDescription();

    /**
     * Set framework description.
     *
     * @param description
     *         framework description.
     */
    void setDescription(String description);

    /**
     * Get default memory size in megabytes.
     *
     * @return memory size
     */
    int getMemory();

    /**
     * Set memory size in megabytes.
     *
     * @param memory
     *         memory size in megabytes
     */
    void setMemory(int memory);

    String getDisplayName();

    void setDisplayName(String displayName);
}