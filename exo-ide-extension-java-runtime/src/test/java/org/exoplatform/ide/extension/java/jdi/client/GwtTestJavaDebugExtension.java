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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.ide.extension.java.jdi.shared.*;
import org.junit.Test;

/**
 * GWT JUnit <b>integration</b> tests must extend GWTTestCase.
 * Using <code>"GwtTest*"</code> naming pattern exclude them from running with
 * surefire during the test phase.
 * <p/>
 * If you run the tests using the Maven command line, you will have to
 * navigate with your browser to a specific url given by Maven.
 * See http://mojo.codehaus.org/gwt-maven-plugin/user-guide/testing.html
 * for details.
 */
public class GwtTestJavaDebugExtension extends GWTTestCase {


    @Override
    public String getModuleName() {
        return "org.exoplatform.ide.extension.java.jdi.DebuggerExtensionJUnit";
    }

    private DebuggerAutoBeanFactory factory;

    private String eventsTxt =
            "{\"events\":[{\"location\":{\"className\":\"HelloWorld\",\"lineNumber\":13},\"type\":2},{\"type\":1," +
            "\"breakPoint\":{\"location\":{\"className\":\"HelloWorld\",\"lineNumber\":13},\"enabled\":true}}]}";

    @Test
    public void testUnmarshallerPrepareBean() throws Exception {
        AutoBean<DebuggerEventList> ab = factory.debuggerEventList();
        DebuggerEventListUnmarshaller unmarshaller = new DebuggerEventListUnmarshaller(ab.as());
        MockResponse response = new MockResponse(eventsTxt);
        unmarshaller.unmarshal(response);
        assertEquals(2, ab.as().getEvents().size());
        assertTrue(ab.as().getEvents().get(0) instanceof StepEvent);
        StepEvent stepEvent = (StepEvent)ab.as().getEvents().get(0);
        Location location = stepEvent.getLocation();
        assertNotNull(location);
        assertEquals("HelloWorld", location.getClassName());
        assertEquals(13, location.getLineNumber());
        assertTrue(ab.as().getEvents().get(1) instanceof BreakPointEvent);

        BreakPointEvent breakPointEvent = (BreakPointEvent)ab.as().getEvents().get(1);
        BreakPoint breakPoint = breakPointEvent.getBreakPoint();
        assertNotNull(breakPoint);
        assertTrue(breakPoint.isEnabled());
        location = breakPoint.getLocation();
        assertEquals("HelloWorld", location.getClassName());
        assertEquals(13, location.getLineNumber());
    }

    @Override
    protected void gwtSetUp() throws Exception {
        factory = GWT.create(DebuggerAutoBeanFactory.class);
    }

    public class MockResponse extends Response {

        int status;

        String text;

        /**
         * @param status
         * @param text
         */
        public MockResponse(String text) {
            this.status = 200;
            this.text = text;
        }

        @Override
        public String getHeader(String arg0) {
            return null;
        }

        @Override
        public Header[] getHeaders() {
            return null;
        }

        @Override
        public String getHeadersAsString() {
            return null;
        }

        @Override
        public int getStatusCode() {
            return status;
        }

        @Override
        public String getStatusText() {
            return null;
        }

        @Override
        public String getText() {
            return text;
        }

    }


}
