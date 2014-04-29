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
    public String oauthLoginPrompt(String host);

    @Key("accept.oauth.failed.to.get.current.loggedin.user")
    public String oauthFailedToGetCurrentLoggedInUser();

    @Key("accept.need.to.authorize")
    public String needToAuthorize();
    
    @Key("factory.url.action")
    public String factoryUrlAction();
    
    @Key("share.view.title")
    public String shareViewTitle();
    
    @Key("factory.view.title")
    public String factoryViewTitle();
    
    @Key("nonencoded.url.title")
    public String nonEncodedUrlTitle();
    
    @Key("encoded.url.title")
    public String encodedUrlTitle();
    
    @Key("html.button")
    public String htmlButton();
    
    @Key("github.button")
    public String githubButton();
    
    @Key("iframe.button")
    public String iframeButton();
    
    @Key("social.button")
    public String socialButton();
    
}
