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
 * Class that manages available/binded ports for run new code servers.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CodeServerPortManager.java Aug 7, 2013 2:50:55 PM azatsarynnyy $
 */
class CodeServerPortManager {
    private List<Integer> availablePorts;

    private List<Integer> bindedPorts;

    CodeServerPortManager(List<Integer> availablePorts) {
        this.availablePorts = availablePorts;

        this.bindedPorts = new ArrayList<Integer>();
    }

    int nextPort() {
        for (int portNumber : availablePorts) {
            if (!bindedPorts.contains(portNumber)) {
                bindedPorts.add(portNumber);
                return portNumber;
            }
        }
        return -1;
    }

    void releasePort(int port) {
        bindedPorts.remove((Integer)port);
    }
}
