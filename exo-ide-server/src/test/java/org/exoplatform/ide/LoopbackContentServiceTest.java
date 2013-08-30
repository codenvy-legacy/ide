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
package org.exoplatform.ide;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.core.impl.MultivaluedMapImpl;
import org.everrest.test.mock.MockHttpServletRequest;
import org.exoplatform.services.security.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Test for UploadService class.
 * <p/>
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class LoopbackContentServiceTest extends BaseTest {


    @Before
    public void setUp() throws Exception {
        super.setUp();
        Authenticator authr = (Authenticator)container.getComponentInstanceOfType(Authenticator.class);
        String validUser =
                authr.validateUser(new Credential[]{new UsernameCredential("root"), new PasswordCredential("exo")});
        Identity id = authr.createIdentity(validUser);
        Set<String> roles = new HashSet<String>();
        roles.add("users");
        roles.add("administrators");
        id.setRoles(roles);
        ConversationState s = new ConversationState(id);
        ConversationState.setCurrent(s);
    }

    @Test
    public void uploadFile() throws Exception {
        MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
        headers.putSingle("content-type", "multipart/form-data; boundary=-----abcdef");
        EnvironmentContext ctx = new EnvironmentContext();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter w = new PrintWriter(out);

        String source =
                "-------abcdef\r\n" + "Content-Disposition: form-data; name=\"file\"; filename=\"test.txt\"\r\n"
                + "Content-Type: text/plain\r\n\r\ntest file content\r\n" + "-------abcdef\r\n"
                + "Content-Disposition: form-data; name=\"location\"\r\n\r\n"
                + "http://localhost/jcr/db1/dev-monit/test.txt\r\n" + "-------abcdef\r\n"
                + "Content-Disposition: form-data; name=\"mimeType\"\r\n\r\ntext/plain\r\n" + "-------abcdef\r\n"
                + "Content-Disposition: form-data; name=\"nodeType\"\r\n\r\n\r\n" + "-------abcdef\r\n"
                + "Content-Disposition: form-data; name=\"jcrContentNodeType\"\r\n\r\nnt:resource\r\n"
                + "-------abcdef--\r\n";

        w.write(source);
        w.flush();

        byte[] data = out.toByteArray();

        HttpServletRequest httpRequest =
                new MockHttpServletRequest("http://localhost/ide/loopbackcontent", new ByteArrayInputStream(data),
                                           data.length, "POST", headers);
        ctx.put(HttpServletRequest.class, httpRequest);

        ContainerResponse response =
                launcher.service("POST", "/ide/loopbackcontent", "http://localhost", headers, data, null, ctx);

        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue(response.getEntity() instanceof String);
        String text = (String)response.getEntity();
        Assert.assertEquals("<filecontent>test+file+content%0A</filecontent>", text);
    }

}
