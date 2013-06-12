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
package com.codenvy.ide.factory.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Interface represent the constants contained in the resource bundle: 'FactoryLocalizationConstants.properties'.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FactoryLocalizationConstants.java Jun 11, 2013 12:02:56 PM azatsarynnyy $
 */
public interface FactoryLocalizationConstants extends Messages {
    /*
     * Controls
     */
    @Key("control.shareWithFactoryURL.id")
    String shareWithFactoryURLControlId();

    @Key("control.shareWithFactoryURL.title")
    String shareWithFactoryURLControlTitle();

    @Key("control.shareWithFactoryURL.prompt")
    String shareWithFactoryURLControlPrompt();

    /*
     * FactoryURLView
     */
    @Key("factoryURL.view.id")
    String factoryURLViewId();

    @Key("factoryURL.view.title")
    String factoryURLViewTitle();

    @Key("factoryURL.field.url")
    String factoryURLFieldURL();

    @Key("factoryURL.button.ok")
    String factoryURLButtonOk();

    /*
     * CommitChangesView
     */
    @Key("commitChanges.view.id")
    String commitChangesViewId();

    @Key("commitChanges.view.title")
    String commitChangesViewTitle();

    @Key("commitChanges.field.description")
    String commitChangesFieldDescription();

    @Key("commitChanges.button.ok")
    String commitChangesButtonOk();

    @Key("commitChanges.button.continue")
    String commitChangesButtonContinue();

}
