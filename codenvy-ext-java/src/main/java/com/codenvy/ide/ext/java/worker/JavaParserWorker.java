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
package com.codenvy.ide.ext.java.worker;

import com.google.gwt.webworker.client.DedicatedWorkerEntryPoint;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaParserWorker extends DedicatedWorkerEntryPoint {

    private WorkerMessageHandler messageHandler = new WorkerMessageHandler(this);

    @Override
    public void onWorkerLoad() {
        setOnMessage(messageHandler);
    }

    public void sendMessage(String message) {
        postMessage(message);
    }
}
