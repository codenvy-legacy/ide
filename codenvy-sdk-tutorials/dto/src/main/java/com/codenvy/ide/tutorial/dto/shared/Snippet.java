package com.codenvy.ide.tutorial.dto.shared;

import com.codenvy.ide.dto.DTO;

@DTO
public interface Snippet {
    String getDescription();

    boolean isPublic();

    Files getFiles();
}
