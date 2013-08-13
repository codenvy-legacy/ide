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
package com.codenvy.ide.ext.aws.client.marshaller;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.s3.S3ObjectsList;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for S3 Object list.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class S3ObjectListUnmarshaller implements Unmarshallable<S3ObjectsList> {
    private DtoClientImpls.S3ObjectsListImpl objectsList;

    /**
     * Create unmarshaller.
     *
     * @param objectsList
     */
    public S3ObjectListUnmarshaller(DtoClientImpls.S3ObjectsListImpl objectsList) {
        this.objectsList = objectsList;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public S3ObjectsList getPayload() {
        return objectsList;
    }
}
