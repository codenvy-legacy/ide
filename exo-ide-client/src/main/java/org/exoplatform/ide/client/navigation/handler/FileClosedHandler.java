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
package org.exoplatform.ide.client.navigation.handler;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedEvent;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 13, 2010 $
 */
public class FileClosedHandler implements EditorFileClosedHandler, ApplicationSettingsReceivedHandler {

    private Map<String, String> lockTokens;

    private static final String UNLOCK_FAILURE_MSG = IDE.ERRORS_CONSTANT.fileClosedUnlockFailure();

    public FileClosedHandler() {
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client
     * .framework.editor.event.EditorFileClosedEvent) */
    public void onEditorFileClosed(final EditorFileClosedEvent event) {
        final String lockToken = lockTokens.get(event.getFile().getId());
        if (!event.getFile().isPersisted()) {
            return;
        }

        if (lockToken != null) {
            if (event.getFile().getLinks().isEmpty()) {
                try {
                    VirtualFileSystem.getInstance()
                                     .getItemById(event.getFile().getId(),
                                                  new AsyncRequestCallback<ItemWrapper>(
                                                                                        new ItemUnmarshaller(
                                                                                                             new ItemWrapper(
                                                                                                                             event.getFile()))) {

                                                      @Override
                                                      protected void onSuccess(ItemWrapper result) {
                                                          event.getFile().setLinks(result.getItem().getLinks());
                                                          unlockFile(event.getFile(), lockToken);
                                                      }

                                                      @Override
                                                      protected void onFailure(Throwable exception) {
                                                          IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                      }
                                                  });
                } catch (RequestException e) {
                    IDE.fireEvent(new ExceptionThrownEvent(e));
                }
            } else {
                unlockFile(event.getFile(), lockToken);
            }
        }
    }
    
    private void unlockFile(final FileModel file, String lockToken) {
        try {
            VirtualFileSystem.getInstance().unlock(file, lockToken, new AsyncRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                    IDE.fireEvent(new ItemUnlockedEvent(file));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception, UNLOCK_FAILURE_MSG));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e, UNLOCK_FAILURE_MSG));
        }
    }

    /** @see org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org
     * .exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent) */
    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null) {
            event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
        }

        lockTokens = event.getApplicationSettings().getValueAsMap("lock-tokens");
    }

}
