/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.extruntime.server.tools;

import com.sun.jna.Library;
import com.sun.jna.Native;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Helpers to manage system processes.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public final class ProcessUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessUtil.class);

    private static final CLibrary C_LIBRARY;

    static {
        CLibrary tmp = null;
        try {
            if (SystemInfo.isUnix()) {
                tmp = ((CLibrary)Native.loadLibrary("c", CLibrary.class));
            }
        } catch (Exception e) {
            LOG.error("Cannot load native library", e);
        }
        C_LIBRARY = tmp;
    }

    private static interface ProcessManager {
        void kill(Process process);

        boolean isAlive(Process process);

        int getPid(Process process);
    }

    private static interface CLibrary extends Library {
        // kill -l
        int SIGKILL = 9;
        int SIGTERM = 15;

        int kill(int pid, int signal);

        String strerror(int errno);

        /*int system(String cmd);*/
    }

    private static final Pattern PS_TABLE_PATTERN = Pattern.compile("\\s+");

    private static final ProcessManager LINUX_PROCESS_MANAGER = new ProcessManager() {
        @Override
        public void kill(Process process) {
            if (C_LIBRARY != null) {
                final int pid = getPid(process);
                killTree(pid);
                kill(pid);
            } else {
                throw new IllegalStateException("Cannot kill process. Not unix system?");
            }
        }

        private int kill(int pid) {
            return C_LIBRARY.kill(pid, CLibrary.SIGKILL);
        }

        private void killTree(int pid) {
            final int[] children = getChildProcesses(pid);
            LOG.debug("My PID: {}, my child PIDs: {}", pid, children);
            if (children.length > 0) {
                for (int cpid : children) {
                    killTree(cpid); // kill process tree recursively
                }
            }
            int r = kill(pid); // kill origin process
            LOG.debug("kill {}", pid);
            if (r != 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("kill for {} returns {}, strerror '{}'", new Object[]{pid, r, C_LIBRARY.strerror(r)});
                }
            }
        }

        private int[] getChildProcesses(final int myPid) {
            final String ps = "ps -e -o ppid,pid,comm"; /* PPID, PID, COMMAND */
            final List<Integer> children = new ArrayList<>();
            final StringBuilder error = new StringBuilder();
            final LineConsumer stdout = new LineConsumer() {
                @Override
                public void writeLine(String line) throws IOException {
                    if (line != null && !line.isEmpty()) {
                        final String[] tokens = PS_TABLE_PATTERN.split(line.trim());
                        if (tokens.length == 3 /* PPID, PID, COMMAND */) {
                            int ppid;
                            try {
                                ppid = Integer.parseInt(tokens[0]);
                            } catch (NumberFormatException e) {
                                // May be first line from process table: 'PPID PID COMMAND'. Skip it.
                                return;
                            }
                            if (ppid == myPid) {
                                int pid = Integer.parseInt(tokens[1]);
                                children.add(pid);
                            }
                        }
                    }
                }

                @Override
                public void close() throws IOException {
                }
            };

            final LineConsumer stderr = new LineConsumer() {
                @Override
                public void writeLine(String line) throws IOException {
                    if (error.length() > 0) {
                        error.append('\n');
                    }
                    error.append(line);
                }

                @Override
                public void close() throws IOException {
                }
            };

            try {
                process(Runtime.getRuntime().exec(ps), stdout, stderr);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }

            if (error.length() > 0) {
                throw new IllegalStateException("cannot get child processes: " + error.toString());
            }
            final int size = children.size();
            final int[] result = new int[size];
            for (int i = 0; i < size; i++) {
                result[i] = children.get(i);
            }
            return result;
        }

        @Override
        public boolean isAlive(Process process) {
            return isAlive(getPid(process));
        }

        private boolean isAlive(int pid) {
            return new java.io.File("/proc/" + pid).exists();
        }

        @Override
        public int getPid(Process process) {
            try {
                Field f = process.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                return ((Number)f.get(process)).intValue();
            } catch (Exception e) {
                throw new IllegalStateException("Cannot get process pid. Not unix system?", e);
            }
        }
    };

    private static final ProcessManager DEFAULT_PROCESS_MANAGER = new ProcessManager() {

        @Override
        public void kill(Process process) {
            if (isAlive(process)) {
                process.destroy();
                try {
                    process.waitFor(); // wait for process death
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
        }

        @Override
        public boolean isAlive(Process process) {
            try {
                process.exitValue();
                return false;
            } catch (IllegalThreadStateException e) {
                return true;
            }
        }

        @Override
        public int getPid(Process process) {
            throw new UnsupportedOperationException();
        }
    };

    public static void process(Process p, LineConsumer stdout, LineConsumer stderr) throws IOException {
        BufferedReader inputReader = null;
        BufferedReader errorReader = null;
        try {
            final InputStream inputStream = p.getInputStream();
            final InputStream errorStream = p.getErrorStream();
            inputReader = new BufferedReader(new InputStreamReader(inputStream));
            errorReader = new BufferedReader(new InputStreamReader(errorStream));
            String line;
            while ((line = inputReader.readLine()) != null) {
                stdout.writeLine(line);
            }
            stdout.close();
            while ((line = errorReader.readLine()) != null) {
                stderr.writeLine(line);
            }
            stderr.close();
        } finally {
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException ignored) {
                }
            }
            if (errorReader != null) {
                try {
                    errorReader.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static boolean isAlive(Process process) {
        if (SystemInfo.isUnix()) {
            return LINUX_PROCESS_MANAGER.isAlive(process);
        }
        return DEFAULT_PROCESS_MANAGER.isAlive(process);
    }

    public static void kill(Process process) {
        if (SystemInfo.isUnix()) {
            LINUX_PROCESS_MANAGER.kill(process);
        }
        DEFAULT_PROCESS_MANAGER.kill(process);
    }

    private ProcessUtil() {
    }
}

