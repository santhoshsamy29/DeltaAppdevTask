package com.example.application.deltatasktwo;

import android.net.Uri;

public class CardView {

    int position;
    String caption;
    Uri image;

    public CardView(int position, String caption, Uri image) {
        this.position = position;
        this.caption = caption;
        this.image = image;
    }
}
