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
package com.codenvy.ide.ext.java.messages.impl;

import com.codenvy.ide.collections.Array;
import com.google.gwt.webworker.client.messages.MessageImpl;

/**
 * Message for Outline Update
 *
 * @author Evgen Vidolob
 */
public class OutlineUpdateMessage extends MessageImpl {
    protected OutlineUpdateMessage() {
    }

    public native final String getFileId() /*-{
        return this.fileId;
    }-*/;

    public native final OutlineUpdateMessage setFileId(String fileId) /*-{
        this.fileId = fileId;
        return this;
    }-*/;

    public native final Array<WorkerCodeBlock> getBlocks() /*-{
        return this.blocks;
    }-*/;

    public native final OutlineUpdateMessage setBlocks(Array<WorkerCodeBlock> blocks) /*-{
        this.blocks = blocks;
        return this;
    }-*/;

    public static native OutlineUpdateMessage make() /*-{
        return {
            _type : 8
        }
    }-*/;
}
