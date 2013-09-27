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
package org.exoplatform.ide.client.framework.editor;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 4:10:07 PM Apr 2, 2012 evgen $
 */
public class AddCodeFormatterEvent extends GwtEvent<AddCodeFormatterHandler> {

    public static final GwtEvent.Type<AddCodeFormatterHandler> TYPE = new Type<AddCodeFormatterHandler>();

    private CodeFormatter formatter;

    private final String mimeType;

    /** @param formatter */
    public AddCodeFormatterEvent(CodeFormatter formatter, String mimeType) {
        super();
        this.formatter = formatter;
        this.mimeType = mimeType;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<AddCodeFormatterHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(AddCodeFormatterHandler handler) {
        handler.onAddCodeFormatter(this);
    }

    /** @return the formatter */
    public CodeFormatter getFormatter() {
        return formatter;
    }

    /** @return the mimeType */
    public String getMimeType() {
        return mimeType;
    }

}
