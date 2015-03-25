/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.actions;

import com.google.inject.Inject;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.about.AboutLocalizationConstant;
import org.eclipse.che.ide.about.AboutPresenter;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.statepersisting.promises.TestPromises;
import org.eclipse.che.ide.toolbar.PresentationFactory;
import org.eclipse.che.ide.util.Pair;
import org.eclipse.che.ide.util.loging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Action for showing About application information.
 *
 * @author Ann Shumilova
 */
public class ShowAboutAction extends Action {

    private final AboutPresenter       presenter;
    private final AnalyticsEventLogger eventLogger;
    private final TestPromises         testPromises;
    private final ActionManager        actionManager;
    private final PresentationFactory  presentationFactory;
    private final OpenProjectAction    openProjectAction;
    private final OpenFileAction openFileAction;

    @Inject
    public ShowAboutAction(AboutPresenter presenter,
                           AboutLocalizationConstant locale,
                           AnalyticsEventLogger eventLogger,
                           Resources resources,
                           TestPromises testPromises,
                           ActionManager actionManager,
                           PresentationFactory presentationFactory,
                           OpenProjectAction openProjectAction,
                           OpenFileAction openFileAction) {
        super("Make Promise", "Show about application", null, resources.about());
        this.presenter = presenter;
        this.eventLogger = eventLogger;
        this.testPromises = testPromises;
        this.actionManager = actionManager;
        this.presentationFactory = presentationFactory;
        this.openProjectAction = openProjectAction;
        this.openFileAction = openFileAction;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
//        eventLogger.log(this);
//        presenter.showAbout();

        List<Pair<Action, ActionEvent>> actions = new ArrayList<>();

        final Presentation presentation = presentationFactory.getPresentation(openFileAction);
        ActionEvent e1 = new ActionEvent("", presentation, actionManager, 0, Collections.singletonMap("file", "pom.xml"));
        ActionEvent e2 = new ActionEvent("", presentation, actionManager, 0, Collections.singletonMap("file", "src/main/webapp/index.jsp"));
        ActionEvent e3 = new ActionEvent("", presentation, actionManager, 0, Collections.singletonMap("file", "src/main/java/com/codenvy/example/spring/GreetingController.java"));
        ActionEvent e4 = new ActionEvent("", presentation, actionManager, 0, Collections.singletonMap("file", "src/main/webapp/WEB-INF/web.xml"));
        actions.add(new Pair<Action, ActionEvent>(openFileAction, e1));
        actions.add(new Pair<Action, ActionEvent>(openFileAction, e2));
        actions.add(new Pair<Action, ActionEvent>(openFileAction, e3));
        actions.add(new Pair<Action, ActionEvent>(openFileAction, e4));

        Promise<Void> promise = actionManager.performActions(actions);
        promise.then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                Log.info(ShowAboutAction.class, "done");
            }
        });
    }

}
