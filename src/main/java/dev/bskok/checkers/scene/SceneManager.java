package dev.bskok.checkers.scene;

import java.util.HashMap;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

// TODO(bskok): not used anywhere yet
public class SceneManager {
  private final HashMap<String, Pane> screenMap = new HashMap<>();
  private final Scene main;

  public SceneManager(Scene main) {
    this.main = main;
  }

  public void addScreen(String name, Pane pane) {
    screenMap.put(name, pane);
  }

  public void removeScreen(String name) {
    screenMap.remove(name);
  }

  public void activate(String name) {
    main.setRoot(screenMap.get(name));
  }
}
