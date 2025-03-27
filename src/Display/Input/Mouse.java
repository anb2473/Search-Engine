package Display.Input;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener {

    public int mouseX, mouseY;
    public boolean mousePressed = false;

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {mousePressed = false;}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(@NotNull MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public boolean mouseCollide(@NotNull Rectangle rect) {
        return rect.contains(new Point(mouseX, mouseY));
    }

    public boolean mouseCollideAndPress(@NotNull Rectangle rect) {
        return rect.contains(new Point(mouseX, mouseY)) && mousePressed;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
}
