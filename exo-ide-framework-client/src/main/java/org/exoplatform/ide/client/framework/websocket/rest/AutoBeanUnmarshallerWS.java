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
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;

/**
 * @param <T>
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: AutoBeanUnmarshallerWS.java Nov 19, 2012 11:44:19 AM azatsarynnyy $
 */
public class AutoBeanUnmarshallerWS<T> implements Unmarshallable<T> {
    private AutoBean<T> bean;

    public AutoBeanUnmarshallerWS(AutoBean<T> autoBean) {
        this.bean = autoBean;
    }

    /** @see org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable#unmarshal(org.exoplatform.ide.client.framework.websocket
     * .rest.ResponseMessage) */
    @Override
    public void unmarshal(ResponseMessage response) throws UnmarshallerException {
        if (response.getResponseCode() != HTTPStatus.NO_CONTENT && response.getBody() != null) {
            Splittable data = StringQuoter.split(response.getBody());
            AutoBeanCodex.decodeInto(data, bean);
        }
    }

    /** @see org.exoplatform.ide.client.framework.websocket.rest.Unmarshallable#getPayload() */
    @Override
    public T getPayload() {
        return bean.as();
    }
}
