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

/**
 * Typically may be in use for windows systems only. For *nix like system UnixProcessManager is in use.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
class DefaultProcessManager extends ProcessManager {
    /*
    NOTE: some methods are not implemented for other system than unix like system.
     */

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
    public void kill(int pid) {
        throw new UnsupportedOperationException();
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
    public boolean isAlive(int pid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPid(Process process) {
        throw new UnsupportedOperationException();
    }
}
