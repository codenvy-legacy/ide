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
package com.codenvy.ide.ext.java.jdi.server;

import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.event.EventQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author andrew00x */
final class EventsCollector implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(EventsCollector.class);
    private final    EventsHandler handler;
    private final    EventQueue    queue;
    private final    Thread        thread;
    private volatile boolean       running;

    EventsCollector(EventQueue queue, EventsHandler handler) {
        this.queue = queue;
        this.handler = handler;

        thread = new Thread(this);
        running = true;
        thread.start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                handler.handleEvents(queue.remove());
            } catch (DebuggerException e) {
                LOG.error(e.getMessage(), e);
            } catch (VMDisconnectedException e) {
                break;
            } catch (InterruptedException e) {
                // Thread interrupted with method stop().
                LOG.debug("EventsCollector terminated");
            }
        }
        LOG.debug("EventsCollector stopped");
    }

    void stop() {
        running = false;
        thread.interrupt();
    }
}
