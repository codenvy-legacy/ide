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