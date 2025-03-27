import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Objects;

public class Loader {
    public static @NotNull HashMap<String, LinkedList<Object>> loadPage(String url, String innerName, String baseUrl, boolean isDarkMode) throws Exception {
        if (innerName == null)
            innerName = "index";

        LinkedHashMap<String, Object> pageData = mapIDs(url + "\\" + innerName + ".swq");

        String styleName = (String) pageData.get("&");

        if (styleName == null)
            styleName = "styles";

        HashMap<String, Object> styles = mapStyles(url + "\\" + styleName + ".sbq");

        HashMap<String, LinkedList<Object>> result = buildPage(pageData, styles, baseUrl + "\\OneDrive\\AuroraSearchEngine\\", url, isDarkMode);
        return result;
    }

    private static @NotNull HashMap<String, LinkedList<Object>> buildPage(@NotNull LinkedHashMap<String, Object> pageData, HashMap<String, Object> styles, String url, String baseUrl, boolean isDarkMode) throws Exception {
        HashMap<String, LinkedList<Object>> result = new HashMap<>();

        result.put("global", buildPageLevel("global", pageData, styles, false, 0, 0, url, 1500, baseUrl, isDarkMode));

        for (String spaceLevel : styles.keySet()){
            if (spaceLevel.equals("global"))
                continue;

            int screenWidth = 1500;
            if (spaceLevel.startsWith("width"))
                screenWidth = Integer.parseInt(spaceLevel.split("<")[1]);

            result.put(spaceLevel, buildPageLevel(spaceLevel, pageData, styles, false, 0, 0, url, screenWidth, baseUrl, isDarkMode));
        }

        return result;
    }

    public static BufferedImage crop(@NotNull BufferedImage image) {
        int maxY = 0;
        boolean isBlank, minYIsDefined = false;
        Raster raster = image.getRaster();

        for (int y = 0; y < image.getHeight(); y++) {
            isBlank = true;

            for (int x = 0; x < image.getWidth(); x++)
                if (raster.getPixel(x, y, (int[]) null)[3] != 0)
                    isBlank = false;

            if (!isBlank)
                if (!minYIsDefined)
                    minYIsDefined = true;
                else
                    if (y > maxY) maxY = y;
        }

        return image.getSubimage(0, 0, image.getWidth(), maxY + 1);
    }

    @SuppressWarnings("unchecked")
    private static @NotNull LinkedList<Object> buildPageLevel(String levelName, @NotNull LinkedHashMap<String, Object> pageData, @NotNull HashMap<String, Object> styles, boolean interpretAxis, int startX, int startY, String url, int screenWidth, String projectUrl, boolean isDarkMode) throws Exception {
        int offset = 0;

        BufferedImage result = new BufferedImage(screenWidth, 5000, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = (Graphics2D) result.getGraphics();

        HashMap<String, Object> globalLevel = (HashMap<String, Object>) styles.get("global");

        HashMap<String, Object> localLevel = (HashMap<String, Object>) styles.get(levelName);

        HashMap<String, LinkedList<Integer>> links = new HashMap<>();

        for (String id : pageData.keySet()){
            HashMap<String, String> info = new HashMap<>();

            try{
                info.putAll((HashMap<String, String>) globalLevel.get(id));
            } catch (NullPointerException e){}

            try{
                info.putAll((HashMap<String, String>) localLevel.get(id));
            }
            catch (NullPointerException e){}

            if (id.startsWith("&"))
                continue;

            if (!id.startsWith("#")){
                int top = 0;
                if (info.get("top") != null)
                    if (interpretAxis)
                        top = Integer.parseInt(info.get("top"));
                    else
                        offset += Integer.parseInt(info.get("top"));

                int bottom = 0;
                if (info.get("bottom") != null)
                    bottom = Integer.parseInt(info.get("bottom"));

                int right = 0;
                if (info.get("right") != null)
                    right = Integer.parseInt(info.get("right"));

                int left = 0;
                if (info.get("left") != null)
                    if (interpretAxis)
                        offset += Integer.parseInt(info.get("left"));
                    else
                        left = Integer.parseInt(info.get("left"));

                Object width = info.get("width");
                if (width == null)
                    width = 1500;
                else
                    width = Integer.parseInt(width.toString());

                Object height = info.get("height");
                if (height == null)
                    height = 1500;
                else
                    height = Integer.parseInt(width.toString());

                if (info.get("background-image") != null){
                    if (interpretAxis){
                        BufferedImage btmImage = CreateBTMImage.createBTMImage(projectUrl + "\\" + info.get("background-image"), (int) width, (int) height);
                        g.drawImage(btmImage, offset, top - bottom, null);
                    }
                    else{
                        BufferedImage btmImage = CreateBTMImage.createBTMImage(projectUrl + "\\" + info.get("background-image"), (int) width, (int) height);
                        g.drawImage(btmImage,left - right + startX, offset + startY, null);
                    }
                }

                String color = info.get("background");
                if (color != null){
                    switch (color){
                        case "white" -> {
                            if (!isDarkMode) g.setColor(Color.white);
                            else g.setColor(Color.black);
                        }
                        case "black" -> {
                            if (isDarkMode) g.setColor(Color.white);
                            else g.setColor(Color.black);
                        }
                        case "gray" -> g.setColor(Color.gray);
                        case "blue" -> g.setColor(Color.blue);
                        case "red" -> g.setColor(Color.red);
                        case "cyan" -> g.setColor(Color.cyan);
                        case "dark-gray" -> g.setColor(Color.darkGray);
                        case "light-gray" -> g.setColor(Color.lightGray);
                        case "magenta" -> g.setColor(Color.magenta);
                        case "green" -> g.setColor(Color.green);
                        case "orange" -> g.setColor(Color.orange);
                        case "yellow" -> g.setColor(Color.yellow);
                        case "pink" -> g.setColor(Color.pink);
                    }

                    String transparency = info.get("background-transparency");
                    if (transparency != null)
                        g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), Integer.parseInt(transparency)));

                    if (info.get("border-radius") != null){
                        if (interpretAxis)
                            g.fillRoundRect(offset, top - bottom, (int) width, (int) height, Integer.parseInt(info.get("border-radius")), Integer.parseInt(info.get("border-radius")));
                        else
                            g.fillRoundRect(left - right + startX, offset + startY, (int) width, (int) height, Integer.parseInt(info.get("border-radius")), Integer.parseInt(info.get("border-radius")));
                        continue;
                    }

                    if (interpretAxis)
                        g.fillRect(offset, top - bottom, (int) width, (int) height);
                    else
                        g.fillRect(left - right + startX, offset + startY, (int) width, (int) height);
                }

                g.setStroke(new BasicStroke(1));

                String borderWidth = info.get("border");
                if (borderWidth!= null){
                    String borderColor = info.get("border-color");
                    switch (borderColor){
                        case "white" -> {
                            if (!isDarkMode) g.setColor(Color.white);
                            else g.setColor(Color.black);
                        }
                        case "black" -> {
                            if (isDarkMode) g.setColor(Color.white);
                            else g.setColor(Color.black);
                        }
                        case "gray" -> g.setColor(Color.gray);
                        case "blue" -> g.setColor(Color.blue);
                        case "red" -> g.setColor(Color.red);
                        case "cyan" -> g.setColor(Color.cyan);
                        case "dark-gray" -> g.setColor(Color.darkGray);
                        case "light-gray" -> g.setColor(Color.lightGray);
                        case "magenta" -> g.setColor(Color.magenta);
                        case "green" -> g.setColor(Color.green);
                        case "orange" -> g.setColor(Color.orange);
                        case "yellow" -> g.setColor(Color.yellow);
                        case "pink" -> g.setColor(Color.pink);
                    }

                    g.setStroke(new BasicStroke(Float.parseFloat(borderWidth)));

                    String transparency = info.get("border-transparency");
                    if (transparency != null)
                        g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), Integer.parseInt(transparency)));

                    if (info.get("border-radius") != null){
                        if (interpretAxis)
                            g.drawRoundRect(offset, top - bottom, (int) width, (int) height, Integer.parseInt(info.get("border-radius")), Integer.parseInt(info.get("border-radius")));
                        else
                            g.drawRoundRect(left - right + startX, offset + startY, (int) width, (int) height, Integer.parseInt(info.get("border-radius")), Integer.parseInt(info.get("border-radius")));
                        continue;
                    }

                    if (interpretAxis)
                        g.drawRect(offset, top - bottom, (int) width, (int) height);
                    else
                        g.drawRect(left - right + startX, offset + startY, (int) width, (int) height);
                }

                boolean innerAxis = Objects.equals(info.get("alignment"), "horizontal");

                if (interpretAxis){
                    LinkedList<Object> innerMaterial = buildPageLevel(levelName, (LinkedHashMap<String, Object>) pageData.get(id), styles, innerAxis, offset, top - bottom, url, screenWidth, projectUrl, isDarkMode);

                    links.putAll((HashMap<String, LinkedList<Integer>>) innerMaterial.getLast());

                    g.drawImage((BufferedImage) innerMaterial.getFirst(), offset + startX, top - bottom + startY, null);
                    offset += left + g.getFontMetrics().stringWidth((String) pageData.get(id));
                }
                else{
                    LinkedList<Object> innerMaterial = buildPageLevel(levelName, (LinkedHashMap<String, Object>) pageData.get(id), styles, innerAxis, left - right, offset, url, screenWidth, projectUrl, isDarkMode);

                    links.putAll((HashMap<String, LinkedList<Integer>>) innerMaterial.getLast());

                    g.drawImage((BufferedImage) innerMaterial.getFirst(), left - right + startX, offset + startY, null);
                    offset += bottom + ((BufferedImage) innerMaterial.getFirst()).getHeight();
                }

                continue;
            }

            String color = info.get("color");
            if (color != null)
                switch (color){
                    case "white" -> {
                        if (!isDarkMode) g.setColor(Color.white);
                        else g.setColor(Color.black);
                    }
                    case "black" -> {
                        if (isDarkMode) g.setColor(Color.white);
                        else g.setColor(Color.black);
                    }
                    case "gray" -> g.setColor(Color.gray);
                    case "blue" -> g.setColor(Color.blue);
                    case "red" -> g.setColor(Color.red);
                    case "cyan" -> g.setColor(Color.cyan);
                    case "dark-gray" -> g.setColor(Color.darkGray);
                    case "light-gray" -> g.setColor(Color.lightGray);
                    case "magenta" -> g.setColor(Color.magenta);
                    case "green" -> g.setColor(Color.green);
                    case "orange" -> g.setColor(Color.orange);
                    case "yellow" -> g.setColor(Color.yellow);
                    case "pink" -> g.setColor(Color.pink);
                }

            String transparency = info.get("transparency");
            if (transparency != null)
                g.setColor(new Color(g.getColor().getRed(), g.getColor().getGreen(), g.getColor().getBlue(), Integer.parseInt(transparency)));

            int fontSize;
            if (info.get("font-size") != null)
                fontSize = Integer.parseInt(info.get("font-size"));
            else
                fontSize = 20;

            String font = info.get("font");
            if (font == null)
                font = "Arial";

            Object fontWeight = info.get("font-weight");
            if (fontWeight == null)
                fontWeight = Font.PLAIN;
            else if (fontWeight == "plain")
                fontWeight = Font.PLAIN;
            else if (fontWeight == "bold")
                fontWeight = Font.BOLD;
            else if (fontWeight == "italic")
                fontWeight = Font.ITALIC;
            else if (fontWeight == "monospace")
                fontWeight = Font.MONOSPACED;
            else if (fontWeight == "serif")
                fontWeight = Font.SERIF;
            else if (fontWeight == "sans-serif")
                fontWeight = Font.SANS_SERIF;
            else if (fontWeight == "roman-baseline")
                fontWeight = Font.ROMAN_BASELINE;
            else if (fontWeight == "dialog")
                fontWeight = Font.DIALOG;
            else if (fontWeight == "hanging-baseline")
                fontWeight = Font.HANGING_BASELINE;
            else
                fontWeight = Font.PLAIN;

            if (font.startsWith("\\"))
                g.setFont(Font.createFont(Font.TRUETYPE_FONT, new File(url + "\\FontThemes" + font + ".ttf")).deriveFont(Font.PLAIN, fontSize));
            else
                g.setFont(new Font(font, (int) fontWeight, fontSize));

            int top = 0;
            if (info.get("top") != null)
                if (interpretAxis)
                    top = Integer.parseInt(info.get("top"));
                else
                    offset += Integer.parseInt(info.get("top"));

            int bottom = 0;
            if (info.get("bottom") != null)
                bottom = Integer.parseInt(info.get("bottom"));

            int right = 0;
            if (info.get("right") != null)
                right = Integer.parseInt(info.get("right"));

            int left = 0;
            if (info.get("left") != null)
                if (interpretAxis)
                    offset += Integer.parseInt(info.get("left"));
                else
                    left = Integer.parseInt(info.get("left"));

            if (info.get("qref") != null){
                LinkedList<Integer> positioningData = new LinkedList<>();

                if (interpretAxis){
                    positioningData.add(offset + startX);
                    positioningData.add(top - bottom + startY);
                }
                else {
                    positioningData.add(left - right + startX);
                    positioningData.add(offset + fontSize + startY);
                }

                positioningData.add(g.getFontMetrics().stringWidth((String) pageData.get(id)));
                positioningData.add(fontSize);

                links.put("u" + info.get("qref"), positioningData);
            }

            if (interpretAxis){
                g.drawString((String) pageData.get(id), offset + startX, top - bottom + startY);
                offset += left + g.getFontMetrics().stringWidth((String) pageData.get(id));
            }
            else{
                g.drawString((String) pageData.get(id), left - right + startX, offset + fontSize + startY);
                offset += bottom + fontSize;
            }
        }

        g.dispose();

        LinkedList<Object> finalResult = new LinkedList<>();
        finalResult.add(crop(result));
        finalResult.add(links);

        return finalResult;
    }

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    private static @NotNull HashMap<String, Object> mapStyles(String path) throws IOException {
        HashMap<String, Object> result = new HashMap<>();

        FileReader reader;

        try{
            reader = new FileReader(path);
        } catch(FileNotFoundException fnf){
            throw new FileNotFoundException("Could Not Find Specified File: " + path);
        }

        BufferedReader br = new BufferedReader(reader);

        String currentSpace = null;

        LinkedList<String> divLocation = new LinkedList<>();

        try {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (line.isEmpty())
                    continue;

                if (line.charAt(0) == '}'){
                    divLocation.clear();
                    continue;
                }

                if (line.charAt(0) == '@'){
                    currentSpace = line.substring(1).strip();
                    result.put(currentSpace, new HashMap<String, Object>());
                    continue;
                }

                if (line.charAt(0) == ' '){
                    HashMap<String, Object> space = (HashMap<String, Object>) result.get(currentSpace);

                    result.put(currentSpace, insertValue(divLocation, space, line));

                    continue;
                }

                String divName = line.strip().replace("{", "");
                ((HashMap<String, Object>) result.get(currentSpace)).put(divName, new HashMap<String, Object>());
                divLocation.add(divName);
            }
        } catch(IOException io) {
            throw new IOException("IO Exception: Check That The Specified File Exists and Application Has Permission");
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private static HashMap<String, Object> insertValue(LinkedList<String> divLocation, @NotNull HashMap<String, Object> space, String line){
        int divIndx = 0;
        for (Object div : space.keySet()){
            if (div == divLocation.get(divIndx)){
                divIndx++;

                if (divLocation.size() == 1){
                    ((HashMap<String, Object>) space.get(divLocation.getFirst())).put(line.split(":")[0].strip(), line.split(":")[1].strip());
                    break;
                }

                space = insertValue((LinkedList<String>) divLocation.subList(1, divLocation.size()), (HashMap<String, Object>) space.get(divLocation.getFirst()), line);
            }
        }

        return space;
    }

    private static @NotNull LinkedHashMap<String, Object> mapIDs(String path) throws IOException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        FileReader reader;

        try{
            reader = new FileReader(path);
        } catch(FileNotFoundException fnf){
            throw new FileNotFoundException("Could Not Find Specified File: " + path);
        }

        BufferedReader br = new BufferedReader(reader);

        try{
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                if (line.strip().startsWith("&")){
                    map.put("&", line.strip().replace("&", ""));
                    continue;
                }

                if (line.strip().startsWith(".")){
                    StringBuilder textContent = new StringBuilder();

                    String originalLine = line.replace(".", "");

                    int spaceStart = 0;
                    for (int i = 0; i < line.length(); i++){
                        if (line.charAt(i) != ' '){
                            spaceStart = i;
                            break;
                        }
                    }

                    String start = line.substring(0, spaceStart);

                    for (String inline = br.readLine(); inline != null; inline = br.readLine()){
                        if (inline.equals("/enddiv"))
                            break;

                        textContent.append(inline).append("\n");

                        line = br.readLine();

                        if (line == null) continue;

                        if (line.equals("/enddiv"))
                            break;

                        textContent.append(line).append("\n");
                    }

                    map.put(originalLine, mapIDs(textContent.toString(), null));

                    continue;
                }

                if (line.isEmpty()) continue;

                String[] split = line.split("#");

                split[0] =  split[0].strip();

                split[1] =  split[1].strip();

                map.put("#" + split[1], split[0]);
            }
        }
        catch(IOException io){
            throw new IOException("IO Exception: Check That The Specified File Exists and Application Has Permission");
        }

        return map;
    }

    private static @NotNull LinkedHashMap<String, Object> mapIDs(String text, Void infer) throws IOException {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        BufferedReader br = new BufferedReader(new StringReader(text));

        try{
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                line = line.strip();

                if (line.strip().startsWith(".")){
                    StringBuilder textContent = new StringBuilder();

                    String originalLine = line.replace(".", "");

                    int spaceStart = 0;
                    for (int i = 0; i < line.length(); i++){
                        if (line.charAt(i) != ' '){
                            spaceStart = i;
                            break;
                        }
                    }

                    String start = line.substring(0, spaceStart);

                    for (String inline = br.readLine(); inline != null; inline = br.readLine()){
                        if (inline.replace("    ", "").equals("/enddiv"))
                            break;

                        textContent.append(inline).append("\n");

                        line = br.readLine().replace("     ", "");

                        if (line == null) continue;

                        if (line.equals("/enddiv"))
                            break;

                        textContent.append(line).append("\n");
                    }

                    map.put(originalLine, mapIDs(textContent.toString(), null));

                    continue;
                }

                if (line.isEmpty()){
                    continue;
                }

                if (line.equals("/enddiv")) continue;

                String[] split = line.split("#");

                split[0] =  split[0].strip();

                split[1] =  split[1].strip();

                map.put("#" + split[1], split[0]);
            }
        }
        catch(IOException io){
            throw new IOException("IO Exception: Check That The Specified File Exists and Application Has Permission");
        }

        return map;
    }
}
