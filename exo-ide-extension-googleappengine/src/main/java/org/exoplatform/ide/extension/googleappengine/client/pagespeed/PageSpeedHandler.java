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
package org.exoplatform.ide.extension.googleappengine.client.pagespeed;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEnginePresenter;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 24, 2012 3:49:49 PM anya $
 */
public class PageSpeedHandler extends GoogleAppEnginePresenter implements UpdatePageSpeedHandler {

    public PageSpeedHandler() {
        IDE.addHandler(UpdatePageSpeedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.pagespeed.UpdatePageSpeedHandler#onUpdatePageSpeed(org.exoplatform.ide
     * .extension.googleappengine.client.pagespeed.UpdatePageSpeedEvent) */
    @Override
    public void onUpdatePageSpeed(UpdatePageSpeedEvent event) {
        if (isAppEngineProject()) {
            updatePageSpeed();
        } else {
            Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.notAppEngineProjectError());
        }
    }

    public void updatePageSpeed() {
        try {
            GoogleAppEngineClientService.getInstance().updatePagespeed(currentVfs.getId(), currentProject.getId(),
                                                                       new GoogleAppEngineAsyncRequestCallback<Object>() {

                                                                           @Override
                                                                           protected void onSuccess(Object result) {
                                                                               IDE.fireEvent(new OutputEvent(
                                                                                       GoogleAppEngineExtension.GAE_LOCALIZATION
                                                                                                               .updatePageSpeedSuccessfully(),
                                                                                       Type.INFO));
                                                                           }
                                                                       });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
