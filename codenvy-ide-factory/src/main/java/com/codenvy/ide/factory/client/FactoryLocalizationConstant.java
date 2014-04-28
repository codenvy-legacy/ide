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
