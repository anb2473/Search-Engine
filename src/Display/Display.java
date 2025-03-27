package Display;

import Display.Input.Keys;
import Display.Input.Mouse;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.Random;

public class Display {
    private JFrame frame;
    private Canvas canvas;

    private final int width,  height;

    private Mouse mouseListener;
    private Keys keyListener;

    public BufferStrategy BS;
    private Graphics g;

    private Image iconImage;

    private String title = "";

    public Display(String title, int width, int height, Image iconImage, boolean subWindow, boolean resizable) throws Exception {
        this.width = width;
        this.height = height;
        this.iconImage = iconImage;
        if (title != null){
            this.title = title;
        }

        CreateDisplay(subWindow, resizable);
    }

    private void CreateDisplay(boolean subWindow, boolean resizable) throws IOException {
        frame = new JFrame();

        frame.setSize(width, height);

        mouseListener = new Mouse();
        keyListener = new Keys();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (subWindow) {
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

        frame.setResizable(resizable);

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        frame.setIconImage(iconImage);

        frame.setTitle(title);

        canvas = new Canvas();

        frame.setPreferredSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(487, 353));

        canvas.setFocusable(false);

        frame.add(canvas);

        frame.addMouseListener(mouseListener);
        frame.addMouseMotionListener(mouseListener);
        frame.addKeyListener(keyListener);

        canvas.addMouseListener(mouseListener);
        canvas.addMouseMotionListener(mouseListener);
        canvas.addKeyListener(keyListener);

        frame.pack();
    }

    public Graphics graphicsInit(){
        BS = canvas.getBufferStrategy();

        if(BS == null) {
            canvas.createBufferStrategy(3);
            BS = canvas.getBufferStrategy();
        }

        g = BS.getDrawGraphics();

        g.clearRect(0, 0, frame.getWidth(), frame.getHeight());

        g.setColor(new Color(255, 255, 255));
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());

        return g;
    }

    public Graphics2D graphicsInit(Color color){
        BS = canvas.getBufferStrategy();

        if(BS == null) {
            canvas.createBufferStrategy(3);
            BS = canvas.getBufferStrategy();
        }

        g = BS.getDrawGraphics();

        g.clearRect(0, 0, frame.getWidth(), frame.getHeight());

        g.setColor(color);
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());

        return (Graphics2D) g;
    }


    public void render() {
        BS.show();
        g.dispose();
    }

    public void changeTitle(String title){frame.setTitle(title);}

    public boolean mouseCollide(Rectangle rect){return mouseListener.mouseCollide(rect);}

    public int getMouseX(){return mouseListener.mouseX;}

    public int getMouseY(){return mouseListener.mouseY;}

    public boolean getMousePressed(){return mouseListener.mousePressed;}

    public boolean[] getKeys(){return keyListener.getKeys();}

    public Canvas getCanvas() {return canvas;}

    public JFrame getFrame() {return frame;}

    public int getFrameWidth(){return frame.getWidth();}

    public int getFrameHeight(){return frame.getHeight();}

    public Mouse getMouseListener(){return mouseListener;}
}
