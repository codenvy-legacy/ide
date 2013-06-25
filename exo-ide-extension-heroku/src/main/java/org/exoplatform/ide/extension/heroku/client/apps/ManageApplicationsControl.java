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
package org.exoplatform.ide.extension.heroku.client.apps;

import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.heroku.client.HerokuClientBundle;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.control.AbstractHerokuControl;

/**
 * Control for managing Heroku applications.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 14, 2012 5:15:04 PM anya $
 */
@RolesAllowed("developer")
public class ManageApplicationsControl extends AbstractHerokuControl {

    public ManageApplicationsControl() {
        super(HerokuExtension.LOCALIZATION_CONSTANT.listApplicationsControlId());
        setTitle(HerokuExtension.LOCALIZATION_CONSTANT.listApplicationsControlTitle());
        setPrompt(HerokuExtension.LOCALIZATION_CONSTANT.listApplicationsControlPrompt());
        setEvent(new ManageApplicationsEvent());
        setImages(HerokuClientBundle.INSTANCE.applicationsList(), HerokuClientBundle.INSTANCE.applicationsListDisabled());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);

        setVisible(true);
    }

    protected void refresh() {
        setEnabled(vfsInfo != null);
    }

}
