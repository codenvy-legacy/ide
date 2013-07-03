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
package com.codenvy.ide.ext.aws.client.command;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.aws.client.AWSResource;
import com.codenvy.ide.ext.aws.client.s3.S3ManagerPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class S3ManagementAction extends Action {
    private S3ManagerPresenter presenter;
    private ResourceProvider   resourceProvider;

    @Inject
    public S3ManagementAction(S3ManagerPresenter presenter, ResourceProvider resourceProvider, AWSResource resource) {
        super("S3 Management...", "Manage S3 Buckets", resource.s3());
        this.presenter = presenter;
        this.resourceProvider = resourceProvider;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.showDialog();
    }


}
