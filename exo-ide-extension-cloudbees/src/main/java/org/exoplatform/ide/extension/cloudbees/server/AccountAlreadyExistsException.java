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
package org.exoplatform.ide.extension.cloudbees.server;

import org.exoplatform.ide.extension.cloudbees.shared.CloudBeesAccount;

/**
 * Thrown if account with the same name already registered.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see CloudBees#createAccount(org.exoplatform.ide.extension.cloudbees.shared.CloudBeesAccount)
 * @see CloudBees#createAccount(String, org.exoplatform.ide.extension.cloudbees.shared.CloudBeesAccount)
 */
@SuppressWarnings("serial")
public final class AccountAlreadyExistsException extends Exception {
    public AccountAlreadyExistsException(CloudBeesAccount account) {
        super("Account " + account.getName() + " already registered. ");
    }
}
