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
package org.exoplatform.ide.extension.heroku.server;

import com.codenvy.ide.commons.server.ContainerUtils;
import com.codenvy.ide.commons.server.ParsingResponseException;

import org.eclipse.jgit.transport.URIish;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.extension.heroku.shared.HerokuKey;
import org.exoplatform.ide.extension.heroku.shared.Stack;
import org.exoplatform.ide.extension.ssh.server.SshKey;
import org.exoplatform.ide.extension.ssh.server.SshKeyStore;
import org.exoplatform.ide.extension.ssh.server.SshKeyStoreException;
import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.*;
import org.exoplatform.ide.security.paas.Credential;
import org.exoplatform.ide.security.paas.CredentialStore;
import org.exoplatform.ide.security.paas.CredentialStoreException;
import org.exoplatform.services.security.ConversationState;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.net.ssl.*;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.*;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Heroku {
    /** Base URL of heroku REST API. */
    public static final String HEROKU_API = "https://api.heroku.com";

    private static final String HEROKU_GIT_REMOTE = "heroku";

    private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    }};

    private final HerokuAuthenticator authenticator;
    private final CredentialStore     credentialStore;
    private final SshKeyStore         sshKeyStore;
    private final String              demoLogin;
    private final String              demoPass;

    public Heroku(HerokuAuthenticator authenticator, CredentialStore credentialStore, SshKeyStore sshKeyStore, InitParams initParams) {
        this.authenticator = authenticator;
        this.credentialStore = credentialStore;
        this.sshKeyStore = sshKeyStore;
        this.demoLogin = ContainerUtils.readValueParam(initParams, "demoLogin");
        this.demoPass = ContainerUtils.readValueParam(initParams, "demoPass");
    }

    /**
     * Log in with specified email/password. Result of command execution is saved 'heroku API key', see
     * {@link HerokuAuthenticator#login(String, String, Credential)} for details.
     *
     * @param email
     *         email address that used when create account at heroku.com
     * @param password
     *         password
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     * @throws IOException
     *         id any i/o errors occurs
     */
    public void login(String email, String password)
            throws HerokuException, ParsingResponseException, IOException, CredentialStoreException {
        final Credential credential = new Credential();
        final String userId = getUserId();
        credentialStore.load(userId, "heroku", credential);
        authenticator.login(email.equals("demo") ? demoLogin : email, password.equals("demo") ? demoPass : password, credential);
        credentialStore.save(userId, "heroku", credential);
    }

    /** Remove locally saved authentication credentials. */
    public void logout() throws CredentialStoreException {
        final Credential credential = new Credential();
        final String userId = getUserId();
        credentialStore.load(userId, "heroku", credential);
        credential.removeAttribute("email");
        credential.removeAttribute("api_key");
        credentialStore.save(userId, "heroku", credential);
    }

    private String getUserId() {
        return ConversationState.getCurrent().getIdentity().getUserId();
    }

    /**
     * Add SSH key for current user. Upload SSH key to heroku.com. {@link SshKeyStore} must have registered public key
     * for host' heroku.com', see method {@link SshKeyStore#getPublicKey(String)}
     *
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     * @throws IOException
     *         id any i/o errors occurs
     */
    public void addSshKey() throws HerokuException, IOException, SshKeyStoreException, CredentialStoreException {
        addSshKey(getCredential());
    }

    private void addSshKey(HerokuCredential herokuCredential) throws HerokuException, IOException,
                                                                     SshKeyStoreException {
        final String host = "heroku.com";
        SshKey publicKey = sshKeyStore.getPublicKey(host);
        if (publicKey == null) {
            sshKeyStore.genKeyPair(host, null, null);
            publicKey = sshKeyStore.getPublicKey(host);
        }
        HttpURLConnection http = null;
        try {
            URL url = new URL(HEROKU_API + "/user/keys");
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            authenticate(herokuCredential, http);
            http.setRequestProperty("Accept", "application/xml, */*");
            http.setDoOutput(true);
            http.setRequestProperty("Content-type", "text/ssh-authkey");
            OutputStream output = http.getOutputStream();
            try {
                output.write(publicKey.getBytes());
                output.flush();
            } finally {
                output.close();
            }

            if (http.getResponseCode() != 200) {
                throw fault(http);
            }
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Remove SSH key for current user.
     *
     * @param keyName
     *         key name to remove typically in form 'user@host'. NOTE: If <code>null</code> then all keys for current user
     *         removed
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     * @throws IOException
     *         id any i/o errors occurs
     */
    public void removeSshKey(String keyName) throws HerokuException, IOException, CredentialStoreException {
        removeSshKey(getCredential(), keyName);
    }

    private void removeSshKey(HerokuCredential herokuCredential, String keyName) throws HerokuException, IOException {
        HttpURLConnection http = null;
        try {
            URL url = new URL(Heroku.HEROKU_API + ((keyName != null) //
                                                   ? ("/user/keys/" + URLEncoder.encode(keyName, "utf-8")) //
                                                   : "/user/keys")); // The same as keyClear
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("DELETE");
            http.setRequestProperty("Accept", "application/xml, */*");
            authenticate(herokuCredential, http);

            if (http.getResponseCode() != 200) {
                throw fault(http);
            }
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Get SSH keys for current user.
     *
     * @param inLongFormat
     *         if <code>true</code> then display info about each key in long format. In other words full content of
     *         public key provided. By default public key displayed in truncated form
     * @return List with all SSH keys for current user
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     * @throws IOException
     *         id any i/o errors occurs
     */
    public List<HerokuKey> listSshKeys(boolean inLongFormat)
            throws HerokuException, ParsingResponseException, IOException, CredentialStoreException {
        return listSshKeys(getCredential(), inLongFormat);
    }

    private List<HerokuKey> listSshKeys(HerokuCredential herokuCredential, boolean inLongFormat)
            throws HerokuException, IOException, ParsingResponseException {
        HttpURLConnection http = null;
        try {
            URL url = new URL(HEROKU_API + "/user/keys");
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("Accept", "application/xml, */*");
            authenticate(herokuCredential, http);

            if (http.getResponseCode() != 200) {
                throw fault(http);
            }

            InputStream input = http.getInputStream();
            Document xmlDoc;
            try {
                xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
            } finally {
                input.close();
            }

            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList keyNodes = (NodeList)xPath.evaluate("/keys/key", xmlDoc, XPathConstants.NODESET);
            int keyLength = keyNodes.getLength();
            List<HerokuKey> keys = new ArrayList<HerokuKey>(keyLength);
            for (int i = 0; i < keyLength; i++) {
                Node n = keyNodes.item(i);
                String email = (String)xPath.evaluate("email", n, XPathConstants.STRING);
                String contents = (String)xPath.evaluate("contents", n, XPathConstants.STRING);
                if (!inLongFormat) {
                    contents = formatKey(contents);
                }
                keys.add(new HerokuKey(email, contents));
            }
            return keys;
        } catch (ParserConfigurationException pce) {
            throw new ParsingResponseException(pce.getMessage(), pce);
        } catch (SAXException sae) {
            throw new ParsingResponseException(sae.getMessage(), sae);
        } catch (XPathExpressionException xpe) {
            throw new ParsingResponseException(xpe.getMessage(), xpe);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    private String formatKey(String source) {
        String[] parts = source.split(" ");
        StringBuilder key = new StringBuilder();
        key.append(parts[0]) //
                .append(' ') //
                .append(parts[1].substring(0, 10)) //
                .append('.') //
                .append('.') //
                .append('.') //
                .append(parts[1].substring(parts[1].length() - 10, parts[1].length()));
        if (parts.length > 2) {
            key.append(' ') //
                    .append(parts[2]);
        }
        return key.toString();
    }

    /**
     * Remove all SSH keys for current user.
     *
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     * @throws IOException
     *         id any i/o errors occurs
     */
    public void removeAllSshKeys() throws HerokuException, IOException, CredentialStoreException {
        removeAllSshKeys(getCredential());
    }

    private void removeAllSshKeys(HerokuCredential herokuCredential) throws HerokuException, IOException {
        HttpURLConnection http = null;
        try {
            URL url = new URL(HEROKU_API + "/user/keys");
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("DELETE");
            http.setRequestProperty("Accept", "application/xml, */*");
            authenticate(herokuCredential, http);

            if (http.getResponseCode() != 200) {
                throw fault(http);
            }
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Create new application.
     *
     * @param name
     *         application name. If <code>null</code> then application got random name
     * @param remote
     *         git remote name, default 'heroku'
     * @param workDir
     *         git working directory. May be <code>null</code> if command executed out of git repository. If
     *         <code>workDir</code> exists and is git repository folder then remote configuration added
     * @return Map with information about newly created application. Minimal set of application attributes:
     *         <ul>
     *         <li>Name</li>
     *         <li>Git URL of repository</li>
     *         <li>HTTP URL of application</li>
     *         </ul>
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     * @throws IOException
     *         id any i/o errors occurs
     */
    public Map<String, String> createApplication(String name, String remote, File workDir)
            throws HerokuException, ParsingResponseException, IOException, CredentialStoreException {
        return createApplication(getCredential(), name, remote, workDir);
    }

    private Map<String, String> createApplication(HerokuCredential herokuCredential, String name, String remote,
                                                  File workDir) throws HerokuException, ParsingResponseException, IOException {
        if (remote == null || remote.isEmpty()) {
            remote = HEROKU_GIT_REMOTE;
        }
        HttpURLConnection http = null;
        try {
            URL url = new URL(HEROKU_API + "/apps");
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Accept", "application/xml");
            authenticate(herokuCredential, http);
            if (name != null) {
                http.setDoOutput(true);
                OutputStream output = http.getOutputStream();
                try {
                    output.write(("app[name]=" + name).getBytes());
                    output.flush();
                } finally {
                    output.close();
                }
            }

            int status = http.getResponseCode();
            if (status < 200 || status > 202) {
                throw fault(http);
            }

            InputStream input = http.getInputStream();
            Document xmlDoc;
            try {
                xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
            } finally {
                input.close();
            }

            XPath xPath = XPathFactory.newInstance().newXPath();

            name = (String)xPath.evaluate("/app/name", xmlDoc, XPathConstants.STRING);
            String gitUrl = (String)xPath.evaluate("/app/git-url", xmlDoc, XPathConstants.STRING);
            String webUrl = (String)xPath.evaluate("/app/web-url", xmlDoc, XPathConstants.STRING);

            if (workDir != null) {
                GitConnection git = null;
                try {
                    git = GitConnectionFactory.getInstance().getConnection(workDir, null);
                    if (!new File(workDir, ".git").exists()) {
                        git.init(new InitRequest());
                    }
                    git.remoteAdd(new RemoteAddRequest(remote, gitUrl));
                } finally {
                    if (git != null) {
                        git.close();
                    }
                }
            }

            Map<String, String> info = new HashMap<String, String>(3);
            info.put("name", name);
            info.put("gitUrl", gitUrl);
            info.put("webUrl", webUrl);

            return info;
        } catch (ParserConfigurationException pce) {
            throw new ParsingResponseException(pce.getMessage(), pce);
        } catch (SAXException sae) {
            throw new ParsingResponseException(sae.getMessage(), sae);
        } catch (XPathExpressionException xpe) {
            throw new ParsingResponseException(xpe.getMessage(), xpe);
        } catch (GitException ge) {
            throw new RuntimeException(ge.getMessage(), ge);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Permanently destroy an application.
     *
     * @param name
     *         application name to destroy. If <code>null</code> then try to determine application name from git configuration.
     *         To be able determine application name <code>workDir</code> must not be <code>null</code> at least. If name not
     *         specified and cannot be determined IllegalStateException thrown
     * @param workDir
     *         git working directory. May be <code>null</code> if command executed out of git repository in this case
     *         <code>name</code> parameter must be not <code>null</code>
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     * @throws IOException
     *         id any i/o errors occurs
     */
    public void destroyApplication(String name, File workDir)
            throws HerokuException, IOException, CredentialStoreException {
        destroyApplication(getCredential(), name, workDir);
    }

    private void destroyApplication(HerokuCredential herokuCredential, String name, File workDir) throws IOException,
                                                                                                         HerokuException {
        if (name == null || name.isEmpty()) {
            name = detectAppName(workDir);
        }

        HttpURLConnection http = null;
        try {
            URL url = new URL(HEROKU_API + "/apps/" + name);
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("DELETE");
            http.setRequestProperty("Accept", "application/xml, */*");
            authenticate(herokuCredential, http);

            if (http.getResponseCode() != 200) {
                throw fault(http);
            }
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Provide detailed information about application.
     *
     * @param name
     *         application name to get information. If <code>null</code> then try to determine application name from git
     *         configuration. To be able determine application name <code>workDir</code> must not be <code>null</code> at least.
     *         If name not specified and cannot be determined IllegalStateException thrown
     * @param inRawFormat
     *         if <code>true</code> then get result as raw Map. If <code>false</code> (default) result is Map that
     *         contains predefined set of key-value pair
     * @param workDir
     *         git working directory. May be <code>null</code> if command executed out of git repository in this case
     *         <code>name</code> parameter must be not <code>null</code>
     * @return result of execution of depends to <code>inRawFormat</code> parameter. If <code>inRawFormat</code> is
     *         <code>false</code> (default) then method returns with predefined set of attributes otherwise method returns raw Map
     *         that contains all attributes
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     * @throws IOException
     *         id any i/o errors occurs
     */
    public Map<String, String> applicationInfo(String name, boolean inRawFormat, File workDir)
            throws HerokuException, ParsingResponseException, IOException, CredentialStoreException {
        return applicationInfo(getCredential(), name, inRawFormat, workDir);
    }

    private Map<String, String> applicationInfo(HerokuCredential herokuCredential, String name, boolean inRawFormat,
                                                File workDir) throws HerokuException, ParsingResponseException, IOException {
        if (name == null || name.isEmpty()) {
            name = detectAppName(workDir);
        }

        HttpURLConnection http = null;
        try {
            URL url = new URL(HEROKU_API + "/apps/" + name);
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("Accept", "application/xml, */*");
            authenticate(herokuCredential, http);

            if (http.getResponseCode() != 200) {
                throw fault(http);
            }

            InputStream input = http.getInputStream();
            Document xmlDoc;
            try {
                xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
            } finally {
                input.close();
            }

            // TODO : Add 'addons', 'collaborators' to be conform to ruby implementation.
            XPath xPath = XPathFactory.newInstance().newXPath();

            Map<String, String> info = new HashMap<String, String>();

            if (!inRawFormat) {
                info.put("name", (String)xPath.evaluate("/app/name", xmlDoc, XPathConstants.STRING));
                info.put("webUrl", (String)xPath.evaluate("/app/web-url", xmlDoc, XPathConstants.STRING));
                info.put("domainName", (String)xPath.evaluate("/app/domain-name", xmlDoc, XPathConstants.STRING));
                info.put("gitUrl", (String)xPath.evaluate("/app/git-url", xmlDoc, XPathConstants.STRING));
                info.put("dynos", (String)xPath.evaluate("/app/dynos", xmlDoc, XPathConstants.STRING));
                info.put("workers", (String)xPath.evaluate("/app/workers", xmlDoc, XPathConstants.STRING));
                info.put("repoSize", (String)xPath.evaluate("/app/repo-size", xmlDoc, XPathConstants.STRING));
                info.put("slugSize", (String)xPath.evaluate("/app/slug-size", xmlDoc, XPathConstants.STRING));
                info.put("stack", (String)xPath.evaluate("/app/stack", xmlDoc, XPathConstants.STRING));
                info.put("owner", (String)xPath.evaluate("/app/owner", xmlDoc, XPathConstants.STRING));
                info.put("databaseSize", (String)xPath.evaluate("/app/database_size", xmlDoc, XPathConstants.STRING));
                return info;
            } else {
                NodeList appNodes = (NodeList)xPath.evaluate("/app/*", xmlDoc, XPathConstants.NODESET);
                int appLength = appNodes.getLength();
                for (int i = 0; i < appLength; i++) {
                    Node item = appNodes.item(i);
                    if (!item.getNodeName().equals("dyno_hours")) {
                        info.put(item.getNodeName(), item.getTextContent());
                    }
                }
                return info;
            }
        } catch (ParserConfigurationException pce) {
            throw new ParsingResponseException(pce.getMessage(), pce);
        } catch (SAXException sae) {
            throw new ParsingResponseException(sae.getMessage(), sae);
        } catch (XPathExpressionException xpe) {
            throw new ParsingResponseException(xpe.getMessage(), xpe);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Rename application.
     *
     * @param name
     *         current application name. If <code>null</code> then try to determine application name from git configuration. To
     *         be able determine application name <code>workDir</code> must not be <code>null</code>. If name not specified and
     *         cannot be determined IllegalStateException thrown
     * @param newname
     *         new name for application. If <code>null</code> IllegalStateException thrown
     * @param workDir
     *         git working directory. May be <code>null</code> if command executed out of git repository in this case
     *         <code>name</code> parameter must be not <code>null</code>. If not <code>null</code> and is git folder then remote
     *         configuration update
     * @return information about renamed application. Minimal set of application attributes:
     *         <ul>
     *         <li>New name</li>
     *         <li>New git URL of repository</li>
     *         <li>New HTTP URL of application</li>
     *         </ul>
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     * @throws IOException
     *         id any i/o errors occurs
     */
    public Map<String, String> renameApplication(String name, String newname, File workDir)
            throws HerokuException, ParsingResponseException, IOException, CredentialStoreException {
        return renameApplication(getCredential(), name, newname, workDir);
    }

    private Map<String, String> renameApplication(HerokuCredential herokuCredential, String name, String newname,
                                                  File workDir) throws HerokuException, ParsingResponseException, IOException {
        if (newname == null || newname.isEmpty()) {
            throw new IllegalStateException("New name may not be null or empty string. ");
        }

        if (name == null || name.isEmpty()) {
            name = detectAppName(workDir);
        }

        HttpURLConnection http = null;
        GitConnection git;
        try {
            URL url = new URL(HEROKU_API + "/apps/" + name);
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("PUT");
            http.setRequestProperty("Accept", "application/xml, */*");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            authenticate(herokuCredential, http);

            OutputStream output = http.getOutputStream();
            try {
                output.write(("app[name]=" + newname).getBytes());
                output.flush();
            } finally {
                output.close();
            }

            if (http.getResponseCode() != 200) {
                throw fault(http);
            }

            // Get updated info about application.
            Map<String, String> info = applicationInfo(herokuCredential, newname, false, workDir);

            String gitUrl = info.get("gitUrl");

            RemoteListRequest listRequest = new RemoteListRequest(null, true);
            git = GitConnectionFactory.getInstance().getConnection(workDir, null);
            List<Remote> remoteList = git.remoteList(listRequest);
            for (Remote r : remoteList) {
                // Update remote.
                if (r.getUrl().startsWith("git@heroku.com:")) {
                    String rname = extractAppName(r);
                    if (rname != null && rname.equals(name)) {
                        git.remoteUpdate(new RemoteUpdateRequest(r.getName(), null, false, new String[]{gitUrl},
                                                                 new String[]{r.getUrl()}, null, null));
                        break;
                    }
                }
            }
            return info;
        } catch (GitException gite) {
            throw new RuntimeException(gite.getMessage(), gite);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Get the list of Heroku application's available stacks.
     *
     * @param name
     *         current application name. If <code>null</code> then try to determine application name from git configuration. To
     *         be able determine application name <code>workDir</code> must not be <code>null</code>. If name not specified and
     *         cannot be determined {@link IllegalStateException} thrown
     * @param workDir
     *         git working directory. May be <code>null</code> if command executed out of git repository in this case
     *         <code>name</code> parameter must be not <code>null</code>.
     * @return {@link List} list of available application's stacks
     * @throws IOException
     *         if any i/o errors occurs
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     */
    public List<Stack> getStacks(String name, File workDir)
            throws HerokuException, ParsingResponseException, IOException, CredentialStoreException {
        return getStacks(getCredential(), name, workDir);
    }

    private List<Stack> getStacks(HerokuCredential herokuCredential, String name, File workDir)
            throws HerokuException, ParsingResponseException, IOException {
        List<Stack> stacks = new ArrayList<Stack>();
        if (name == null || name.isEmpty()) {
            name = detectAppName(workDir);
        }

        HttpURLConnection http = null;
        try {
            URL url = new URL(HEROKU_API + "/apps/" + name + "/stack");
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("Accept", "application/xml, */*");
            authenticate(herokuCredential, http);
            if (http.getResponseCode() != 200) {
                throw fault(http);
            }

            InputStream input = http.getInputStream();
            Document xmlDoc;
            try {
                xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
            } finally {
                input.close();
            }

            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList stackList = (NodeList)xpath.evaluate("/stacks/stack", xmlDoc, XPathConstants.NODESET);
            for (int i = 0, length = stackList.getLength(); i < length; i++) {
                Node stackElement = stackList.item(i);
                String stackName = (String)xpath.evaluate("name", stackElement, XPathConstants.STRING);
                boolean current = Boolean.valueOf((String)xpath.evaluate("current", stackElement, XPathConstants.STRING));
                boolean requested =
                        Boolean.valueOf((String)xpath.evaluate("requested", stackElement, XPathConstants.STRING));
                boolean beta = Boolean.valueOf((String)xpath.evaluate("beta", stackElement, XPathConstants.STRING));
                stacks.add(new StackImpl(stackName, current, beta, requested));
            }
        } catch (ParserConfigurationException pce) {
            throw new ParsingResponseException(pce.getMessage(), pce);
        } catch (SAXException sae) {
            throw new ParsingResponseException(sae.getMessage(), sae);
        } catch (XPathExpressionException xpe) {
            throw new ParsingResponseException(xpe.getMessage(), xpe);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
        return stacks;
    }

    /**
     * Migrate from current application's stack (deployment environment) to pointed one.
     *
     * @param name
     *         current application name. If <code>null</code> then try to determine application name from git configuration. To
     *         be able determine application name <code>workDir</code> must not be <code>null</code>. If name not specified and
     *         cannot be determined {@link IllegalStateException} thrown
     * @param workDir
     *         git working directory. May be <code>null</code> if command executed out of git repository in this case
     *         <code>name</code> parameter must be not <code>null</code>.
     * @param stack
     *         stack name to migrate to. If <code>null</code> IllegalStateException thrown
     * @return {@link String} output of the migration operation
     * @throws IOException
     *         if any i/o errors occurs
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     */
    public byte[] stackMigrate(String name, File workDir, String stack)
            throws HerokuException, IOException, CredentialStoreException {
        return stackMigrate(getCredential(), name, workDir, stack);
    }

    private byte[] stackMigrate(HerokuCredential herokuCredential, String name, File workDir, String stack)
            throws HerokuException, IOException {
        if (stack == null || stack.isEmpty()) {
            throw new IllegalStateException("Stack can not be null or empty string. ");
        }

        if (name == null || name.isEmpty()) {
            name = detectAppName(workDir);
        }

        HttpURLConnection http = null;
        try {
            URL url = new URL(HEROKU_API + "/apps/" + name + "/stack");
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("PUT");
            http.setRequestProperty("Accept", "application/xml, */*");
            http.setDoOutput(true);
            authenticate(herokuCredential, http);

            OutputStream output = http.getOutputStream();
            try {
                output.write(stack.getBytes());
                output.flush();
            } finally {
                output.close();
            }

            if (http.getResponseCode() != 200) {
                throw fault(http);
            }

            InputStream input = http.getInputStream();
            int length = http.getContentLength();
            byte[] text;
            try {
                text = readBody(input, length);
            } finally {
                input.close();
            }
            return text;
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * List of heroku applications for current user.
     *
     * @return List of names of applications for current user
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     * @throws ParsingResponseException
     *         if any error occurs when parse response body
     * @throws IOException
     *         id any i/o errors occurs
     */
    public List<String> listApplications()
            throws HerokuException, ParsingResponseException, IOException, CredentialStoreException {
        return listApplications(getCredential());
    }

    private List<String> listApplications(HerokuCredential herokuCredential)
            throws HerokuException, ParsingResponseException, IOException {
        HttpURLConnection http = null;
        try {
            URL url = new URL(HEROKU_API + "/apps");
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("Accept", "application/xml");
            authenticate(herokuCredential, http);

            if (http.getResponseCode() != 200) {
                throw fault(http);
            }

            InputStream input = http.getInputStream();
            Document xmlDoc;
            try {
                xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
            } finally {
                input.close();
            }

            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList appNodes = (NodeList)xPath.evaluate("/apps/app", xmlDoc, XPathConstants.NODESET);
            int appLength = appNodes.getLength();
            List<String> apps = new ArrayList<String>(appLength);
            for (int i = 0; i < appLength; i++) {
                String name = (String)xPath.evaluate("name", appNodes.item(i), XPathConstants.STRING);
                apps.add(name);
            }
            return apps;
        } catch (ParserConfigurationException pce) {
            throw new ParsingResponseException(pce.getMessage(), pce);
        } catch (SAXException sae) {
            throw new ParsingResponseException(sae.getMessage(), sae);
        } catch (XPathExpressionException xpe) {
            throw new ParsingResponseException(xpe.getMessage(), xpe);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * Run heroku rake command.
     *
     * @param name
     *         application name. If <code>null</code> then try to determine application name from git configuration. To be able
     *         determine application name <code>workDir</code> must not be <code>null</code> at least. If name not specified and
     *         cannot be determined IllegalStateException thrown
     * @param workDir
     *         git working directory. May be <code>null</code> if command executed out of git repository in this case
     *         <code>name</code> parameter must be not <code>null</code>
     * @param command
     *         command, in form: rake {command} {options}
     *         <p/>
     *         Examples:
     *         <ul>
     *         <li>rake db:create</li>
     *         <li>rake db:version</li>
     *         <ul>
     * @return LazyHttpChunkReader to read result of running command
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     * @throws IOException
     *         if any i/o occurs
     * @throws ParsingResponseException
     */
    public byte[] run(String name, File workDir, String command)
            throws HerokuException, IOException, ParsingResponseException, CredentialStoreException {
        return run(getCredential(), name, workDir, command);
    }

    private byte[] run(HerokuCredential herokuCredential, String name, File workDir, String command)
            throws HerokuException, ParsingResponseException, IOException {
        if (command == null || command.isEmpty()) {
            throw new IllegalStateException("Command is not defined. ");
        }

        if (name == null || name.isEmpty()) {
            name = detectAppName(workDir);
        }

        String rendezvousLocation = runAttachedProcess(name, command, herokuCredential);
        return accessStdInOut(rendezvousLocation);
    }

    /**
     * Run one-off process in Heroku.
     *
     * @param appName
     *         application name
     * @param command
     *         command to run
     * @param herokuCredential
     *         user's credentials
     * @return {@link String} rendezvous location
     * @throws IOException
     * @throws HerokuException
     * @throws ParsingResponseException
     */
    private String runAttachedProcess(String appName, String command, HerokuCredential herokuCredential)
            throws IOException, HerokuException, ParsingResponseException {
        HttpURLConnection http = null;
        try {
            // "attach" parameter points to use rendezvous to access stdin/stdout or
            // to stream process output to the application log:
            URL url = new URL(HEROKU_API + "/apps/" + appName + "/ps");
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setRequestProperty("Accept", "application/xml, */*");

            authenticate(herokuCredential, http);

            http.setDoOutput(true);
            OutputStream output = http.getOutputStream();
            try {
                output.write(("attach=true&command=" + command).getBytes());
                output.flush();
            } finally {
                output.close();
            }

            if (http.getResponseCode() != 200) {
                throw fault(http);
            }

            InputStream input = http.getInputStream();
            String rendezvousUrl;

            if (http.getHeaderField("Content-Type").contains("application/json")) {
                JsonParser jsonParser = new JsonParser();
                jsonParser.parse(input);
                rendezvousUrl = jsonParser.getJsonObject().getElement("rendezvous_url").getStringValue();
            } else {
                Document xmlDoc;
                try {
                    xmlDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
                } finally {
                    input.close();
                }

                XPath xpath = XPathFactory.newInstance().newXPath();

                rendezvousUrl = (String)xpath.evaluate("/process/rendezvous-url", xmlDoc, XPathConstants.STRING);
            }

            return rendezvousUrl;
        } catch (ParserConfigurationException pce) {
            throw new ParsingResponseException(pce.getMessage(), pce);
        } catch (SAXException sae) {
            throw new ParsingResponseException(sae.getMessage(), sae);
        } catch (XPathExpressionException xpe) {
            throw new ParsingResponseException(xpe.getMessage(), xpe);
        } catch (JsonException jse) {
            throw new ParsingResponseException(jse.getMessage(), jse);
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /** Access to stdin/stdout using rendezvous. */
    private byte[] accessStdInOut(String rendezvousLocation) throws IOException {
        rendezvousLocation = rendezvousLocation.replace("rendezvous://", "http://");
        URL rendezvousUrl = new URL(rendezvousLocation);
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        SSLSocket sslsocket = null;
        try {
            sslsocket = (SSLSocket)sslsocketfactory.createSocket(rendezvousUrl.getHost(), rendezvousUrl.getPort());

            OutputStream output = sslsocket.getOutputStream();
            InputStream input = sslsocket.getInputStream();
            try {
                output.write((rendezvousUrl.getPath().replaceFirst("/", "")).getBytes());
                output.flush();

                return readBody(input, -1);
            } finally {
                if (output != null) {
                    output.close();
                }

                if (input != null) {
                    input.close();
                }
            }
        } finally {
            if (sslsocket != null) {
                sslsocket.close();
            }
        }
    }

    /**
     * Get the application's logs.
     *
     * @param name
     *         current application name. If <code>null</code> then try to determine application name from git configuration. To
     *         be able determine application name <code>workDir</code> must not be <code>null</code>. If name not specified and
     *         cannot be determined {@link IllegalStateException} thrown
     * @param workDir
     *         git working directory. May be <code>null</code> if command executed out of git repository in this case
     *         <code>name</code> parameter must be not <code>null</code>.
     * @param logLines
     *         max number of log lines to be returned
     * @return {@link String} logs content
     * @throws IOException
     *         if any i/o errors occurs
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     */
    public byte[] logs(String name, File workDir, int logLines)
            throws HerokuException, IOException, GeneralSecurityException, CredentialStoreException {
        String logsLocation = getLogsLocation(getCredential(), name, workDir, logLines);
        return logs(logsLocation);
    }

    /**
     * Get the location of the logs stream.
     *
     * @param herokuCredential
     *         user's credentials
     * @param name
     *         current application name. If <code>null</code> then try to determine application name from git configuration. To
     *         be able determine application name <code>workDir</code> must not be <code>null</code>. If name not specified and
     *         cannot be determined {@link IllegalStateException} thrown
     * @param workDir
     *         git working directory. May be <code>null</code> if command executed out of git repository in this case
     *         <code>name</code> parameter must be not <code>null</code>.
     * @param logLines
     *         max number of log lines to be returned
     * @return {@link String} location of logs stream
     * @throws IOException
     * @throws HerokuException
     */
    private String getLogsLocation(HerokuCredential herokuCredential, String name, File workDir, int logLines)
            throws HerokuException, IOException {
        if (name == null || name.isEmpty()) {
            name = detectAppName(workDir);
        }

        HttpURLConnection http = null;
        try {
            String query = (logLines > 0) ? "&num=" + logLines : "";
            // "logplex" query parameter must be true:
            URL url = new URL(HEROKU_API + "/apps/" + name + "/logs?logplex=true" + query);
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("Accept", "application/xml, */*");
            authenticate(herokuCredential, http);
            if (http.getResponseCode() != 200) {
                throw fault(http);
            }

            InputStream input = http.getInputStream();
            BufferedReader r = null;
            String line = null;
            try {
                r = new BufferedReader(new InputStreamReader(input));
                line = r.readLine();
            } finally {
                if (r != null) {
                    r.close();
                }
                input.close();
            }
            return line;
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }

    /**
     * @param logsLocation
     *         location of the logs stream
     * @return {@link String} logs' content
     * @throws IOException
     * @throws HerokuException
     */
    private byte[] logs(String logsLocation) throws HerokuException, IOException, java.security.GeneralSecurityException {
        // Create a trust manager that does not validate certificate chains
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, TRUST_ALL_CERTS, new java.security.SecureRandom());
        URL url = new URL(logsLocation);
        HttpURLConnection seccon = null;
        try {
            seccon = (HttpURLConnection)url.openConnection();
            seccon.setRequestMethod("GET");
            ((HttpsURLConnection)seccon).setSSLSocketFactory(sc.getSocketFactory());
            if (seccon.getResponseCode() != 200) {
                throw fault(seccon);
            }
            InputStream input = seccon.getInputStream();
            int length = seccon.getContentLength();
            byte[] logs;
            try {
                logs = readBody(input, length);
            } finally {
                input.close();
            }
            return logs;
        } finally {
            if (seccon != null) {
                seccon.disconnect();
            }
        }
    }


    private HerokuCredential getCredential() throws HerokuException, CredentialStoreException {
        final Credential credential = new Credential();
        credentialStore.load(getUserId(), "heroku", credential);
        final String email = credential.getAttribute("email");
        final String apiKey = credential.getAttribute("api_key");
        if (email == null || apiKey == null) {
            throw new HerokuException(200, "Authentication required.\n", "text/plain");
        }
        return new HerokuCredential(email, apiKey);
    }

    /**
     * Add Basic authentication headers to HttpURLConnection.
     *
     * @param herokuCredential
     *         heroku account credentials
     * @param http
     *         HttpURLConnection
     * @throws IOException
     *         if any i/o errors occurs
     */
    private static void authenticate(HerokuCredential herokuCredential, HttpURLConnection http) throws IOException {
        byte[] base64 =
                encodeBase64((herokuCredential.getEmail() + ':' + herokuCredential.getApiKey()).getBytes("ISO-8859-1"));
        http.setRequestProperty("Authorization", "Basic " + new String(base64, "ISO-8859-1"));
    }

    /**
     * Extract heroku application name from git configuration. If <code>workDir</code> is <code>null</code> or does not contain
     * <code>.git<code> sub-directory method always return <code>null</code>.
     *
     * @return application name or <code>null</code> if name can't be determined since command invoked outside of git repository
     */
    private static String detectAppName(File workDir) {
        String app = null;
        if (workDir != null && new File(workDir, ".git").exists()) {
            GitConnection git = null;
            List<Remote> remotes;
            try {
                git = GitConnectionFactory.getInstance().getConnection(workDir, null);
                remotes = git.remoteList(new RemoteListRequest(null, true));
            } catch (GitException ge) {
                throw new RuntimeException(ge.getMessage(), ge);
            } finally {
                if (git != null) {
                    git.close();
                }
            }
            for (Iterator<Remote> iter = remotes.iterator(); iter.hasNext() && app == null; ) {
                Remote r = iter.next();
                if (r.getUrl().startsWith("git@heroku.com:")) {
                    app = extractAppName(r);
                }
            }
        }
        if (app == null || app.isEmpty()) {
            throw new RuntimeException("Not a Heroku application. Please select root folder of Heroku project. ");
        }
        return app;
    }

    private static String extractAppName(Remote gitRemote) {
        String name = null;
        try {
            name = new URIish(gitRemote.getUrl()).getHumanishName();
        } catch (URISyntaxException e) {
            // Invalid URL is not a problem for us, just say we can't determine name from wrong URL.
        }
        return name;
    }

    static HerokuException fault(HttpURLConnection http) throws IOException {
        HerokuException error;
        InputStream errorStream = null;
        try {
            errorStream = http.getErrorStream();
            if (errorStream == null) {
                error = new HerokuException(http.getResponseCode(), null, null);
            } else {
                int length = http.getContentLength();
                byte[] body = readBody(errorStream, length);
                if (body.length == 0) {
                    error = new HerokuException(http.getResponseCode(), null, null);
                } else {
                    String message = new String(body);
                    // On invalid credentials the login form is sent (HTML).
                    // Check body contains action with login path and element with id "login".
                    error =
                            (404 == http.getResponseCode() && (message.contains("action=\"/login\"") || message
                                    .contains("id=\"login\""))) //
                            ? new HerokuException(400, "Authentication failed.", MediaType.TEXT_PLAIN) //
                            : new HerokuException(http.getResponseCode(), message, http.getContentType());
                }
            }
        } finally {
            if (errorStream != null) {
                errorStream.close();
            }
        }
        return error;
    }

    private static byte[] readBody(InputStream input, int contentLength) throws IOException {
        if (contentLength > 0) {
            byte[] b = new byte[contentLength];
            int point, off = 0;
            while ((point = input.read(b, off, contentLength - off)) > 0) {
                off += point;
            }
            return b;
        } else if (contentLength < 0) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int point;
            while ((point = input.read(buf)) != -1) {
                bout.write(buf, 0, point);
            }
            return bout.toByteArray();
        }
        return new byte[0];
    }
}
