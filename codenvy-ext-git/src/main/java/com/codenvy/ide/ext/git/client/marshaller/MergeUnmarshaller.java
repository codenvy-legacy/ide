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
package com.codenvy.ide.ext.git.client.marshaller;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.MergeResult;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for merge result in JSON format.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 20, 2011 12:01:03 PM anya $
 */
public class MergeUnmarshaller implements Unmarshallable<MergeResult> {
    /** Result of merge operation. */
    private DtoClientImpls.MergeResultImpl merge;

    /**
     * @param merge
     *         result of merge operation
     */
    public MergeUnmarshaller(@NotNull DtoClientImpls.MergeResultImpl merge) {
        this.merge = merge;
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

        String s = jsonObject.toString();
        DtoClientImpls.MergeResultImpl mergeResult = DtoClientImpls.MergeResultImpl.deserialize(s);
        if (mergeResult.hasConflicts()) {
            merge.setConflicts(mergeResult.getConflicts());
        }
        if (mergeResult.hasFailed()) {
            merge.setFailed(mergeResult.getFailed());
        }
        merge.setMergedCommits(mergeResult.getMergedCommits());
        merge.setMergeStatus(mergeResult.getMergeStatus());
        merge.setNewHead(mergeResult.getNewHead());
    }

    /** {@inheritDoc} */
    @Override
    public MergeResult getPayload() {
        return merge;
    }
}