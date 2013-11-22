package org.exoplatform.ide.extension.ssh.shared;

import java.util.List;

/**
 * POJO model for List of SSH key items.
 */
public interface ListKeyItem {
    List<KeyItem> getKeys();

    void setKeys(List<KeyItem> keys);
}
