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

import java.util.ArrayList;
import java.util.List;

/**
 * Class that manages available/binded ports for run new Tomcat servers.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: TomcatPortManager.java Aug 7, 2013 2:50:55 PM azatsarynnyy $
 */
class TomcatPortManager {
    private List<Integer> catalinaShutdownPortRanges;
    private List<Integer> httpConnectorPortRanges;
    private List<Integer> ajpConnectorPortRanges;

    private List<Integer> bindedCatalinaShutdownPortRanges;
    private List<Integer> bindedHttpConnectorPortRanges;
    private List<Integer> bindedAjpConnectorPortRanges;

    TomcatPortManager(List<Integer> catalinaShutdownPortRanges, List<Integer> httpConnectorPortRanges, List<Integer> ajpConnectorPortRanges) {
        this.catalinaShutdownPortRanges = catalinaShutdownPortRanges;
        this.httpConnectorPortRanges = httpConnectorPortRanges;
        this.ajpConnectorPortRanges = ajpConnectorPortRanges;

        this.bindedCatalinaShutdownPortRanges = new ArrayList<Integer>();
        this.bindedHttpConnectorPortRanges = new ArrayList<Integer>();
        this.bindedAjpConnectorPortRanges = new ArrayList<Integer>();
    }

    int nextShutdownPort() {
        for (int portNumber : catalinaShutdownPortRanges) {
            if (!bindedCatalinaShutdownPortRanges.contains(portNumber)) {
                bindedCatalinaShutdownPortRanges.add(portNumber);
                return portNumber;
            }
        }
        return -1;
    }

    int nextHttpPort() {
        for (int portNumber : httpConnectorPortRanges) {
            if (!bindedHttpConnectorPortRanges.contains(portNumber)) {
                bindedHttpConnectorPortRanges.add(portNumber);
                return portNumber;
            }
        }
        return -1;
    }

    int nextAjpPort() {
        for (int portNumber : ajpConnectorPortRanges) {
            if (!bindedAjpConnectorPortRanges.contains(portNumber)) {
                bindedAjpConnectorPortRanges.add(portNumber);
                return portNumber;
            }
        }
        return -1;
    }

    void releasePorts(int catalinaShutdownPort, int httpConnectorPort, int ajpConnectorPort) {
        bindedCatalinaShutdownPortRanges.remove((Integer)catalinaShutdownPort);
        bindedHttpConnectorPortRanges.remove((Integer)httpConnectorPort);
        bindedAjpConnectorPortRanges.remove((Integer)ajpConnectorPort);
    }
}
