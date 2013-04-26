/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.java.client.projectmodel;

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
