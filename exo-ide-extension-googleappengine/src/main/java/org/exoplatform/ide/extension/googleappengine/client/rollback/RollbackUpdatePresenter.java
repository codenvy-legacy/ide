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
package org.exoplatform.ide.extension.googleappengine.client.rollback;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEnginePresenter;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 21, 2012 11:54:50 AM anya $
 */
public class RollbackUpdatePresenter extends GoogleAppEnginePresenter implements RollbackUpdateHandler {
    public RollbackUpdatePresenter() {
        IDE.addHandler(RollbackUpdateEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.rollback.RollbackUpdateHandler#onRollbackUpdate(org.exoplatform.ide
     * .extension.googleappengine.client.rollback.RollbackUpdateEvent) */
    @Override
    public void onRollbackUpdate(RollbackUpdateEvent event) {
        if (isAppEngineProject()) {
            rollback();
        }
    }

    public void rollback() {
        try {
            GoogleAppEngineClientService.getInstance().rollback(currentVfs.getId(), currentProject.getId(),
                                                                new GoogleAppEngineAsyncRequestCallback<Object>() {

                                                                    @Override
                                                                    protected void onSuccess(Object result) {
                                                                        IDE.fireEvent(new OutputEvent(
                                                                                GoogleAppEngineExtension.GAE_LOCALIZATION
                                                                                                        .rollbackUpdateSuccess(),
                                                                                Type.INFO));
                                                                    }
                                                                });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
