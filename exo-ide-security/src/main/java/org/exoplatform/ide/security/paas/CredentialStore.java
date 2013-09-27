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
package org.exoplatform.ide.security.paas;

/**
 * Abstraction of store for user PaaS credential.
 *
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id: CredentialStore.java Mar 1, 2013 vetal $
 */
public interface CredentialStore {
    /**
     * Load user credential for specified target.
     *
     * @param user
     *         user identifier
     * @param target
     *         credential's target, e.g. PaaS name
     * @param credential
     *         instance of Credential to store attributes, see {@link Credential#setAttribute(String, String)}
     * @return <code>true</code> if credential has been found in this store and <code>false</code> if credential not
     *         found in store
     * @throws CredentialStoreException
     */
    boolean load(String user, String target, Credential credential) throws CredentialStoreException;

    /**
     * Save user credential for specified target.
     *
     * @param user
     *         user identifier
     * @param target
     *         credential's target, e.g. PaaS name
     * @param credential
     *         credentials
     * @throws CredentialStoreException
     */
    void save(String user, String target, Credential credential) throws CredentialStoreException;

    /**
     * Delete user credential for specified target.
     *
     * @param user
     *         user identifier
     * @param target
     *         credential's target, e.g. PaaS name
     * @return <code>true</code> if credential successfully removed from this store and <code>false</code> if credential
     *         not found in store
     * @throws CredentialStoreException
     */
    boolean delete(String user, String target) throws CredentialStoreException;
}