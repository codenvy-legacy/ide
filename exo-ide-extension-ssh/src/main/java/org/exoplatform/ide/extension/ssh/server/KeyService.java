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
package org.exoplatform.ide.extension.ssh.server;

import org.apache.commons.fileupload.FileItem;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.ide.extension.ssh.shared.*;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * REST interface to SshKeyProvider.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/ssh-keys")
public class KeyService {
    private final SshKeyStore keyStore;

    private long MAX_UPLOAD_SIZE = 16384L;

    public KeyService(SshKeyStore keyStore) {
        this.keyStore = keyStore;
    }

    /** Generate SSH key pair. */
    @POST
    @Path("gen")
    @RolesAllowed({"developer"})
    @Consumes({MediaType.APPLICATION_JSON})
    public void genKeyPair(GenKeyRequest request) throws SshKeyStoreException {
        keyStore.genKeyPair(request.getHost(), request.getComment(), request.getPassphrase());
    }

    /** Add prepared private key. */
    @POST
    @Path("add")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @RolesAllowed({"developer"})
    public Response addPrivateKey(@HeaderParam(HTTPHeader.CONTENT_LENGTH) Long length,
                                  @QueryParam("host") String host,
                                  Iterator<FileItem> iterator) throws SshKeyStoreException {
        if (length > MAX_UPLOAD_SIZE) {
            throw new SshKeyStoreException("File is to large to proceed.");
        }

        byte[] key = null;
        while (iterator.hasNext() && key == null) {
            FileItem fileItem = iterator.next();
            if (!fileItem.isFormField()) {
                if (fileItem.getSize() > MAX_UPLOAD_SIZE) {
                    throw new SshKeyStoreException("File is to large to proceed.");
                }
                key = fileItem.get();
            }
        }

        if (key == null) {
            throw new SshKeyStoreException("Can't find input file.");
        }
        for (String keyContentLine : new String(key).split("\\n")) {
            if (keyContentLine.matches("(?i)proc-type:\\s*\\d*,\\s*encrypted\\s*")) {
                throw new SshKeyStoreException("SSH key with passphrase is not supported");
            }
        }
        keyStore.addPrivateKey(host, key);

        //method usually return 204, but on client-side submit complete handler wont work with it, so we create response with 200, to work
        //client properly.
        return Response.ok().entity("").header(HTTPHeader.CONTENT_TYPE, MediaType.TEXT_HTML).build();
    }

    /**
     * Get public key.
     *
     * @see {@link SshKeyStore#genKeyPair(String, String, String)}
     * @see {@link SshKeyStore#getPublicKey(String)}
     */
    @GET
    @RolesAllowed({"developer"})
    @Produces({MediaType.APPLICATION_JSON})
    public PublicKey getPublicKey(@QueryParam("host") String host) throws SshKeyStoreException {
        SshKey publicKey = keyStore.getPublicKey(host);

        if (publicKey == null) {
            throw new SshKeyStoreException("Public key for host " + host + " not found.");
        }

        return new PublicKeyImpl(host, new String(publicKey.getBytes()));
    }

    /** Remove SSH keys. */
    @POST
    @Path("remove")
    @RolesAllowed({"developer"})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public void removeKeys(@FormParam("host") String host) throws SshKeyStoreException {
        keyStore.removeKeys(host);
    }

    @GET
    @Path("all")
    @RolesAllowed({"developer"})
    @Produces({MediaType.APPLICATION_JSON})
    public ListKeyItem getKeys() throws SshKeyStoreException {
        Set<String> hosts = keyStore.getAll();
        List<KeyItem> keys = new ArrayList<>(hosts.size());
        for (String host : hosts) {
            keys.add(new KeyItemImpl(host, keyStore.getPublicKey(host) != null));
        }

        return new ListKeyItemImpl(keys);
    }
}
