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
package org.exoplatform.ide.client.edit.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowLineNumbersEvent extends GwtEvent<ShowLineNumbersHandler> {

    public static final GwtEvent.Type<ShowLineNumbersHandler> TYPE = new GwtEvent.Type<ShowLineNumbersHandler>();

    private boolean showLineNumber;

    public ShowLineNumbersEvent(boolean showLineNumber) {
        this.showLineNumber = showLineNumber;
    }

    @Override
    protected void dispatch(ShowLineNumbersHandler handler) {
        handler.onShowLineNumbers(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowLineNumbersHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean isShowLineNumber() {
        return showLineNumber;
    }

}
