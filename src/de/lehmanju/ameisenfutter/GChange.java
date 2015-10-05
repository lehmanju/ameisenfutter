package de.lehmanju.ameisenfutter;

import javafx.scene.image.ImageView;

public class GChange
{
    ImageView view;
    boolean draw;

    public GChange(ImageView v, boolean d)
    {
        draw = d;
        view = v;
    }
}
