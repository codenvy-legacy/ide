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
package org.exoplatform.ide.git.client.marshaller;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.shared.MergeResult;
import org.exoplatform.ide.git.shared.MergeResult.MergeStatus;

/**
 * Unmarshaller for merge result in JSON format.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 20, 2011 12:01:03 PM anya $
 */
public class MergeUnmarshaller implements Unmarshallable<MergeResult>, Constants {
    /** Result of merge operation. */
    private Merge merge;

    /**
     * @param merge result of merge operation
     */
    public MergeUnmarshaller(Merge merge) {
        this.merge = merge;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            if (response.getText() == null || response.getText().isEmpty()) {
                return;
            }

            JSONObject jsonObject = JSONParser.parseStrict(response.getText()).isObject();

            if (jsonObject == null) {
                return;
            }

            if (jsonObject.containsKey(CONFLICTS) && jsonObject.get(CONFLICTS).isArray() != null) {
                JSONArray array = jsonObject.get(CONFLICTS).isArray();
                merge.setConflicts(getArray(array));
            }
            if (jsonObject.containsKey(MERGED_COMMITS) && jsonObject.get(MERGED_COMMITS).isArray() != null) {
                JSONArray array = jsonObject.get(MERGED_COMMITS).isArray();
                merge.setMergedCommits(getArray(array));
            }
            if (jsonObject.containsKey(MERGE_STATUS) && jsonObject.get(MERGE_STATUS).isString() != null) {
                merge.setMergeStatus(MergeStatus.valueOf(jsonObject.get(MERGE_STATUS).isString().stringValue()));
            }
            if (jsonObject.containsKey(NEW_HEAD) && jsonObject.get(NEW_HEAD).isString() != null) {
                merge.setNewHead(jsonObject.get(NEW_HEAD).isString().stringValue());
            }
        } catch (Exception e) {
            throw new UnmarshallerException(GitExtension.MESSAGES.mergeUnmarshallerFailed());
        }
    }

    /**
     * Get array from JSON array.
     * 
     * @param jsonArray JSON array
     * @return array of {@link String}
     */
    private String[] getArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.size() == 0) {
            return null;
        }
        String[] array = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            array[i] = jsonArray.get(i).isString().stringValue();
        }
        return array;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload() */
    @Override
    public Merge getPayload() {
        return merge;
    }
}
