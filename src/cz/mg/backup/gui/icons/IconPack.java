package cz.mg.backup.gui.icons;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.backup.exceptions.Category;
import cz.mg.collections.map.Map;

import javax.swing.*;
import java.awt.*;

public @Component class IconPack {
    private static final @Mandatory Color ERROR_COLOR = new Color(0, 0, 0);
    private static final @Mandatory Color PROBLEM_COLOR = new Color(255, 0, 0);
    private static final @Mandatory Color WARNING_COLOR = new Color(192, 192, 0);
    private static final @Mandatory Color INFORMATION_COLOR = new Color(128, 128, 255);

    private final @Mandatory Map<Category, Icon> map = new Map<>();
    private final @Mandatory IconCompositor compositor;
    private final @Mandatory ImageIcon main;
    private final @Mandatory ImageIcon secondary;

    public IconPack(@Mandatory IconCompositor compositor, @Mandatory ImageIcon main, @Mandatory ImageIcon secondary) {
        this.compositor = compositor;
        this.main = main;
        this.secondary = secondary;
    }

    public @Mandatory Icon get(@Optional Category category) {
        return map.getOrCreate(category, () -> create(category));
    }

    private @Mandatory Icon create(@Optional Category category) {
        return category != null
            ? compositor.combine(main, secondary, convert(category), 13, 13)
            : main;
    }

    private @Mandatory Color convert(@Mandatory Category category) {
        return switch (category) {
            case ERROR -> ERROR_COLOR;
            case PROBLEM -> PROBLEM_COLOR;
            case WARNING -> WARNING_COLOR;
            case INFORMATION -> INFORMATION_COLOR;
        };
    }
}
