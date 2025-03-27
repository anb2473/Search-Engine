package Display.Input;

import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class Keys implements KeyListener {

    private boolean[] keys;

    public Keys() {
        keys = new boolean[256];
    }

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(@NotNull KeyEvent e) {try{keys[e.getKeyCode()] = true;}catch (Exception ex){}}

    public void keyReleased(@NotNull KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public boolean[] getKeys() {
        return keys;
    }
}
