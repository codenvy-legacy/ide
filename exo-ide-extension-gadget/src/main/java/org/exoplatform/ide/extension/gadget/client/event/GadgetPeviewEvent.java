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
package org.exoplatform.ide.extension.gadget.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class GadgetPeviewEvent extends GwtEvent<GadgetPeviewHandler> {

    public static final Type<GadgetPeviewHandler> TYPE = new Type<GadgetPeviewHandler>();

    private String url;

    public GadgetPeviewEvent(String url) {
        this.url = url;
    }

    @Override
    protected void dispatch(GadgetPeviewHandler handler) {
        handler.onGadgetPreview(this);
    }

    @Override
    public Type<GadgetPeviewHandler> getAssociatedType() {
        return TYPE;
    }

    public String getUrl() {
        return url;
    }

}
