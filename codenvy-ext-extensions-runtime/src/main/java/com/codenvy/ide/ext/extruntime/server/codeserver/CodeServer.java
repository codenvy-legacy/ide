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

/**
 * Interface represents a code servers.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CodeServer.java Jul 26, 2013 10:23:07 AM azatsarynnyy $
 */
public interface CodeServer {

    /**
     * Starts a new code server.
     * 
     * @param configuration code server configuration
     * @throws CodeServerException if any error has occurred while starting a code server
     */
    void start(CodeServerConfiguration configuration) throws CodeServerException;

    /**
     * Get code server's logs.
     * 
     * @return code server's logs
     * @throws CodeServerException if any error has occurred while getting code server logs
     */
    String getLogs() throws CodeServerException;

    /** Stop this code server. */
    void stop();

    /** Returns code server configuration. */
    CodeServerConfiguration getConfiguration();
}
