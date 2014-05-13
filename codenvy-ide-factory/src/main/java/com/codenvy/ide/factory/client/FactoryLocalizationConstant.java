package com.codenvy.ide.factory.client;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

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
    
    @Key("generate.encoded.url.button")
    public String generateEncodedUrlButton();
    
    @Key("configure.title")
    public String configureTitle();
    
    @Key("open.file.label")
    public String openFileLabel();
    
    @Key("open.file.help")
    public SafeHtml openFileHelp();
    
    @Key("find.and.replace.label")
    public String findAndReplaceLabel();
    
    @Key("find.and.replace.help")
    public SafeHtml findAndReplaceHelp();
    
    @Key("description.label")
    public String descriptionLabel();
    
    @Key("description.help")
    public SafeHtml descriptionHelp();
    
    @Key("author.label")
    public String authorLabel();
    
    @Key("author.help")
    public SafeHtml authorHelp();
    
    @Key("expiration.date.label")
    public String expirationDateLabel();
    
    @Key("expiration.date.help")
    public SafeHtml expirationDateHelp();
    
    @Key("style.label")
    public String styleLabel();
    
    @Key("vertical.align.label")
    public String verticalAlignLabel();
    
    @Key("horizontal.align.label")
    public String horizontalAlignLabel();
    
    @Key("dark.style.label")
    public String darkStyleLabel();
    
    @Key("white.style.label")
    public String whiteStyleLabel();
    
    @Key("projects.number.label")
    public String projectsNumberLabel();
}
