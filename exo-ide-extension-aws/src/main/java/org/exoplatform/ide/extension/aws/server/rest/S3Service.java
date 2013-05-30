/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.aws.server.rest;

import org.apache.commons.fileupload.FileItem;
import org.exoplatform.ide.extension.aws.server.AWSException;
import org.exoplatform.ide.extension.aws.server.s3.S3;
import org.exoplatform.ide.extension.aws.server.s3.S3Content;
import org.exoplatform.ide.extension.aws.shared.s3.*;
import org.exoplatform.ide.security.paas.CredentialStoreException;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Path("{ws-name}/aws/s3")
public class S3Service {
    @Inject
    private S3 s3;

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    public S3Service() {
    }

    //

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void login(Map<String, String> credentials) throws AWSException, CredentialStoreException {
        s3.login(credentials.get("access_key"), credentials.get("secret_key"));
    }

    @Path("logout")
    @POST
    public void logout() throws CredentialStoreException {
        s3.logout();
    }

    //

    @Path("buckets/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public S3Bucket createBucket(@QueryParam("name") String name,
                                 @QueryParam("region") String region) throws AWSException, CredentialStoreException {
        S3Region s3Region = S3Region.fromValue(region);
        return s3.createBucket(name, s3Region);
    }

    @Path("buckets")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<S3Bucket> listBuckets() throws AWSException, CredentialStoreException {
        return s3.listBuckets();
    }

    @Path("buckets/delete/{name}")
    @POST
    public void deleteBucket(@PathParam("name") String name) throws AWSException, CredentialStoreException {
        s3.deleteBucket(name);
    }

    @Path("buckets/versioning/{s3bucket}")
    @POST
    public void setVersioningStatus(@PathParam("s3bucket") String s3Bucket, @QueryParam("status") String status)
            throws AWSException, CredentialStoreException {
        S3VersioningStatus versioningStatus = S3VersioningStatus.fromValue(status);
        s3.setVersioningStatus(s3Bucket, versioningStatus);
    }

    @Path("buckets/acl/{s3bucket}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<S3AccessControl> getBucketAcl(@PathParam("s3bucket") String s3Bucket)
            throws AWSException, CredentialStoreException {
        return s3.getBucketAcl(s3Bucket);
    }

    @Path("buckets/acl/{s3bucket}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateBucketAcl(@PathParam("s3bucket") String s3Bucket,
                                UpdateAccessControlRequest s3UpdateAccessControls)
            throws AWSException, CredentialStoreException {
        s3.updateBucketAcl(s3Bucket, s3UpdateAccessControls);
    }
    //

    @Path("objects/put/{s3bucket}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public NewS3Object putObject(@PathParam("s3bucket") String s3Bucket,
                                 @QueryParam("s3key") String s3Key,
                                 @QueryParam("data") URL data)
            throws AWSException, CredentialStoreException, IOException {
        return s3.putObject(s3Bucket, s3Key, data);
    }

    @Path("objects/upload_project/{s3bucket}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public NewS3Object uploadProject(@PathParam("s3bucket") String s3Bucket,
                                     @QueryParam("s3key") String s3Key,
                                     @QueryParam("vfsid") String vfsid,
                                     @QueryParam("projectid") String projectId)
            throws VirtualFileSystemException, AWSException, IOException, CredentialStoreException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsid).newInstance(null, null);
        return s3.uploadProject(s3Bucket, s3Key, vfs, projectId);
    }

    @Path("objects/upload/{s3bucket}")
    @POST
    public Response uploadFile(@PathParam("s3bucket") String s3Bucket,
                               Iterator<FileItem> formData)
            throws IOException, InvalidArgumentException, AWSException, CredentialStoreException {
        FileItem contentItem = null;
        String mediaType = null;
        String name = null;

        while (formData.hasNext()) {
            FileItem item = formData.next();

            if (!item.isFormField()) {
                if (contentItem == null) {
                    contentItem = item;
                } else {
                    throw new InvalidArgumentException("More then one upload file is found but only one should be. ");
                }
                if ("file".equals(item.getFieldName())) {
                    name = item.getString().trim();
                }
            } else if ("mimeType".equals(item.getFieldName())) {
                mediaType = item.getString().trim();
            } else if ("name".equals(item.getFieldName())) {
                name = item.getString().trim();
            }

        }

        if (contentItem == null) {
            throw new InvalidArgumentException("Cannot find file for upload. ");
        }

        if (name == null || name.isEmpty()) {
            throw new InvalidArgumentException("File name is required. ");
        }

        if (mediaType == null || mediaType.isEmpty()) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        s3.putObject(s3Bucket, name, contentItem.getInputStream(), mediaType, contentItem.getSize());

        return Response.ok("", MediaType.TEXT_HTML).build();
    }

    @Path("objects/{s3bucket}")
    @GET
    public Response downloadFile(@PathParam("s3bucket") String s3Bucket,
                                 @QueryParam("s3key") String s3Key) throws AWSException, CredentialStoreException {
        S3Content content = s3.getObjectContent(s3Bucket, s3Key);

        return Response
                .ok(content.getStream(), content.getContentType())
                .lastModified(content.getLastModificationDate())
                .header(HttpHeaders.CONTENT_LENGTH, Long.toString(content.getLength()))
                .header("Content-Disposition", "attachment; filename=\"" + s3Key + "\"")
                .build();
    }

    @Path("objects/delete/{s3bucket}")
    @POST
    public void deleteObject(@PathParam("s3bucket") String s3Bucket,
                             @QueryParam("s3key") String s3key) throws AWSException, CredentialStoreException {
        s3.deleteObject(s3Bucket, s3key);
    }

    @Path("objects/delete/{s3bucket}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteObjects(@PathParam("s3bucket") String s3Bucket, List<S3KeyVersions> s3Keys)
            throws AWSException, CredentialStoreException {
        s3.deleteObjects(s3Bucket, s3Keys);
    }

    @Path("objects/versions/{s3bucket}")
    @POST
    public void deleteVersion(@PathParam("s3bucket") String s3Bucket,
                              @QueryParam("s3key") String s3Key,
                              @QueryParam("versionid") String versionId) throws AWSException, CredentialStoreException {
        s3.deleteVersion(s3Bucket, s3Key, versionId);
    }

    @Path("objects/versions/{s3bucket}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<S3ObjectVersion> listVersions(@PathParam("s3bucket") String s3Bucket,
                                              @QueryParam("prefix") String prefix,
                                              @QueryParam("keymarker") String keyMarker,
                                              @QueryParam("versionidmarker") String versionIdMarker,
                                              @QueryParam("delimiter") String delimiter,
                                              @QueryParam("maxresults") Integer maxResults)
            throws AWSException, CredentialStoreException {
        return s3.listVersions(s3Bucket, prefix, keyMarker, versionIdMarker, delimiter, maxResults);
    }

    @Path("objects/{s3bucket}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public S3ObjectsList listObjects(@PathParam("s3bucket") String s3Bucket,
                                     @QueryParam("prefix") String prefix,
                                     @QueryParam("nextmarker") String nextMarker,
                                     @QueryParam("maxkeys") int maxKeys) throws AWSException, CredentialStoreException {
        return s3.listObjects(s3Bucket, prefix, nextMarker, maxKeys);
    }

    @Path("objects/acl/{s3bucket}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<S3AccessControl> getObjectAcl(@PathParam("s3bucket") String s3Bucket,
                                              @QueryParam("s3key") String s3Key,
                                              @QueryParam("versionid") String versionId)
            throws AWSException, CredentialStoreException {
        return s3.getObjectAcl(s3Bucket, s3Key, versionId);
    }

    @Path("objects/acl/{s3bucket}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateObjectAcl(@PathParam("s3bucket") String s3Bucket,
                                @QueryParam("s3key") String s3Key,
                                @QueryParam("versionid") String versionId,
                                UpdateAccessControlRequest s3UpdateAccessControls)
            throws AWSException, CredentialStoreException {
        s3.updateObjectAcl(s3Bucket, s3Key, versionId, s3UpdateAccessControls);
    }
}