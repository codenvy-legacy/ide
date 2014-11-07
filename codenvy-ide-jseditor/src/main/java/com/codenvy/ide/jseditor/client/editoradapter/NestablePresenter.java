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
package com.codenvy.ide.jseditor.client.editoradapter;

import javax.annotation.Nonnull;

import com.codenvy.ide.api.mvp.Presenter;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Kind of presenter that can be nested inside an {@link EditorAdapter}.
 */
public interface NestablePresenter extends Presenter, HasEditor {

    /**
     * Action to do when closing the component.
     * @param callback the callback
     */
    void onClose(@Nonnull final AsyncCallback<Void> callback);
}
