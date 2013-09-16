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
package org.exoplatform.ide.extension.java.shared;

import java.util.Map;

/**
 * Interface describe maven response.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: MavenResponse.java Mar 25, 2012 11:51:16 PM azatsarynnyy $
 */
public interface MavenResponse {

    /**
     * Returns exit code.
     *
     * @return exit code
     */
    public int getExitCode();

    /**
     * Set exit code.
     *
     * @param exitCode
     *         exit code
     */
    public void setExitCode(int exitCode);

    /**
     * Returns the output message.
     *
     * @return the outout message
     */
    public String getOutput();

    /**
     * Change the output message.
     *
     * @param output
     *         the output message
     */
    public void setOutput(String output);

    /**
     * Returns the result.
     *
     * @return the result
     */
    public Map<String, String> getResult();

    /**
     * Change the result.
     *
     * @param result
     *         the result
     */
    public void setResult(Map<String, String> result);
}
