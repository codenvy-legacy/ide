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
