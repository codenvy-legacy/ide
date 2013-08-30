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
package org.exoplatform.ide.client.operation.cutcopy;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class CutItemsEvent extends GwtEvent<CutItemsHandler> {

    public static final GwtEvent.Type<CutItemsHandler> TYPE = new GwtEvent.Type<CutItemsHandler>();

    @Override
    protected void dispatch(CutItemsHandler handler) {
        handler.onCutItems(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<CutItemsHandler> getAssociatedType() {
        return TYPE;
    }

}
