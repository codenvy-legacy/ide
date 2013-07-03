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
package com.codenvy.ide.ext.aws.shared.ec2;

import java.util.ArrayList;
import java.util.List;

/**
 * EC2 image type
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public enum ImageType {
    t1_micro("Micro", "t1.micro", 1, "Up to 2 ECUs", "613 MiB"),
    m1_small("Small", "m1.small", 1, "1 ECU", "1.7 GiB"),
    m1_medium("Medium", "m1.medium", 1, "2 ECU", "3.7 GiB"),
    m1_large("Large", "m1.large", 2, "4 ECU", "7.5 GiB"),
    m1_xlarge("Extra Large", "m1.xlarge", 4, "8 ECU", "15 GiB"),
    m2_xlarge("High-Memory Extra Large", "m2.xlarge", 2, "6.5 ECU", "17.1 GiB"),
    m2_2xlarge("High-Memory Double Extra Large", "m2.2xlarge", 4, "13 ECU", "34.2 GiB"),
    m2_4xlarge("High-Memory Quadruple Extra Large", "m2.4xlarge", 8, "26 ECU", "68.4 GiB"),
    c1_medium("High-CPU Medium", "c1.medium", 2, "5 ECU", "1.7 GiB"),
    c1_xlarge("High-CPU Extra Large", "c1.xlarge", 8, "20 ECU", "7 GiB"),
    hi1_4xlarge("High I/O Quadruple Extra Large", "hi1.4xlarge", 8, "35 ECU", "60.5 GiB");

    private final        String       name;
    private final        String       id;
    private final        int          cpuCores;
    private final        String       cpuUnits;
    private final        String       memory;
    private static final List<String> availableInstanceTypes;

    static {
        List<String> types = new ArrayList<String>(11);
        for (ImageType image : ImageType.values()) {
            types.add(image.getId());
        }
        availableInstanceTypes = java.util.Collections.unmodifiableList(types);
    }

    public List<String> availableInstanceTypes() {
        return availableInstanceTypes;
    }

    private ImageType(String name, String id, int cpuCores, String cpuUnits, String memory) {
        this.name = name;
        this.id = id;
        this.cpuCores = cpuCores;
        this.cpuUnits = cpuUnits;
        this.memory = memory;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public String getCpuUnits() {
        return cpuUnits;
    }

    public String getMemory() {
        return memory;
    }

    @Override
    public String toString() {
        return "Image{" +
               "name='" + name + '\'' +
               ", id='" + id + '\'' +
               ", cpuCores=" + cpuCores +
               ", cpuUnits='" + cpuUnits + '\'' +
               ", memory='" + memory + '\'' +
               '}';
    }
}
