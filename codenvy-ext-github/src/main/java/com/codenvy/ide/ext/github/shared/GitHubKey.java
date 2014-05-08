package com.codenvy.ide.ext.github.shared;

import com.codenvy.dto.shared.DTO;

/**
 * GitHub SSH key, taken from API v3.
 *
 * @author Vladyslav Zhukovskii
 */
@DTO
public interface GitHubKey {
    int getId();

    void setId(int id);

    String getKey();

    void setKey(String key);

    String getUrl();

    void setUrl(String url);

    String getTitle();

    void setTitle(String title);
}
