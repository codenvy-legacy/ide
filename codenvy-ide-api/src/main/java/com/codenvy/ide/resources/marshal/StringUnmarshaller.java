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
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;


/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: FileContentUnmarshaller Feb 3, 2011 9:42:13 AM evgen $
 */
public class StringUnmarshaller implements Unmarshallable<StringBuilder> {

    private final StringBuilder string;

    public StringUnmarshaller() {
        this.string = new StringBuilder();
    }

    @Override
    public StringBuilder getPayload() {
        return string;
    }

    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        string.append(response.getText());
    }

}
