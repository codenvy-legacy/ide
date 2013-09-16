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
package org.exoplatform.ide.extension.java.jdi.server;

import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.event.EventQueue;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
final class EventsCollector implements Runnable {
    private static final Log LOG = ExoLogger.getLogger(EventsCollector.class);

    private final EventsHandler handler;
    private final EventQueue    queue;

    private final    Thread  thread;
    private volatile boolean running;

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
