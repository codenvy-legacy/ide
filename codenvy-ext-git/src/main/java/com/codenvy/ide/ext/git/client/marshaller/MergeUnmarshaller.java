/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package com.codenvy.ide.ext.git.client.marshaller;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.MergeResult;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Unmarshaller for merge result in JSON format.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 20, 2011 12:01:03 PM anya $
 */
public class MergeUnmarshaller implements Unmarshallable<MergeResult>, Constants {
    /** Result of merge operation. */
    private Merge                   merge;
    private GitLocalizationConstant constant;

    /**
     * @param merge
     *         result of merge operation
     */
    public MergeUnmarshaller(@NotNull Merge merge, @NotNull GitLocalizationConstant constant) {
        this.merge = merge;
        this.constant = constant;
    }

    /** {@inheritDoc} */
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
                merge.setConflicts(getJsonArray(array));
            }
            if (jsonObject.containsKey(MERGED_COMMITS) && jsonObject.get(MERGED_COMMITS).isArray() != null) {
                JSONArray array = jsonObject.get(MERGED_COMMITS).isArray();
                merge.setMergedCommits(getJsonArray(array));
            }
            if (jsonObject.containsKey(MERGE_STATUS) && jsonObject.get(MERGE_STATUS).isString() != null) {
                merge.setMergeStatus(MergeResult.MergeStatus.valueOf(jsonObject.get(MERGE_STATUS).isString().stringValue()));
            }
            if (jsonObject.containsKey(NEW_HEAD) && jsonObject.get(NEW_HEAD).isString() != null) {
                merge.setNewHead(jsonObject.get(NEW_HEAD).isString().stringValue());
            }
        } catch (Exception e) {
            throw new UnmarshallerException(constant.mergeUnmarshallerFailed(), e);
        }
    }

    /**
     * Get array from JSON array.
     *
     * @param jsonArray
     *         JSON array
     * @return array of {@link String}
     */
    @Nullable
    private JsonArray<String> getJsonArray(@NotNull JSONArray jsonArray) {
        if (jsonArray.size() == 0) {
            return null;
        }
        JsonArray<String> array = JsonCollections.createArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            String value = jsonArray.get(i).isString().stringValue();
            array.add(value);
        }
        return array;
    }

    /** {@inheritDoc} */
    @Override
    public Merge getPayload() {
        return merge;
    }
}