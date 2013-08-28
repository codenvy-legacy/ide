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
package org.exoplatform.ide.client.framework.output.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class OutputEvent extends GwtEvent<OutputHandler> {

    public static final GwtEvent.Type<OutputHandler> TYPE = new GwtEvent.Type<OutputHandler>();

    private String message;

    private OutputMessage.Type outputType;

    public OutputEvent(String message) {
        this.message = message;
        outputType = OutputMessage.Type.INFO;
    }

    public OutputEvent(String message, OutputMessage.Type outputType) {
        this.message = message;
        this.outputType = outputType;
    }

    @Override
    protected void dispatch(OutputHandler handler) {
        handler.onOutput(this);
    }

    /** @return the message */
    public String getMessage() {
        return message;
    }

    /** @return the outputType */
    public OutputMessage.Type getOutputType() {
        return outputType;
    }

    @Override
    public Type<OutputHandler> getAssociatedType() {
        return TYPE;
    }

}
