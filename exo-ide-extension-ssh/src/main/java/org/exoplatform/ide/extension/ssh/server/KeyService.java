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
import org.exoplatform.ide.extension.ssh.shared.GenKeyRequest;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;
import org.exoplatform.ide.extension.ssh.shared.PublicKey;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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
    @RolesAllowed({"developer"})
    public Response addPrivateKey(@HeaderParam(HTTPHeader.CONTENT_LENGTH) Long length,
                                  @QueryParam("host") String host,
                                  Iterator<FileItem> iterator) {
        if (length > MAX_UPLOAD_SIZE) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                                                      .entity("File is to large to proceed.")
                                                      .header(HTTPHeader.CONTENT_TYPE, MediaType.TEXT_HTML)
                                                      .build());
        }

        byte[] key = null;
        while (iterator.hasNext() && key == null) {
            FileItem fileItem = iterator.next();
            if (!fileItem.isFormField()) {
                if (fileItem.getSize() > MAX_UPLOAD_SIZE) {
                    throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                                                              .entity("File is to large to proceed.")
                                                              .header(HTTPHeader.CONTENT_TYPE, MediaType.TEXT_HTML)
                                                              .build());
                }
                key = fileItem.get();
            }
        }

        if (key == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                                                      .entity("Can't find input file.")
                                                      .header(HTTPHeader.CONTENT_TYPE, MediaType.TEXT_HTML)
                                                      .build());
        }

        try {
            keyStore.addPrivateKey(host, key);
        } catch (SshKeyStoreException e) {
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                                      .entity(e.getLocalizedMessage())
                                                      .header(HTTPHeader.CONTENT_TYPE, MediaType.TEXT_HTML)
                                                      .build());
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
    @RolesAllowed({"developer"})
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPublicKey(@QueryParam("host") String host) {
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
                                                      .entity("Public key for host " + host + " not found.") //
                                                      .type(MediaType.TEXT_PLAIN) //
                                                      .build());
        }
        return Response.ok(new PublicKey(host, new String(publicKey.getBytes())), MediaType.APPLICATION_JSON).build();
    }

    /** Remove SSH keys. */
    @GET
    @Path("remove")
    @RolesAllowed({"developer"})
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
    @RolesAllowed({"developer"})
    @Produces({MediaType.APPLICATION_JSON})
    public Response getKeys(@Context UriInfo uriInfo, @PathParam("ws-name") String wsName) {
        try {
            Set<String> all = keyStore.getAll();
            if (all.size() > 0) {
                List<KeyItem> result = new ArrayList<KeyItem>(all.size());
                for (String host : all) {
                    boolean publicKeyExists = keyStore.getPublicKey(host) != null;
                    String getPublicKeyUrl = null;
                    if (publicKeyExists) {
                        getPublicKeyUrl =
                                          uriInfo.getBaseUriBuilder().path(getClass()).queryParam("host", host).build(wsName).toString();
                    }
                    String removeKeysUrl =
                            uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "removeKeys").queryParam("host", host)
                                   .build(wsName).toString();

                    result.add(new KeyItem(host, getPublicKeyUrl, removeKeysUrl));
                }
                return Response.ok().entity(result).type(MediaType.APPLICATION_JSON).build();
            }
            return Response.ok(Collections.emptyList(), MediaType.APPLICATION_JSON).build();
        } catch (SshKeyStoreException e) {
            throw new WebApplicationException(Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build());
        }
    }
}
