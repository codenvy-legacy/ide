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
package com.codenvy.ide.ext.extruntime.server.runner;

import java.io.IOException;

/**
 * Interface represents a GWT code server. Concrete implementations provide an implementation of methods
 * thereby controlling how the GWT code server will run, stop, get log files content.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: GWTCodeServer.java Jul 26, 2013 10:23:07 AM azatsarynnyy $
 */
public interface GWTCodeServer {

    /**
     * Starts GWT code server.
     *
     * @param configuration
     *         code server configuration
     * @throws RunnerException
     *         if any error has occurred while starting GWT code server
     */
    void start(GWTCodeServerConfiguration configuration) throws RunnerException;

    /**
     * Get GWT code server's logs.
     *
     * @return GWT code server's logs
     * @throws RunnerException
     *         if any error has occurred while getting GWT code server's logs
     * @throws IOException
     *         if any error occurred while retrieving logs
     */
    String getLogs() throws RunnerException, IOException;

    /** Stop GWT code server. */
    void stop();

    /** Returns GWT code server configuration. */
    GWTCodeServerConfiguration getConfiguration();
}
