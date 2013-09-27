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

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;

/**
 * Deserializer for the body of the {@link ResponseMessage}.
 * <p/>
 * By the contract:
 * <code>getPayload()</code> should never return <code>null</code> (should be initialized in impl's constructor
 * and return the same object (with different content) before and after <code>unmarshal()</code>.
 *
 * @param <T>
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: Unmarshallable.java Nov 9, 2012 10:25:33 AM azatsarynnyy $
 */
public interface Unmarshallable<T> {

    /**
     * Prepares an object from the incoming {@link ResponseMessage}.
     *
     * @param resopnse
     *         {@link ResponseMessage}
     */
    void unmarshal(ResponseMessage response) throws UnmarshallerException;

    /**
     * The content of the returned object normally differs before and
     * after <code>unmarshall()</code> but by the contract it should never be <code>null</code>.
     *
     * @return an object deserialized from the {@link ResponseMessage}
     */
    T getPayload();

}
