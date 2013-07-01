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
package com.codenvy.ide.ext.aws.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.s3.S3ObjectsList;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class S3ObjectListUnmarshaller implements Unmarshallable<S3ObjectsList> {

    private DtoClientImpls.S3ObjectsListImpl objectsList;

    public S3ObjectListUnmarshaller(DtoClientImpls.S3ObjectsListImpl objectsList) {
        this.objectsList = objectsList;
    }

    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();

        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.S3ObjectsListImpl object = DtoClientImpls.S3ObjectsListImpl.deserialize(text);

        objectsList.setMaxKeys(object.getMaxKeys());
        objectsList.setNextMarker(object.getNextMarker());
        objectsList.setPrefix(object.getPrefix());
        objectsList.setS3Bucket(object.getS3Bucket());
        objectsList.setObjects(object.getObjects());
    }

    @Override
    public S3ObjectsList getPayload() {
        return objectsList;
    }
}
