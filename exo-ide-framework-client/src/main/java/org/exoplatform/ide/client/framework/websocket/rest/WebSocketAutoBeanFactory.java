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
package org.exoplatform.ide.client.framework.websocket.rest;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.client.framework.websocket.Message;

/**
 * The interface for the {@link AutoBean} generating.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: WebSocketAutoBeanFactory.java Jul 13, 2012 5:25:39 PM azatsarynnyy $
 */
public interface WebSocketAutoBeanFactory extends AutoBeanFactory {
    /**
     * A factory method for a {@link Message} bean.
     *
     * @return an {@link AutoBean} of type {@link Message}
     */
    AutoBean<Message> message();

    /**
     * A factory method for a {@link Pair} bean.
     *
     * @return an {@link AutoBean} of type {@link Pair}
     */
    AutoBean<Pair> pair();

    /**
     * A factory method for a {@link RequestMessage} bean.
     *
     * @return an {@link AutoBean} of type {@link RequestMessage}
     */
    AutoBean<RequestMessage> requestMessage();

    /**
     * A factory method for a {@link ResponseMessage} bean.
     *
     * @return an {@link AutoBean} of type {@link ResponseMessage}
     */
    AutoBean<ResponseMessage> responseMessage();
}
