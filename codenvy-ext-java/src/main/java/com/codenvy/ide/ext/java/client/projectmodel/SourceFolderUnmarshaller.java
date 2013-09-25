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
package com.codenvy.ide.ext.java.client.projectmodel;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class SourceFolderUnmarshaller implements Unmarshallable<SourceFolder> {
    private SourceFolder sourceFolder;

    private String projectPath;

    public SourceFolderUnmarshaller(SourceFolder sourceFolder, String projectPath) {
        this.sourceFolder = sourceFolder;
        this.projectPath = projectPath;
    }

    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            sourceFolder.init(JSONParser.parseLenient(response.getText()).isObject(), projectPath);
        } catch (Exception e) {
            throw new UnmarshallerException("Can't parse source folder", e);
        }
    }

    @Override
    public SourceFolder getPayload() {
        return sourceFolder;
    }
}
