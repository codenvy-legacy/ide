/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
 * @author vzhukovskii@codenvy.com
 */
public interface FactoryLocalizationConstant extends Messages {

    /** Accept */
    @Key("accept.get.information.about.factory")
    public String getInformationAboutFactory();

    @Key("accept.accepting.incoming.factory.url")
    public String acceptIncomingFactoryURL();

    @Key("accept.factory.url.accepted.successfully")
    public String factoryURLAcceptedSuccessfully();

    @Key("accept.unable.to.set.project.type")
    public String unableToSetProjectType();

    @Key("accept.project.imported")
    public String projectImported(String projectName);

    @Key("accept.oauth.login.prompt")
    public String oAuthLoginPrompt(String host);


    @Key("accept.oauth.login.title")
    public String oAuthLoginTitle();

    @Key("accept.oauth.failed.to.get.current.loggedin.user")
    public String oauthFailedToGetCurrentLoggedInUser();

    @Key("accept.need.to.authorize")
    public String needToAuthorize();
}
