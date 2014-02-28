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
package com.codenvy.ide.ext.ssh.server;

import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.ssh.dto.GenKeyRequest;
import com.codenvy.ide.ext.ssh.dto.KeyItem;
import com.codenvy.ide.ext.ssh.dto.PublicKey;

import org.apache.commons.fileupload.FileItem;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * REST interface to SshKeyProvider.
 *
 * @author andrew00x
 */
@Path("ssh-keys/{ws-id}")
public class KeyService {
    private final SshKeyStore keyStore;

    @PathParam("ws-id")
    private String wsId;

    @Inject
    public KeyService(SshKeyStore keyStore) {
        this.keyStore = keyStore;
    }

    /** Generate SSH key pair. */
    @POST
    @Path("gen")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response genKeyPair(GenKeyRequest request) {
        try {
            keyStore.genKeyPair(request.getHost(), request.getComment(), request.getPassphrase());
        } catch (SshKeyStoreException e) {
            throw new WebApplicationException(Response.serverError() //
                                                      .entity(e.getMessage()) //
                                                      .type(MediaType.TEXT_PLAIN) //
                                                      .build());
        }
        return Response.ok().build();
    }

    /** Add prepared private key. */
    @POST
    @Path("add")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Response addPrivateKey(@QueryParam("host") String host,
                                  Iterator<FileItem> iterator) {
        /*
         * XXX : Temporary turn-off don't work on demo site if (!security.isSecure()) { throw new
         * WebApplicationException(Response.status(400)
         * .entity("Secure connection required to be able generate key. ").type(MediaType.TEXT_PLAIN).build()); }
         */
        byte[] key = null;
        while (iterator.hasNext() && key == null) {
            FileItem fileItem = iterator.next();
            if (!fileItem.isFormField()) {
                key = fileItem.get();
            }
        }
        // Return error response in <pre> HTML tag.
        if (key == null) {
            throw new WebApplicationException(Response.ok("<pre>Can't find input file.</pre>", MediaType.TEXT_HTML).build());
        }

        try {
            keyStore.addPrivateKey(host, key);
        } catch (SshKeyStoreException e) {
            throw new WebApplicationException(Response.ok("<pre>" + e.getMessage() + "</pre>", MediaType.TEXT_HTML).build());
        }
        return Response.ok("", MediaType.TEXT_HTML).build();
    }

    /**
     * Get public key.
     *
     * @see {@link SshKeyStore#genKeyPair(String, String, String)}
     * @see {@link SshKeyStore#getPublicKey(String)}
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPublicKey(@Context SecurityContext security, @QueryParam("host") String host) {

        /*
         * XXX : Temporary turn-off don't work on demo site if (!security.isSecure()) { throw new
         * WebApplicationException(Response.status(400)
         * .entity("Secure connection required to be able generate key. ").type(MediaType.TEXT_PLAIN).build()); }
         */
        SshKey publicKey;
        try {
            publicKey = keyStore.getPublicKey(host);
        } catch (SshKeyStoreException e) {
            throw new WebApplicationException(Response.serverError() //
                                                      .entity(e.getMessage()) //
                                                      .type(MediaType.TEXT_PLAIN) //
                                                      .build());
        }
        if (publicKey == null) {
            throw new WebApplicationException(Response.status(404) //
                                                      .entity("Public key for host " + host + " not found. ") //
                                                      .type(MediaType.TEXT_PLAIN) //
                                                      .build());
        }

        return Response.ok(DtoFactory.getInstance().createDto(PublicKey.class).withHost(host).withKey(new String(publicKey.getBytes())),
                           MediaType.APPLICATION_JSON).build();
    }

    /** Remove SSH keys. */
    @GET
    @Path("remove")
    public String removeKeys(@QueryParam("host") String host, @QueryParam("callback") String calback) {
        try {
            keyStore.removeKeys(host);
        } catch (SshKeyStoreException e) {
            throw new WebApplicationException(Response.serverError() //
                                                      .entity(e.getMessage()) //
                                                      .type(MediaType.TEXT_PLAIN) //
                                                      .build());
        }
        return calback + "();";
    }

    @GET
    @Path("all")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getKeys(@Context UriInfo uriInfo) {
        try {
            Set<String> all = keyStore.getAll();
            if (all.size() > 0) {
                List<KeyItem> result = new ArrayList<KeyItem>(all.size());
                for (String host : all) {
                    boolean publicKeyExists = keyStore.getPublicKey(host) != null;
                    String getPublicKeyUrl = null;
                    if (publicKeyExists) {
                        getPublicKeyUrl =
                                uriInfo.getBaseUriBuilder().path(getClass()).queryParam("host", host).build(wsId).toString();
                    }
                    String removeKeysUrl =
                            uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "removeKeys").queryParam("host", host)
                                   .build(wsId).toString();
                    result.add(DtoFactory.getInstance().createDto(KeyItem.class).withHost(host).withPublicKeyUrl(getPublicKeyUrl)
                                         .withRemoteKeyUrl(removeKeysUrl));
                }
                return Response.ok().entity(result).type(MediaType.APPLICATION_JSON).build();
            }
            return Response.ok(Collections.emptyList(), MediaType.APPLICATION_JSON).build();
        } catch (SshKeyStoreException e) {
            throw new WebApplicationException(Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build());
        }
    }
}
