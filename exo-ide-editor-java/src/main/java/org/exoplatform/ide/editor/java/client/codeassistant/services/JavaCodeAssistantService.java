/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.editor.java.client.codeassistant.services;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.codeassistant.jvm.shared.TypesInfoList;
import org.exoplatform.ide.codeassistant.jvm.shared.TypesList;
import org.exoplatform.ide.editor.java.client.codeassistant.services.marshal.String2ArrayMarshaller;
import org.exoplatform.ide.editor.java.client.model.Types;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

/**
 * Implementation of {@link CodeAssistantService} <br>
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 4:44:53 PM evgen $
 */
public class JavaCodeAssistantService extends CodeAssistantService {

    private static JavaCodeAssistantService instance;

    private static String                   FIND_BY_PROJECT;

    private static String                   TYPES_BY_FQNS;

    public JavaCodeAssistantService(String restContext, String ws, Loader loader) {
        super(restContext, loader, ws + "/code-assistant/java/class-description?fqn=", // GET_CLASS_URL
              ws + "/code-assistant/java/find-by-prefix/", // FIND_CLASS_BY_PREFIX
              ws + "/code-assistant/java/find-by-type/");
        FIND_BY_PROJECT = ws + "/code-assistant/java/find-in-package";
        TYPES_BY_FQNS = ws + "/code-assistant/java/types-by-fqns";
        instance = this;
    }

    public static JavaCodeAssistantService get() {
        if (instance == null)
            instance = new JavaCodeAssistantService(Utils.getRestContext(), Utils.getWorkspaceName(), IDELoader.getInstance());
        return instance;
    }

    /**
     * Find all classes from project with file.
     * 
     * @param fileRelPath for who autocompletion called (Need for find classpath)
     * @param callback - the callback which client has to implement
     */
    public void findClassesByProject(String fileId, String projectId, AsyncRequestCallback<TypesList> callback) {
        if (fileId != null) {
            String url = restServiceContext + FIND_BY_PROJECT;
            url +=
                   "?fileid=" + fileId + "&projectid=" + projectId + "&vfsid="
                       + VirtualFileSystem.getInstance().getInfo().getId();
            try {
                AsyncRequest.build(RequestBuilder.GET, url).send(callback);
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }
        }
    }

    public void findTypeByPrefix(String prefix, Types type, String projectId, AsyncRequestCallback<TypesList> callback) {
        String url = restServiceContext + FIND_TYPE + type.toString();
        url += "?projectid=" + projectId + "&vfsid=" + VirtualFileSystem.getInstance().getInfo().getId();
        if (prefix != null && !prefix.isEmpty()) {
            url += "&prefix=" + prefix;
        }
        try {
            AsyncRequest.build(RequestBuilder.GET, url).send(callback);
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    public void getTypesByFqns(String[] fqns, String projectId, AsyncRequestCallback<TypesInfoList> callback) {
        String url = restServiceContext + TYPES_BY_FQNS;
        url += "?vfsid=" + VirtualFileSystem.getInstance().getInfo().getId();
        if (projectId != null)
            url += "&projecid=" + projectId;
        try {
            Marshallable marshallable = new String2ArrayMarshaller(fqns);
            AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                        .data(marshallable.marshal()).send(callback);
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

}
