package com.soon.fm.spotify.api.model;

import java.util.List;

public interface Results<I extends Item> {
    List<I> getItems();

    String getNext();
}
