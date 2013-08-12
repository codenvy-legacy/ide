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
package com.codenvy.ide.resources.marshal;

import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;


/**
 * Unmarshaller for {@link File} content.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class FileContentUnmarshaller implements Unmarshallable<File> {

    private final File file;

    public FileContentUnmarshaller(File file) {
        this.file = file;
    }

    @Override
    public File getPayload() {
        return file;
    }

    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        file.setContent(response.getText());
    }

}
