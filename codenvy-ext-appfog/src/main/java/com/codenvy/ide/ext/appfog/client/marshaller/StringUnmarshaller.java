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
package com.codenvy.ide.ext.appfog.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for String object.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
public class StringUnmarshaller implements Unmarshallable<StringBuilder> {
    protected StringBuilder builder;

    /**
     * Create unmarshaller.
     *
     * @param builder
     */
    public StringUnmarshaller(StringBuilder builder) {
        super();
        this.builder = builder;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        builder.append(response.getText());
    }

    /** {@inheritDoc} */
    @Override
    public StringBuilder getPayload() {
        return builder;
    }
}