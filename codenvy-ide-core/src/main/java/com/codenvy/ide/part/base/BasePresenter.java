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
package com.codenvy.ide.part.base;

import com.codenvy.ide.api.ui.perspective.AbstractPartPresenter;
import com.codenvy.ide.api.ui.perspective.PartStack;

/**
 * Base presenter for parts that support minimizing by part toolbar button.
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public abstract class BasePresenter extends AbstractPartPresenter implements BaseActionDelegate {

    protected PartStack partStack;

    protected BasePresenter() {
    }

    /** {@inheritDoc} */
    @Override
    public void minimize() {
        if (partStack != null)
            partStack.hidePart(this);
    }

    /**
     * Set PartStack where this part added.
     * @param partStack
     */
    public void setPartStack(PartStack partStack) {
        this.partStack = partStack;
    }
}
