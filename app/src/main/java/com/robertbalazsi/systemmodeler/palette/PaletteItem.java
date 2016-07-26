package com.robertbalazsi.systemmodeler.palette;

import javafx.scene.image.Image;
import javafx.scene.shape.Shape;
import lombok.Getter;
import lombok.Setter;

/**
 * Defines an item in the controls palette.
 */
public class PaletteItem {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Image icon;

    @Getter
    @Setter
    private Shape shape;

    public PaletteItem(String name, Image icon, Shape shape) {
        this.name = name;
        this.icon = icon;
        this.shape = shape;
    }
}
