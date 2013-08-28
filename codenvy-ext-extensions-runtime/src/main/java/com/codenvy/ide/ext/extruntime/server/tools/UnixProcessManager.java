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

import com.sun.jna.Library;
import com.sun.jna.Native;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Process manager for *nix like system.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
class UnixProcessManager extends ProcessManager {
    /*
    At the moment tested on linux only.
     */

    private static final Logger LOG = LoggerFactory.getLogger(UnixProcessManager.class);

    private static final CLibrary C_LIBRARY;

    static {
        CLibrary tmp = null;
        if (SystemInfo.isUnix()) {
            try {
                tmp = ((CLibrary)Native.loadLibrary("c", CLibrary.class));
            } catch (Exception e) {
                LOG.error("Cannot load native library", e);
            }
        }
        C_LIBRARY = tmp;
    }

    private static interface CLibrary extends Library {
        // kill -l
        int SIGKILL = 9;
        int SIGTERM = 15;

        int kill(int pid, int signal);

        String strerror(int errno);

        /*int system(String cmd);*/
    }

    private static final Pattern UNIX_PS_TABLE_PATTERN = Pattern.compile("\\s+");

    @Override
    public void kill(Process process) {
        if (C_LIBRARY != null) {
            killTree(getPid(process));
        } else {
            throw new IllegalStateException("Cannot kill process. Not unix system?");
        }
    }

    @Override
    public void kill(int pid) {
        killTree(pid);
    }

    private void killTree(int pid) {
        final int[] children = getChildProcesses(pid);
        LOG.debug("PID: {}, child PIDs: {}", pid, children);
        if (children.length > 0) {
            for (int cpid : children) {
                killTree(cpid); // kill process tree recursively
            }
        }
        int r = C_LIBRARY.kill(pid, CLibrary.SIGKILL); // kill origin process
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
                    final String[] tokens = UNIX_PS_TABLE_PATTERN.split(line.trim());
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
            ProcessUtil.process(Runtime.getRuntime().exec(ps), stdout, stderr);
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

    @Override
    public boolean isAlive(int pid) {
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
}
