/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.editor.client.api;

import com.codenvy.ide.client.util.logging.Log;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class FileContentLoader {
    private FileContentLoader(){
    }

    public static void getFileContent(FileModel file, final ContentCallback callback){
        try {
            VirtualFileSystem.getInstance().getContent(
                    new AsyncRequestCallback<FileModel>(new FileContentUnmarshaller(file)) {
                        @Override
                        protected void onSuccess(FileModel result) {
                            callback.onContentReceived(result.getContent());
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            Log.error(FileContentLoader.class, exception);
                        }
                    });
        } catch (RequestException e) {
            Log.error(FileContentLoader.class, e);
        }
    }

    public interface ContentCallback{
        void onContentReceived(String content);
    }
}
