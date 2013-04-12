/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
