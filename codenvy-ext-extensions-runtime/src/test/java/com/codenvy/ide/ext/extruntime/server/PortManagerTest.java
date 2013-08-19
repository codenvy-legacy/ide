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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: PortManagerTest.java Aug 18, 2013 10:46:48 PM azatsarynnyy $
 */
public class PortManagerTest {
    private static PortManager   pm;
    private static List<Integer> codeServerPortRanges;
    private static List<Integer> catalinaShutdownPortRanges;
    private static List<Integer> httpConnectorPortRanges;
    private static List<Integer> ajpConnectorPortRanges;

    @BeforeClass
    public static void init() {
        codeServerPortRanges = new ArrayList<Integer>();
        codeServerPortRanges.add(9001);
        codeServerPortRanges.add(9002);

        catalinaShutdownPortRanges = new ArrayList<Integer>();
        catalinaShutdownPortRanges.add(9011);
        catalinaShutdownPortRanges.add(9012);

        httpConnectorPortRanges = new ArrayList<Integer>();
        httpConnectorPortRanges.add(9021);
        httpConnectorPortRanges.add(9022);

        ajpConnectorPortRanges = new ArrayList<Integer>();
        ajpConnectorPortRanges.add(9031);
        ajpConnectorPortRanges.add(9032);
    }

    @Before
    public void setUp() {
        pm = new PortManager(codeServerPortRanges, catalinaShutdownPortRanges, httpConnectorPortRanges, ajpConnectorPortRanges);
    }

    @Test
    public void testNextCodeServerPort() {
        List<Integer> bindedPorts = new ArrayList<>(codeServerPortRanges.size());
        for (int i = 0; i < codeServerPortRanges.size(); i++) {
            int port = pm.nextCodeServerPort();
            assertTrue("Unknown port is binded.", codeServerPortRanges.contains(port));
            assertFalse("The same port is binded twice.", bindedPorts.contains(port));
            bindedPorts.add(port);
        }
        assertEquals(-1, pm.nextCodeServerPort());
    }

    @Test
    public void testNextShutdownPort() {
        List<Integer> bindedPorts = new ArrayList<>(catalinaShutdownPortRanges.size());
        for (int i = 0; i < catalinaShutdownPortRanges.size(); i++) {
            int port = pm.nextShutdownPort();
            assertTrue("Unknown port is binded.", catalinaShutdownPortRanges.contains(port));
            assertFalse("The same port is binded twice.", bindedPorts.contains(port));
            bindedPorts.add(port);
        }
        assertEquals(-1, pm.nextShutdownPort());
    }

    @Test
    public void testNextHttpPort() {
        List<Integer> bindedPorts = new ArrayList<>(httpConnectorPortRanges.size());
        for (int i = 0; i < httpConnectorPortRanges.size(); i++) {
            int port = pm.nextHttpPort();
            assertTrue("Unknown port is binded.", httpConnectorPortRanges.contains(port));
            assertFalse("The same port is binded twice.", bindedPorts.contains(port));
            bindedPorts.add(port);
        }
        assertEquals(-1, pm.nextHttpPort());
    }

    @Test
    public void testNextAjpPort() {
        List<Integer> bindedPorts = new ArrayList<>(ajpConnectorPortRanges.size());
        for (int i = 0; i < ajpConnectorPortRanges.size(); i++) {
            int port = pm.nextAjpPort();
            assertTrue("Unknown port is binded.", ajpConnectorPortRanges.contains(port));
            assertFalse("The same port is binded twice.", bindedPorts.contains(port));
            bindedPorts.add(port);
        }
        assertEquals(-1, pm.nextAjpPort());
    }

    @Test
    public void testReleaseCodeServerPort() {
        bindAllCodeServerPorts();
        pm.releaseCodeServerPort(codeServerPortRanges.get(0));
        assertEquals("Port wasn't released properly.", (int)codeServerPortRanges.get(0), pm.nextCodeServerPort());
    }

    @Test
    public void testReleaseShutdownPort() {
        bindAllShutdownPorts();
        pm.releaseShutdownPort(catalinaShutdownPortRanges.get(0));
        assertEquals("Port wasn't released properly.", (int)catalinaShutdownPortRanges.get(0), pm.nextShutdownPort());
    }

    @Test
    public void testReleaseHttpPort() {
        bindAllHttpPorts();
        pm.releaseHttpPort(httpConnectorPortRanges.get(0));
        assertEquals("Port wasn't released properly.", (int)httpConnectorPortRanges.get(0), pm.nextHttpPort());
    }

    @Test
    public void testReleaseAjpPort() {
        bindAllAjpPorts();
        pm.releaseAjpPort(ajpConnectorPortRanges.get(0));
        assertEquals("Port wasn't released properly.", (int)ajpConnectorPortRanges.get(0), pm.nextAjpPort());
    }

    private static void bindAllCodeServerPorts() {
        for (int i = 0; i < codeServerPortRanges.size(); i++) {
            pm.nextCodeServerPort();
        }
    }

    private static void bindAllShutdownPorts() {
        for (int i = 0; i < catalinaShutdownPortRanges.size(); i++) {
            pm.nextShutdownPort();
        }
    }

    private static void bindAllHttpPorts() {
        for (int i = 0; i < httpConnectorPortRanges.size(); i++) {
            pm.nextHttpPort();
        }
    }

    private static void bindAllAjpPorts() {
        for (int i = 0; i < ajpConnectorPortRanges.size(); i++) {
            pm.nextAjpPort();
        }
    }
}
