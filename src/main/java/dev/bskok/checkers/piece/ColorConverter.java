package dev.bskok.checkers.piece;

import java.lang.reflect.Field;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorConverter {
  public static final Logger log = LoggerFactory.getLogger(ColorConverter.class);

  public static String getColorName(Color color) {
    try {
      for (Field field : Color.class.getFields()) {
        if (field.getType() == Color.class && field.get(null).equals(color)) {
          return field.getName().toUpperCase();
        }
      }
    } catch (IllegalAccessException e) {
      log.error("Failed to convert color to name string: {}", e.getMessage());
    }
    return "CUSTOM COLOR";
  }
}
