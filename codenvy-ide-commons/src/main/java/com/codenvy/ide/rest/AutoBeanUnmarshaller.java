/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.rest;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.google.gwt.http.client.Response;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;


/** @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a> */
public class AutoBeanUnmarshaller<T> implements Unmarshallable<T> {
    private AutoBean<T> bean;

    /**
     * Create unmarshaller.
     *
     * @param autoBean
     */
    public AutoBeanUnmarshaller(AutoBean<T> autoBean) {
        this.bean = autoBean;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        if (response.getStatusCode() != 204 && response.getText() != null) {
            Splittable data = StringQuoter.split(response.getText());
            AutoBeanCodex.decodeInto(data, bean);
        }
    }

    public T getPayload() {
        return bean.as();
    }
}