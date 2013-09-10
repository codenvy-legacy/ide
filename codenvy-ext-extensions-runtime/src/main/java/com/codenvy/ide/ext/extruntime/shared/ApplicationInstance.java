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
package com.codenvy.ide.ext.extruntime.shared;

import com.codenvy.ide.dto.DTO;

/**
 * Interface represents an launched Codenvy application with custom extension.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInstance.java Jul 31, 2013 4:21:09 PM azatsarynnyy $
 */
@DTO
public interface ApplicationInstance {
    /** Returns application id. */
    String getId();

    /** Returns the domain name or IP-address of host on which application was started. */
    String getHost();

    /** Returns the port number of host on which application was started. */
    int getPort();

    /** Returns the domain name or IP-address of host on which code server was started. */
    String getCodeServerHost();

    /** Returns the port number of host on which code server was started. */
    int getCodeServerPort();
}
