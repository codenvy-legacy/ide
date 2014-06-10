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
package com.codenvy.ide.ext.tutorials.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants. Interface represents the constants contained in resource bundle:
 * 'TutorialsLocalizationConstant.properties'.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: TutorialsLocalizationConstant.java Sep 16, 2013 12:40:17 PM azatsarynnyy $
 */
public interface TutorialsLocalizationConstant extends Messages {
    /* Actions */
    @Key("control.showTutorialGuide.id")
    String showTutorialGuideActionId();

    @Key("control.showTutorialGuide.text")
    String showTutorialGuideActionText();

    @Key("control.showTutorialGuide.description")
    String showTutorialGuideActionDescription();
}
