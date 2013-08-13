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

    /**
     * The unique Name of the AMI.
     *
     * @return The unique Name of the AMI.
     */
    public String getName() {
        return name;
    }

    /**
     * The unique ID of the AMI.
     *
     * @return The unique ID of the AMI.
     */
    public String getId() {
        return id;
    }

    /**
     * The CPU Cores that need to run for this image.
     *
     * @return The CPU Cores that need to run for this image.
     */
    public int getCpuCores() {
        return cpuCores;
    }

    /**
     * The CPU Units that need to run for this image.
     *
     * @return The CPU Units that need to run for this image.
     */
    public String getCpuUnits() {
        return cpuUnits;
    }

    /**
     * The Memory that need to run for this image.
     *
     * @return The Memory that need to run for this image.
     */
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
