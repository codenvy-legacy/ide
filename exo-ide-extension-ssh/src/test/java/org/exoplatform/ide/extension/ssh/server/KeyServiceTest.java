package org.exoplatform.ide.extension.ssh.server;

import org.everrest.assured.EverrestJetty;
import org.everrest.assured.JettyHttpServer;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.ide.extension.ssh.shared.GenKeyRequest;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;
import org.exoplatform.ide.extension.ssh.shared.PublicKey;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** Tests for {@link org.exoplatform.ide.extension.ssh.server.KeyService} */
@Listeners(value = {EverrestJetty.class, MockitoTestNGListener.class})
public class KeyServiceTest {

    @Mock
    private SshKeyStore keyStore;

    @Mock
    private GenKeyRequest genKeyRequest;

    @InjectMocks
    private KeyService keyService;

    @Test
    public void shouldGenerateKeyPairForHost() throws Exception {
        when(genKeyRequest.getHost()).thenReturn("host.com");
        when(genKeyRequest.getComment()).thenReturn("comment");
        when(genKeyRequest.getPassphrase()).thenReturn("pass");

        Map<String, String> body = new HashMap<>();
        body.put("host", "host.com");
        body.put("comment", "comment");
        body.put("passphrase", "pass");

        given().auth().basic(JettyHttpServer.ADMIN_USER_NAME, JettyHttpServer.ADMIN_USER_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON).body(body)
                .pathParam("ws-name", "dev-monit")
                .expect().statusCode(200)
                .when().post("/private/{ws-name}/ssh-keys/gen");
    }

    @Test
    public void shouldThrowExceptionWhenGenerateKeyPair() throws Exception {
        doThrow(new SshKeyStoreException("message")).when(keyStore).genKeyPair(anyString(), anyString(), anyString());

        Map<String, String> body = new HashMap<>();

        given().auth().basic(JettyHttpServer.ADMIN_USER_NAME, JettyHttpServer.ADMIN_USER_PASSWORD)
                .contentType(MediaType.APPLICATION_JSON).body(body)
                .pathParam("ws-name", "dev-monit")
                .expect().statusCode(500).body(equalTo("message"))
                .when().post("/private/{ws-name}/ssh-keys/gen");
    }

    @Test
    public void shouldUploadPrivateKey() throws Exception {
        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource("www_example_com.key").toURI());
        given().auth().basic(JettyHttpServer.ADMIN_USER_NAME, JettyHttpServer.ADMIN_USER_PASSWORD)
                .multiPart(path.toFile())
                .pathParam("ws-name", "dev-monit")
                .expect().statusCode(200).contentType(equalTo(MediaType.TEXT_HTML))
                .when().post("/private/{ws-name}/ssh-keys/add");
    }

    @Test
    public void shouldThrowExceptionFileNotFoundWhenUploadPrivateKey() throws Exception {
        given().auth().basic(JettyHttpServer.ADMIN_USER_NAME, JettyHttpServer.ADMIN_USER_PASSWORD)
                .multiPart("param", "nonexistvalue")
                .pathParam("ws-name", "dev-monit")
                .expect().statusCode(400).contentType(equalTo(MediaType.TEXT_HTML)).body(equalTo("Can't find input file."))
                .when().post("/private/{ws-name}/ssh-keys/add");
    }

    @Test
    public void shouldThrowExceptionSshStoreWhenUploadPrivateKey() throws Exception {
        doThrow(new SshKeyStoreException("message")).when(keyStore).addPrivateKey(anyString(), any(byte[].class));

        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource("www_example_com.key").toURI());

        given().auth().basic(JettyHttpServer.ADMIN_USER_NAME, JettyHttpServer.ADMIN_USER_PASSWORD)
                .multiPart(path.toFile())
                .pathParam("ws-name", "dev-monit")
                .expect().statusCode(500).contentType(equalTo(MediaType.TEXT_HTML)).body(equalTo("message"))
                .when().post("/private/{ws-name}/ssh-keys/add");
    }

    @Test
    public void shouldThrowExceptionFileToLargeWhenUploadPrivateKey() throws Exception {
        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource("www_example_com.key.invalid").toURI());

        given().auth().basic(JettyHttpServer.ADMIN_USER_NAME, JettyHttpServer.ADMIN_USER_PASSWORD)
                .multiPart(path.toFile())
                .pathParam("ws-name", "dev-monit")
                .expect().statusCode(400).contentType(equalTo(MediaType.TEXT_HTML)).body(equalTo("File is to large to proceed."))
                .when().post("/private/{ws-name}/ssh-keys/add");
    }

    @Test
    public void shouldGetPublicKey() throws Exception {
        when(keyStore.getPublicKey(anyString())).thenReturn(new SshKey("host.com", "key content".getBytes()));

        PublicKey key = given().auth().basic(JettyHttpServer.ADMIN_USER_NAME, JettyHttpServer.ADMIN_USER_PASSWORD)
                .pathParam("ws-name", "dev-monit").queryParam("host", "host.com").header(HTTPHeader.ACCEPT, MediaType.APPLICATION_JSON)
                .expect().statusCode(200).contentType(equalTo(MediaType.APPLICATION_JSON))
                .when().get("/private/{ws-name}/ssh-keys").as(PublicKey.class);

        assertEquals(key.getKey(), "key content");
        assertEquals(key.getHost(), "host.com");
    }

    @Test
    public void shouldThrowExceptionWhenGetPublicKeyFailed() throws Exception {
        when(keyStore.getPublicKey(anyString())).thenReturn(null);

        given().auth().basic(JettyHttpServer.ADMIN_USER_NAME, JettyHttpServer.ADMIN_USER_PASSWORD)
                .pathParam("ws-name", "dev-monit").queryParam("host", "host.com").header(HTTPHeader.ACCEPT, MediaType.APPLICATION_JSON)
                .expect()
                .statusCode(404).contentType(equalTo(MediaType.TEXT_PLAIN)).body(equalTo("Public key for host host.com not found."))
                .when().get("/private/{ws-name}/ssh-keys");
    }

    @Test
    public void shouldThrowInternalExceptionWhenGetPublicKeyFailed() throws Exception {
        when(keyStore.getPublicKey(anyString())).thenThrow(new SshKeyStoreException("message"));

        given().auth().basic(JettyHttpServer.ADMIN_USER_NAME, JettyHttpServer.ADMIN_USER_PASSWORD)
                .pathParam("ws-name", "dev-monit").queryParam("host", "host.com").header(HTTPHeader.ACCEPT, MediaType.APPLICATION_JSON)
                .expect()
                .statusCode(500).contentType(equalTo(MediaType.TEXT_PLAIN)).body(equalTo("message"))
                .when().get("/private/{ws-name}/ssh-keys");
    }

    @Test
    public void shouldRemoveKey() throws Exception {
        doNothing().when(keyStore).removeKeys(anyString());

        given().auth().basic(JettyHttpServer.ADMIN_USER_NAME, JettyHttpServer.ADMIN_USER_PASSWORD)
                .pathParam("ws-name", "dev-monit")
                .queryParam("host", "host.com")
                .queryParam("callback", "test")
                .expect()
                .statusCode(200).body(equalTo("test();"))
                .when().get("/private/{ws-name}/ssh-keys/remove");
    }

    @Test
    public void shouldThrowExceptionWhenRemoveKey() throws Exception {
        doThrow(new SshKeyStoreException("message")).when(keyStore).removeKeys(anyString());

        given().auth().basic(JettyHttpServer.ADMIN_USER_NAME, JettyHttpServer.ADMIN_USER_PASSWORD)
                .pathParam("ws-name", "dev-monit")
                .queryParam("host", "host.com")
                .queryParam("callback", "test")
                .expect()
                .statusCode(500).body(equalTo("message"))
                .when().get("/private/{ws-name}/ssh-keys/remove");
    }

    @Test
    public void shouldGetAllKeys() throws Exception {
        when(keyStore.getAll()).thenReturn(Collections.<String>emptySet());

        given().auth().basic(JettyHttpServer.ADMIN_USER_NAME, JettyHttpServer.ADMIN_USER_PASSWORD)
                .pathParam("ws-name", "dev-monit")
                .header(HTTPHeader.ACCEPT, MediaType.APPLICATION_JSON)
                .expect()
                .statusCode(200).body(equalTo("[]"))
                .when().get("/private/{ws-name}/ssh-keys/all");
    }

    @Test
    public void shouldGetKey() throws Exception {
        when(keyStore.getAll()).thenReturn(Collections.singleton("host.com"));
        when(keyStore.getPublicKey(anyString())).thenReturn(new SshKey(null, null));

        KeyItem[] keys = given().auth().basic(JettyHttpServer.ADMIN_USER_NAME, JettyHttpServer.ADMIN_USER_PASSWORD)
                .pathParam("ws-name", "dev-monit")
                .header(HTTPHeader.ACCEPT, MediaType.APPLICATION_JSON)
                .expect()
                .statusCode(200)
                .when().get("/private/{ws-name}/ssh-keys/all").as(KeyItem[].class);

        assertEquals(keys.length, 1);
        assertEquals(keys[0].getHost(), "host.com");
        assertTrue(keys[0].getPublicKeyURL().endsWith("/private/dev-monit/ssh-keys?host=host.com"));
        assertTrue(keys[0].getRemoveKeyURL().endsWith("/private/dev-monit/ssh-keys/remove?host=host.com"));
    }

    @Test
    public void shouldThrowExceptionWhenGetKey() throws Exception {
        doThrow(new SshKeyStoreException("message")).when(keyStore).getAll();

        given().auth().basic(JettyHttpServer.ADMIN_USER_NAME, JettyHttpServer.ADMIN_USER_PASSWORD)
                .pathParam("ws-name", "dev-monit")
                .header(HTTPHeader.ACCEPT, MediaType.APPLICATION_JSON)
                .expect().statusCode(500).contentType(equalTo(MediaType.TEXT_PLAIN)).body(equalTo("message"))
                .when().get("/private/{ws-name}/ssh-keys/all");
    }
}
