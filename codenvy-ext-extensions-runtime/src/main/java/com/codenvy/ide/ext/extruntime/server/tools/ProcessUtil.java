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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Helpers to manage system processes.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public final class ProcessUtil {
    private static final ProcessManager PROCESS_MANAGER = ProcessManager.newInstance();

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
        return PROCESS_MANAGER.isAlive(process);
    }

    public static boolean isAlive(int pid) {
        return PROCESS_MANAGER.isAlive(pid);
    }

    public static void kill(Process process) {
        PROCESS_MANAGER.kill(process);
    }

    public static void kill(int pid) {
        PROCESS_MANAGER.kill(pid);
    }

    public int getPid(Process process) {
        return PROCESS_MANAGER.getPid(process);
    }

    private ProcessUtil() {
    }
}

