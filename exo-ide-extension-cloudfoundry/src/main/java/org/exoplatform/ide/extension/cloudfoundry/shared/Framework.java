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

    /**
     * Get list of runtimes.
     * 
     * @return runtime list
     */
    List<Runtime> getRuntimes();

    /**
     * Set runtimes.
     * 
     * @param runtimes runtime list to set
     */
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