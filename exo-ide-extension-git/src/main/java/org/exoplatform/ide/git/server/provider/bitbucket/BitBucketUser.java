package org.exoplatform.ide.git.server.provider.bitbucket;

/**
 * POJO of BitBucket current user information
 */
public class BitBucketUser {
    private String  username;
    private String  first_name;
    private String  last_name;
    private String  display_name;
    private boolean is_team;
    private String  avatar;
    private String  resource_uri;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public boolean getIs_team() {
        return is_team;
    }

    public void setIs_team(boolean is_team) {
        this.is_team = is_team;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getResource_uri() {
        return resource_uri;
    }

    public void setResource_uri(String resource_uri) {
        this.resource_uri = resource_uri;
    }
}
