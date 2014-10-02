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
package com.codenvy.ide.actions.find;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action fo find action action
 *
 * @author Evgen Vidolob
 */
@Singleton
public class FindActionAction extends Action {

    private       FindActionPresenter  presenter;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public FindActionAction(FindActionPresenter presenter, AnalyticsEventLogger eventLogger) {
        super("Find Action", "Find Action by name");
        this.presenter = presenter;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        presenter.show();
    }
}
