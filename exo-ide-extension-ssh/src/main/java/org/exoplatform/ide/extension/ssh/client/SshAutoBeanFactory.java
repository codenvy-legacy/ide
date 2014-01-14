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
package org.exoplatform.ide.extension.ssh.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.ssh.shared.GenKeyRequest;
import org.exoplatform.ide.extension.ssh.shared.ListKeyItem;
import org.exoplatform.ide.extension.ssh.shared.PublicKey;

/**
 * The interface for the AutoBean generator.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: SshAutoBeanFactory.java Mar 13, 2012 2:38:22 PM azatsarynnyy $
 */
public interface SshAutoBeanFactory extends AutoBeanFactory {
    /**
     * A factory method for a request SSH-key bean.
     *
     * @return an {@link AutoBean} of type {@link GenKeyRequest}
     */
    AutoBean<GenKeyRequest> genKeyRequest();

    /**
     * A factory method for a keys list.
     *
     * @return an {@link AutoBean} of type {@link ListKeyItem}
     */
    AutoBean<ListKeyItem> keyItems();

    /**
     * A factory method for a public key.
     *
     * @return an {@link AutoBean} of type {@link PublicKey}
     */
    AutoBean<PublicKey> publicKey();
}
