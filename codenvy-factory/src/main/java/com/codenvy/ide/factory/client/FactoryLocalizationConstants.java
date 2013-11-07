/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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

    @Key("messages.saveAllChangesBeforeCopying")
    String saveAllChangesBeforeCopying();

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

    @Key("sendMail.field.message.content")
    String sendMailFieldMessageEntry(String projectName, String factoryUrl, String senderName, String senderEmail);

    @Key("sendMail.button.send.id")
    String sendMailButtonSend();

    @Key("sendMail.button.cancel.id")
    String sendMailButtonCancel();

    @Key("sendMail.error.get.profile")
    String sendMailErrorGettingProfile();

    /**
     * Private repositories
     */
    @Key("privateRepo.needAuth.title")
    String privateRepoNeedAuthTitle();

    @Key("privateRepo.needAuth.content")
    String privateRepoNeedAuthContent(String gitProvider);

    @Key("privateRepo.Auth.Failed")
    String privateRepoAuthFailed();

    @Key("privateRepo.Auth.Permitted")
    String privateRepoAuthPermitted();
}
