package com.soon.fm.spotify.api.model;

import java.io.Serializable;
import java.util.List;

public interface Item extends Serializable {

    String getTitle();

    String getSubTitle();

    String getUri();

    List<Image> getImages();

}
