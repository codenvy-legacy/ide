package com.codenvy.ide.ext.git.shared;

import java.util.List;

/**
 * Information about Git service which support operation for specific git url.
 * Explanation: if we have git url like: "git@bitbucket.org:user/reponame.git" than we send to server request for getting information
 * about provider that should process this url, than we try to search through all enabled Git services that service, which matches for
 * pattern, and in response to client we send all necessary information about this provider(e.g. its base host, short name, authorization
 * scopes if it support authorization via oauth and if our url is ssh url) and client based on this info can call authorization for user.
 */
public class GitUrlVendorInfo {
    private String       vendorName;
    private String       vendorBaseHost;
    private List<String> oauthScopes;
    private boolean      givenUrlSSH;

    public GitUrlVendorInfo() {
    }

    public GitUrlVendorInfo(String vendorName, String vendorBaseHost, List<String> oauthScopes, boolean givenUrlSSH) {
        this.vendorName = vendorName;
        this.vendorBaseHost = vendorBaseHost;
        this.oauthScopes = oauthScopes;
        this.givenUrlSSH = givenUrlSSH;
    }

    /**
     * Get short Git service name.
     *
     * @return short service name
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Get base Git service host.
     *
     * @return base Git service host
     */
    public String getVendorBaseHost() {
        return vendorBaseHost;
    }

    /**
     * If current url is SSH.
     *
     * @return true is current url is SSH
     */
    public boolean isGivenUrlSSH() {
        return givenUrlSSH;
    }

    /**
     * Get list of authorization scopes for specific Git service.
     *
     * @return list of scopes
     */
    public List<String> getOAuthScopes() {
        return oauthScopes;
    }

    /**
     * Set short Git service name.
     *
     * @param vendorName
     *         short service name
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * Set base Git service host.
     *
     * @param vendorBaseHost
     *         base Git service host
     */
    public void setVendorBaseHost(String vendorBaseHost) {
        this.vendorBaseHost = vendorBaseHost;
    }

    /**
     * Set list of authorization scopes for specific Git service.
     *
     * @param oauthScopes
     *         list of scopes
     */
    public void setOauthScopes(List<String> oauthScopes) {
        this.oauthScopes = oauthScopes;
    }

    /**
     * Set if current url is SSH.
     *
     * @param givenUrlSSH
     *         true is current url is SSH
     */
    public void setGivenUrlSSH(boolean givenUrlSSH) {
        this.givenUrlSSH = givenUrlSSH;
    }
}
