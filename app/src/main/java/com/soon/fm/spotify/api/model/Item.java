package com.soon.fm.spotify.api.model;

import java.util.List;

public interface Item {

    String getTitle();

    String getSubTitle();

    String getUri();

    List<Image> getImages();

}
