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
import com.codenvy.ide.ext.aws.shared.s3.NewS3Object;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for new S3 Object.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class NewS3ObjectUnmarshaller implements Unmarshallable<NewS3Object> {
    private DtoClientImpls.NewS3ObjectImpl newS3Object;

    /**
     * Create unmarshaller.
     *
     * @param newS3Object
     */
    public NewS3ObjectUnmarshaller(DtoClientImpls.NewS3ObjectImpl newS3Object) {
        this.newS3Object = newS3Object;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();

        if (text == null || text.isEmpty()) {
            return;
        }

        JSONObject jsonObject = JSONParser.parseStrict(text).isObject();
        if (jsonObject == null) {
            return;
        }

        DtoClientImpls.NewS3ObjectImpl newS3ObjectDto = DtoClientImpls.NewS3ObjectImpl.deserialize(text);
        this.newS3Object.setS3Bucket(newS3ObjectDto.getS3Bucket());
        this.newS3Object.setS3Key(newS3ObjectDto.getS3Key());
        this.newS3Object.setVersionId(newS3ObjectDto.getVersionId());

    }

    /** {@inheritDoc} */
    @Override
    public NewS3Object getPayload() {
        return newS3Object;
    }
}
