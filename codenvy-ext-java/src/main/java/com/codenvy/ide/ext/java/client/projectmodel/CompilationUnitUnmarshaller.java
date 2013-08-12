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
 * Unmarshaller for {@link CompilationUnit}
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CompilationUnitUnmarshaller implements Unmarshallable<CompilationUnit> {

    private final CompilationUnit compilationUnit;

    /** @param compilationUnit */
    public CompilationUnitUnmarshaller(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        try {
            compilationUnit.init(JSONParser.parseLenient(response.getText()).isObject());
        } catch (Exception e) {
            throw new UnmarshallerException("Can't parse compilation unit.", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CompilationUnit getPayload() {
        return compilationUnit;
    }

}
