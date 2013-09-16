/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extruntime.server;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class that manages available/bound ports for running new Tomcat servers and GWT code servers.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: PortManager.java Aug 7, 2013 2:50:55 PM azatsarynnyy $
 */
class PortManager {

    private enum Service {
        CODE_SERVER("GWT code server"),
        CATALINA_SHUTDOWN("Catalina shutdown"),
        CATALINA_HTTP("Catalina HTTP connector"),
        CATALINA_AJP("Catalina AJP connector");
        String name;

        Service(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /** Map GWT code server's port number to its availability status: true - port is bound, false - port is free. */
    private Map<Integer, Boolean> codeServerPortRanges;
    /** Map Catalina shutdown port number to its availability status: true - port is bound, false - port is free. */
    private Map<Integer, Boolean> catalinaShutdownPortRanges;
    /**
     * Map Catalina HTTP-connector port number to its availability status: true - port is bound,
     * false - port is free.
     */
    private Map<Integer, Boolean> httpConnectorPortRanges;
    /**
     * Map Catalina APJ-connector port number to its availability status: true - port is bound,
     * false - port is free.
     */
    private Map<Integer, Boolean> ajpConnectorPortRanges;

    PortManager(List<Integer> codeServerPortRanges,
                List<Integer> catalinaShutdownPortRanges,
                List<Integer> httpConnectorPortRanges,
                List<Integer> ajpConnectorPortRanges) {
        this.codeServerPortRanges = new ConcurrentHashMap<>(codeServerPortRanges.size());
        this.catalinaShutdownPortRanges = new ConcurrentHashMap<>(catalinaShutdownPortRanges.size());
        this.httpConnectorPortRanges = new ConcurrentHashMap<>(httpConnectorPortRanges.size());
        this.ajpConnectorPortRanges = new ConcurrentHashMap<>(ajpConnectorPortRanges.size());

        initPortMap(this.codeServerPortRanges, codeServerPortRanges);
        initPortMap(this.catalinaShutdownPortRanges, catalinaShutdownPortRanges);
        initPortMap(this.httpConnectorPortRanges, httpConnectorPortRanges);
        initPortMap(this.ajpConnectorPortRanges, ajpConnectorPortRanges);
    }

    private void initPortMap(Map<Integer, Boolean> map, List<Integer> availablePortRangesList) {
        for (Integer portNumber : availablePortRangesList) {
            map.put(portNumber, false);
        }
    }

    int nextCodeServerPort() {
        return nextPort(Service.CODE_SERVER);
    }

    int nextShutdownPort() {
        return nextPort(Service.CATALINA_SHUTDOWN);
    }

    int nextHttpPort() {
        return nextPort(Service.CATALINA_HTTP);
    }

    int nextAjpPort() {
        return nextPort(Service.CATALINA_AJP);
    }

    void releaseCodeServerPort(int portNumber) {
        releasePort(portNumber, Service.CODE_SERVER);
    }

    void releaseShutdownPort(int portNumber) {
        releasePort(portNumber, Service.CATALINA_SHUTDOWN);
    }

    void releaseHttpPort(int portNumber) {
        releasePort(portNumber, Service.CATALINA_HTTP);
    }

    void releaseAjpPort(int portNumber) {
        releasePort(portNumber, Service.CATALINA_AJP);
    }

    /** Marks all the specified port numbers as available for binding. */
    void releasePorts(int codeServerPort, int catalinaShutdownPort, int httpConnectorPort, int ajpConnectorPort) {
        releaseCodeServerPort(codeServerPort);
        releaseShutdownPort(catalinaShutdownPort);
        releaseHttpPort(httpConnectorPort);
        releaseAjpPort(ajpConnectorPort);
    }

    /**
     * Returns the next port number that is available to bind to the specified <code>service</code> and marks returned
     * port number as already bound. May return -1 if no port is available.
     */
    private int nextPort(Service service) {
        Set<Entry<Integer, Boolean>> entries = null;
        switch (service) {
            case CODE_SERVER:
                entries = codeServerPortRanges.entrySet();
                break;
            case CATALINA_SHUTDOWN:
                entries = catalinaShutdownPortRanges.entrySet();
                break;
            case CATALINA_HTTP:
                entries = httpConnectorPortRanges.entrySet();
                break;
            case CATALINA_AJP:
                entries = ajpConnectorPortRanges.entrySet();
                break;
            default:
                throw new IllegalArgumentException("Unknown service to get port for binding: " + service);
        }

        for (Entry<Integer, Boolean> entry : entries) {
            if (entry.getValue() == false) {
                entry.setValue(true);
                return entry.getKey();
            }
        }

        return -1;
    }

    /** Marks the specified port number as available for binding for the specified service. */
    private void releasePort(int portNumber, Service service) {
        switch (service) {
            case CODE_SERVER:
                codeServerPortRanges.put(portNumber, false);
                break;
            case CATALINA_SHUTDOWN:
                catalinaShutdownPortRanges.put(portNumber, false);
                break;
            case CATALINA_HTTP:
                httpConnectorPortRanges.put(portNumber, false);
                break;
            case CATALINA_AJP:
                ajpConnectorPortRanges.put(portNumber, false);
                break;
            default:
                throw new IllegalArgumentException("Unknown service to release bound port: " + service);
        }
    }
}
