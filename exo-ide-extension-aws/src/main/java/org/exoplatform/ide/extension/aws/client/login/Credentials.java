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
package org.exoplatform.ide.extension.aws.client.login;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 14, 2012 11:52:17 AM anya $
 */
public interface Credentials {
    /** @return the access_key */
    public String getAccess_key();

    /**
     * @param access_key
     *         the access_key to set
     */
    public void setAccess_key(String access_key);

    /** @return the secret_key */
    public String getSecret_key();

    /**
     * @param secret_key
     *         the secret_key to set
     */
    public void setSecret_key(String secret_key);
}
