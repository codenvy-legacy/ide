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
import com.codenvy.ide.ext.aws.shared.s3.S3Bucket;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for S3 Bucket list.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class S3BucketsUnmarshaller implements Unmarshallable<JsonArray<S3Bucket>> {
    private JsonArray<S3Bucket> s3Buckets;

    /**
     * Create unmarshaller.
     *
     * @param s3Buckets
     */
    public S3BucketsUnmarshaller(JsonArray<S3Bucket> s3Buckets) {
        this.s3Buckets = s3Buckets;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();

        if (text == null || text.isEmpty()) {
            return;
        }

        JSONArray jsonArray = JSONParser.parseStrict(text).isArray();

        if (jsonArray == null) {
            return;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.get(i).isObject();
            String value = (jsonObject.isObject() != null) ? jsonObject.isObject().toString() : "";

            DtoClientImpls.S3BucketImpl s3Bucket = DtoClientImpls.S3BucketImpl.deserialize(value);
            s3Buckets.add(s3Bucket);
        }
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<S3Bucket> getPayload() {
        return s3Buckets;
    }
}
