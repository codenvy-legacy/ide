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
package org.exoplatform.ide.extension.appfog.server;

import org.exoplatform.ide.extension.appfog.shared.Framework;
import org.exoplatform.ide.extension.appfog.shared.Runtime;

import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class FrameworkImpl implements Framework {
    private String        name;
    private List<Runtime> runtimes;
    private String        description;
    private int           memory;
    private String        displayName;

    public FrameworkImpl(String name, String displayName, List<Runtime> runtimes, int memory, String description) {
        this.name = name;
        this.displayName = displayName;
        this.runtimes = runtimes;
        this.memory = memory;
        this.description = description;
    }

    public FrameworkImpl() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<Runtime> getRuntimes() {
        return runtimes;
    }

    @Override
    public void setRuntimes(List<Runtime> runtimes) {
        this.runtimes = runtimes;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getMemory() {
        return memory;
    }

    @Override
    public void setMemory(int memory) {
        this.memory = memory;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "FrameworkImpl{" +
               "name='" + name + '\'' +
               ", runtimes=" + runtimes +
               ", description='" + description + '\'' +
               ", memory=" + memory +
               ", displayName='" + displayName + '\'' +
               '}';
    }
}
