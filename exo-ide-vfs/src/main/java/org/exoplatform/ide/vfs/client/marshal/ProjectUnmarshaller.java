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
package org.exoplatform.ide.vfs.client.marshal;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/** @version $Id:$ */
public class ProjectUnmarshaller implements Unmarshallable<ProjectModel> {

    private final ProjectModel item;

    public ProjectUnmarshaller(ProjectModel item) {

        this.item = item;

    }

    /** @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response) */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        doUnmarshal(response.getText());
    }

    protected void doUnmarshal(String text) throws UnmarshallerException {
        try {
            item.init(JSONParser.parseLenient(text).isObject());
        } catch (Exception exc) {
            String message = "Can't parse item " + text;
            throw new UnmarshallerException(message);
        }
    }

    @Override
    public ProjectModel getPayload() {
        return this.item;
    }

}
