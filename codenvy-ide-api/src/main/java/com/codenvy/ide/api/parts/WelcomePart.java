/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.parts;

import com.codenvy.ide.api.ui.workspace.PartPresenter;

import javax.validation.constraints.NotNull;

/**
 * Part containing Welcome Page
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface WelcomePart extends PartPresenter {
    /**
     * Add item on welcome page.
     *
     * @param action
     *         action what need to execute when current item is clicked
     */
    void addItem(@NotNull WelcomeItemAction action);
}