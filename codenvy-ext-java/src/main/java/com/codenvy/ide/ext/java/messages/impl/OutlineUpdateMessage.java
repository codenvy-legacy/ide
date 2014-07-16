/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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

    public native final String getFilePath() /*-{
        return this.filePath;
    }-*/;

    public native final OutlineUpdateMessage setFilePath(String filePath) /*-{
        this.filePath = filePath;
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
