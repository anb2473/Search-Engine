import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import static java.lang.Math.ceil;
import static java.lang.Math.pow;

public class CreateBTMImage {
    public static @NotNull BufferedImage createBTMImage(String filePath, int width, int height) throws Exception {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics g = img.getGraphics();

        FileReader testReader;

        try{
            testReader = new FileReader(filePath);
        } catch(FileNotFoundException fnf){
            throw new FileNotFoundException("Could Not Find Specified File: " + filePath);
        }

        BufferedReader flbr = new BufferedReader(testReader);

        String firstLine = flbr.readLine();

        boolean bitMapFormat = firstLine.charAt(0) == '1';

        drawBTM(filePath, width, height, g, bitMapFormat, firstLine, flbr);

        return img;
    }

    private static void drawBTM(String path, int width, int height, Graphics g, boolean bitMapFormat, String firstLine, BufferedReader flbr) throws Exception {
        int lineNumber = 0;

        double pixelWidth;
        double pixelHeight;

        if (bitMapFormat){
            if (firstLine.length() < 13)
                throw new Exception("File Doesn't Have Valid Number Of Characters: " + firstLine.length());

            pixelWidth = ((double) width / ((double) (firstLine.length() - 1) / 6)) * 2;

            int lines = 1;
            while (flbr.readLine() != null) lines++;

            pixelHeight = (double) height / lines;
        }
        else{
            if (firstLine.length() < 7)
                throw new Exception("Invalid Number Of Characters In Image File: " + firstLine.length());

            pixelWidth = ((double) width / ((double) (firstLine.length() - 1) / 3)) * 2;

            int lines = 1;
            while (flbr.readLine() != null) lines++;

            pixelHeight = (double) height / lines;
        }

        FileReader reader;

        try{
            reader = new FileReader(path);
        } catch(FileNotFoundException fnf){
            throw new FileNotFoundException("Could Not Find Specified File: " + path);
        }

        BufferedReader br = new BufferedReader(reader);

        try{
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                lineNumber++;

                int innerIndx = 0;
                LinkedList<Integer> RGBAValues = new LinkedList<Integer>();

                int start = 0;
                if (lineNumber == 1)
                    start = 1;

                int RGBAValue = 0;

                for (int i = start; i < line.length() + 1; i++) {
                    String letter;

                    try{
                        letter = Character.toString(line.charAt(i));
                    } catch(IndexOutOfBoundsException e){
                        letter = "";
                    }

                    innerIndx++;

                    if (bitMapFormat){
                        if (innerIndx > 3){
                            RGBAValues.add(RGBAValue);
                            RGBAValue = 0;
                            innerIndx = 1;

                            if (RGBAValues.size() == 4){
                                g.setColor(new Color(RGBAValues.get(0), RGBAValues.get(1), RGBAValues.get(2), RGBAValues.get(3)));
                                g.fillRect((int) ceil(((((double) i / 12) - 1) * pixelWidth)), (int) ceil(((lineNumber - 1) * pixelHeight)), (int) ceil(pixelWidth), (int) ceil(pixelHeight));

                                RGBAValues.clear();
                            }
                        }

                        RGBAValue += getSingleDigitNumber(letter) * (int) pow(10, 3 - innerIndx);
                    }
                    else{
                        if (innerIndx > 3){
                            RGBAValues.add(RGBAValue);
                            RGBAValue = 0;
                            innerIndx = 1;

                            if (RGBAValues.size() == 2){
                                g.setColor(new Color(RGBAValues.get(0), RGBAValues.get(0), RGBAValues.get(0), RGBAValues.get(1)));
                                g.fillRect((int) ceil(((((double) i / 6) - 1) * pixelWidth)), (int) ceil(((lineNumber - 1) * pixelHeight)), (int) ceil(pixelWidth), (int) ceil(pixelHeight));

                                RGBAValues.clear();
                            }
                        }

                        RGBAValue += getSingleDigitNumber(letter) * (int) pow(10, 3 - innerIndx);
                    }
                }
            }
        }
        catch(IOException io){
            throw new IOException("IO Exception: Check That The Specified File Exists and Application Has Permission");
        }

        reader.close();
    }

    @Contract(pure = true)
    private static int getSingleDigitNumber(@NotNull String number){
        int result = 0;

        switch (number){
            case "0" -> {}
            case "1" -> result = 1;
            case "2" -> result = 2;
            case "3" -> result = 3;
            case "4" -> result = 4;
            case "5" -> result = 5;
            case "6" -> result = 6;
            case "7" -> result = 7;
            case "8" -> result = 8;
            case "9" -> result = 9;
        }

        return result;
    }
}
