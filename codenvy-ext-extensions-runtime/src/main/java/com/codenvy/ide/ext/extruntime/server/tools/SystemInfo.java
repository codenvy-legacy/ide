/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extruntime.server.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Provides information about operating system.
 *
 * @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a>
 */
public class SystemInfo {
    private static final Logger LOG = LoggerFactory.getLogger(SystemInfo.class);

    public static final  String  OS      = System.getProperty("os.name").toLowerCase();
    private static final boolean linux   = OS.startsWith("linux");
    private static final boolean windows = OS.startsWith("windows");
    private static final boolean unix    = !windows;

    public static boolean isLinux() {
        return linux;
    }

    public static boolean isWindows() {
        return windows;
    }

    public static boolean isUnix() {
        return unix;
    }

    private static interface SystemResources {
        /** Get CPU load in percents (1-100). Return -1 when fails getting CPU load. */
        int cpu();

        /** Get free physical memory in bytes. Return -1 when fails getting amount of free memory. */
        long freeMemory();

        /** Get total physical memory in bytes. Return -1 when fails getting amount of total memory. */
        long totalMemory();
    }

    /*
    http://docs.oracle.com/javase/7/docs/jre/api/management/extension/com/sun/management/OperatingSystemMXBean.html
     */
    private static final OperatingSystemMXBean OPERATING_SYSTEM_MX_BEAN = ManagementFactory.getOperatingSystemMXBean();
    private static final Method CPU_USAGE;
    private static final Method TOTAL_MEMORY;
    private static final Method FREE_MEMORY;

    static {
        final Class<? extends OperatingSystemMXBean> clazz = OPERATING_SYSTEM_MX_BEAN.getClass();
        CPU_USAGE = getMethodSafety(clazz, "getSystemCpuLoad");
        FREE_MEMORY = getMethodSafety(clazz, "getFreePhysicalMemorySize");
        TOTAL_MEMORY = getMethodSafety(clazz, "getTotalPhysicalMemorySize");
    }

    private static Method getMethodSafety(Class<?> clazz, String name) {
        try {
            final Method m = clazz.getMethod(name);
            m.setAccessible(true);
            return m;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static final SystemResources JDK_SYSTEM_RESOURCES = new SystemResources() {
        @Override
        public int cpu() {
            if (CPU_USAGE != null) {
                try {
                    return (int)((Double)CPU_USAGE.invoke(OPERATING_SYSTEM_MX_BEAN) * 100);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            return -1;
        }

        @Override
        public long freeMemory() {
            if (FREE_MEMORY != null) {
                try {
                    return (Long)FREE_MEMORY.invoke(OPERATING_SYSTEM_MX_BEAN);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            return -1L;
        }

        @Override
        public long totalMemory() {
            if (TOTAL_MEMORY != null) {
                try {
                    return (Long)TOTAL_MEMORY.invoke(OPERATING_SYSTEM_MX_BEAN);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
            return -1L;
        }
    };

    /* not sure about all unix and don't really need it */
    private static final SystemResources LINUX_SYSTEM_RESOURCES = new SystemResources() {
        /*
        See 'man proc', section about '/proc/stat'.
        Values in array:
        [0] - user time
        [1] - nice time
        [2] - system time
        [3] - idle time

        For now do not count CPU usage for each available core.
         */
        private int[] cpuTimes = {0, 0, 0, 0};

        @Override
        public synchronized int cpu() {
            int[] newCpuTimes;
            try {
                newCpuTimes = readCpuTimes();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                return -1;
            }
            int usage = 0;
            for (int i = 0; i < 3; i++) {
                usage += newCpuTimes[i] - cpuTimes[i];
            }
            int idle = newCpuTimes[3] - cpuTimes[3];
            int total = usage + idle;
            int percent = 0;
            if (total != 0) {
                percent = (100 * usage) / total;
            }
            cpuTimes = newCpuTimes;
            return percent;
        }

        private int[] readCpuTimes() throws IOException {
            final int[] times = new int[4];
            final String line;
            try (BufferedReader reader = new BufferedReader(new FileReader("/proc/stat"))) {
                line = reader.readLine(); // need first line only
            }
            if (line.startsWith("cpu")) { // just to be sure we have correct line
                final String[] strings = line.split("\\s+");
                for (int i = 1; i < 5; i++) {
                    times[i - 1] = Integer.parseInt(strings[i]);
                }
            }
            return times;
        }


        @Override
        public long freeMemory() {
            try {
                return readMem("MemFree");
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
            return -1L;
        }

        @Override
        public long totalMemory() {
            try {
                return readMem("MemTotal");
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
            return -1L;
        }

        /*
        See 'man proc', section about '/proc/meminfo'
         */
        private long readMem(String kindOf) throws IOException {
            String line;
            try (BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"))) {
                line = reader.readLine();
                while (line != null && !line.startsWith(kindOf)) {
                    line = reader.readLine();
                }
            }
            if (line != null) {
                final String[] strings = line.split("\\s+");
                long mem = Long.parseLong(strings[1]);
                // never saw something different than 'kB', but man page says: "unit of measurement (e.g., "kB")"
                if (strings.length > 2) {
                    final String unit = strings[2].toLowerCase();
                    if (unit.equals("kb")) {
                        mem *= 1024;
                    }
                    // enough for the moment
                }
                return mem;
            }
            return -1;
        }
    };

    public static int cpu() {
        int cpuUsage = JDK_SYSTEM_RESOURCES.cpu();
        if (cpuUsage < 0 && isLinux()) {
            cpuUsage = LINUX_SYSTEM_RESOURCES.cpu();
        }
        return cpuUsage;
    }

    public static long freeMemory() {
        long free = JDK_SYSTEM_RESOURCES.freeMemory();
        if (free < 0 && isLinux()) {
            free = LINUX_SYSTEM_RESOURCES.freeMemory();
        }
        return free;
    }

    public static long totalMemory() {
        long total = JDK_SYSTEM_RESOURCES.totalMemory();
        if (total < 0 && isLinux()) {
            total = LINUX_SYSTEM_RESOURCES.totalMemory();
        }
        return total;
    }

    private SystemInfo() {
    }
}
