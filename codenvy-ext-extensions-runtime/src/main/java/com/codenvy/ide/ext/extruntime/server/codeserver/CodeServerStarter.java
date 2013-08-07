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
package com.codenvy.ide.ext.extruntime.server.codeserver;

import java.nio.file.Path;

/**
 * Interface represents a code servers starter. Class that implements this interface should provide an implementation of starting of a code
 * server.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CodeServerStarter.java Jul 26, 2013 10:23:07 AM azatsarynnyy $
 */
public interface CodeServerStarter {
    /**
     * Starts a new code server.
     * 
     * @param workingDirectory working directory of a code server
     * @return a new code server that started
     * @throws CodeServerException if any error has occurred while starting a code server
     */
    CodeServer start(Path workingDirectory) throws CodeServerException;

    /** Interface represents a code server. */
    public interface CodeServer {
        /**
         * Get code server's logs.
         * 
         * @return code server's logs
         * @throws CodeServerException if any error has occurred while getting code server logs
         */
        String getLogs() throws CodeServerException;

        /** Stop a code server. */
        void stop();
    }
}
