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
package org.exoplatform.ide.extension.gadget.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.gadget.shared.Gadget;
import org.exoplatform.ide.extension.gadget.shared.GadgetMetadata;
import org.exoplatform.ide.extension.gadget.shared.TokenRequest;
import org.exoplatform.ide.extension.gadget.shared.TokenResponse;

/**
 * The interface for the AutoBean generator.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: GadgetAutoBeanFactory.java Mar 20, 2012 4:31:48 PM azatsarynnyy $
 */
public interface GadgetAutoBeanFactory extends AutoBeanFactory {
    /**
     * A factory method for a token of request bean.
     *
     * @return an {@link AutoBean} of type {@link TokenRequest}
     */
    AutoBean<TokenRequest> tokenRequest();

    /**
     * A factory method for a token of response bean.
     *
     * @return an {@link AutoBean} of type {@link TokenResponse}
     */
    AutoBean<TokenResponse> tokenResponse();

    /**
     * A factory method for a OpenSocial gadget bean.
     *
     * @return an {@link AutoBean} of type {@link Gadget}
     */
    AutoBean<Gadget> gadget();

    /**
     * A factory method for a metadata of OpenSocial gadget bean.
     *
     * @return an {@link AutoBean} of type {@link GadgetMetadata}
     */
    AutoBean<GadgetMetadata> gadgetMetadata();
}
