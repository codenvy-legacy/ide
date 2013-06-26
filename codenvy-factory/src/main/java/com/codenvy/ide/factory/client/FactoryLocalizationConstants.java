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
     * Buttons
     */
    @Key("button.ok")
    String buttonOk();

    @Key("button.continue")
    String buttonContinue();

    @Key("button.send")
    String buttonSend();

    @Key("button.cancel")
    String buttonCancel();

    /*
     * Messages
     */
    @Key("messages.checkRepoStatusFailed")
    String checkRepoStatusFailed();

    /*
     * Controls
     */
    @Key("control.factoryURL.id")
    String factoryURLControlId();

    @Key("control.factoryURL.title")
    String factoryURLControlTitle();

    @Key("control.factoryURL.prompt")
    String factoryURLControlPrompt();

    /*
     * FactoryURLView
     */
    @Key("factoryURL.view.id")
    String factoryURLViewId();

    @Key("factoryURL.view.title")
    String factoryURLViewTitle();

    @Key("factoryURL.field.showCounter.title")
    String factoryURLFieldShowCounterTitle();

    @Key("factoryURL.field.position.title")
    String factoryURLFieldPositionTitle();

    @Key("factoryURL.field.verticalMode.title")
    String factoryURLFieldVerticalModeTitle();

    @Key("factoryURL.field.horizontalMode.title")
    String factoryURLFieldHorizontalModeTitle();

    @Key("factoryURL.field.websitesURL.id")
    String factoryURLFieldWebsitesURLId();

    @Key("factoryURL.field.gitHubURL.id")
    String factoryURLFieldGitHubURLId();

    @Key("factoryURL.field.directSharingURL.id")
    String factoryURLFieldDirectSharingURLId();

    @Key("factoryURL.button.ok.id")
    String factoryURLButtonOkId();

    /*
     * CommitChangesView
     */
    @Key("commitChanges.view.id")
    String commitChangesViewId();

    @Key("commitChanges.view.title")
    String commitChangesViewTitle();

    @Key("commitChanges.descriptionText")
    String commitChangesDescriptionText();

    @Key("commitChanges.field.description.id")
    String commitChangesFieldDescriptionId();

    @Key("commitChanges.field.description.title")
    String commitChangesFieldDescriptionTitle();

    @Key("commitChanges.button.ok.id")
    String commitChangesButtonOk();

    @Key("commitChanges.button.continue.id")
    String commitChangesButtonContinue();

    /*
     * SendMailView
     */
    @Key("sendMail.view.id")
    String sendMailViewId();

    @Key("sendMail.view.title")
    String sendMailViewTitle();

    @Key("sendMail.field.recipient.id")
    String sendMailFieldRecipientId();

    @Key("sendMail.field.recipient.title")
    String sendMailFieldRecipientTitle();

    @Key("sendMail.field.message.id")
    String sendMailFieldMessageId();

    @Key("sendMail.field.message.title")
    String sendMailFieldMessageTitle();

    @Key("sendMail.button.send.id")
    String sendMailButtonSend();

    @Key("sendMail.button.cancel.id")
    String sendMailButtonCancel();
}
