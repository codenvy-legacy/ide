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
package com.codenvy.ide.ext.java.jdi.client.marshaller;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.ext.java.jdi.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.java.jdi.shared.StackFrameDump;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for stack frame dump.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class StackFrameDumpUnmarshaller implements Unmarshallable<StackFrameDump> {
    private DtoClientImpls.StackFrameDumpImpl stackFrameDump;

    /**
     * Create unmarshaller.
     *
     * @param stackFrameDump
     */
    public StackFrameDumpUnmarshaller(@NotNull DtoClientImpls.StackFrameDumpImpl stackFrameDump) {
        this.stackFrameDump = stackFrameDump;
    }

    /** {@inheritDoc} */
    @Override
    public void unmarshal(Response response) throws UnmarshallerException {
        String text = response.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        DtoClientImpls.StackFrameDumpImpl stackFrameDump = DtoClientImpls.StackFrameDumpImpl.deserialize(text);
        this.stackFrameDump.setFields(stackFrameDump.getFields());
        this.stackFrameDump.setLocalVariables(stackFrameDump.getLocalVariables());
    }

    /** {@inheritDoc} */
    @Override
    public StackFrameDump getPayload() {
        return stackFrameDump;
    }
}