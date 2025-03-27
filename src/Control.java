import Display.Display;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;

public class Control {
    String userFilePath;
    Display display;

    LinkedList<String> tabs = new LinkedList<>();

    LinkedList<String> bookmarkedSites = new LinkedList<>();

    HashMap<String, String> queries = new HashMap<>();

    @NotNull HashMap<String, LinkedList<Object>> pageData = new HashMap<>();

    HashMap<String, LinkedList<Integer>> pageLinks = new HashMap<>();

    BufferedImage searchLogo = ImageLoader.loadImage("/SearchDark.png");

    boolean isDarkMode = true;

    boolean addBookmarkMenu = false;
    int addBookmarkMenuLength = 0;

    int settingsWidth = 0;
    boolean settingsMenuOpen = false;

    String bookmarkName = "";
    boolean bookmarkNameLocked = false;
    boolean bookmarkNameOn = false;
    int bookmarkNameTick = 0;
    int bookmarkNameDelay = 0;

    int clickDelay = 0;

    int scrollY = 70;
    boolean scrollLocked = false;

    int removalDelay = 0;

    String selectedTab = "Home";
    int selectedTabIndex = 0;

    String mainSearchBarData = "";
    boolean mainSearchBarLocked = false;
    int mainSearchBarTick = 0;
    boolean mainSearchBarOn = false;
    int mainSearchBarDelay = 0;

    double settingBarHeight = 5;

    boolean oDark = false;

    public static void main(String[] args) throws Exception {
        Control control = new Control();
        control.run();
    }

    public Control() throws Exception {
        tabs.add("Home");

        userFilePath = System.getProperty("user.home");

        display = new Display("Aurora Search Engine", 1000, 500, ImageLoader.loadImage("/Aurora.png"), false, true);
    }

    public void run() throws Exception {
        while (true){
            if (oDark != isDarkMode){
                if (isDarkMode){
                    searchLogo = ImageLoader.loadImage("/SearchDark.png");
                }
                else{
                    searchLogo = ImageLoader.loadImage("/SearchLight.png");
                }

                oDark = isDarkMode;
            }

            Graphics2D g = (Graphics2D) display.graphicsInit();

            if (isDarkMode){
                g.setColor(new Color(40, 40, 42));
            }
            else{
                g.setColor(new Color(170, 170, 172));
            }
            g.fillRect(0, 38, display.getFrameWidth(), display.getFrameHeight() - 30);

            if (selectedTab.equals("Home")){
                drawHomePage(g, display);
            }

            else if (selectedTab.startsWith("\\")){
                drawPage(g, display);
            }

            else if (selectedTab.startsWith("#")){
                drawQueryPage(g, display);
            }

            if (isDarkMode){
                g.setColor(new Color(30, 30, 32));
            }
            else{
                g.setColor(new Color(140, 140, 142));
            }
            g.fillRect(0, 0, display.getFrameWidth(), 40);

            if (isDarkMode){
                g.setColor(new Color(41, 41, 43));
            }
            else{
                g.setColor(new Color(161, 161, 163));
            }
            g.fillRect(0, 38, display.getFrameWidth(), 30);

            drawTabs(g, display);

            if (isDarkMode){
                g.setColor(new Color(45, 45, 47));
            }
            else{
                g.setColor(new Color(145, 145, 147));
            }
            g.drawLine(0, 38, display.getFrameWidth(), 38);

            g.drawLine(0, 70, display.getFrameWidth(), 70);

            drawSettingsBars(g, display);

            drawSettingsMenu(g, display);

            drawBookmarkedTabs(g, display);

            drawAddBookmarkMenu(g, display);

            if (clickDelay > 0) clickDelay--;

            display.render();
        }
    }

    private void drawHomePage(@NotNull Graphics2D g, @NotNull Display display){
        if (isDarkMode){
            g.setColor(new Color(180, 180, 180));
        }
        else{
            g.setColor(new Color(80, 80, 80));
        }

        g.setFont(new Font("Arial", Font.BOLD, display.getFrameWidth() / 10));
        g.drawString("Aurora", display.getFrameWidth() / 2 - display.getFrameWidth() / 6, display.getFrameHeight() / 2);

        g.fillRoundRect(display.getFrameWidth() / 2 - display.getFrameWidth() / 6, display.getFrameHeight() / 2 + 50, display.getFrameWidth() / 3, 50, 20, 20);

        if (display.getMousePressed() && display.mouseCollide(new Rectangle(display.getFrameWidth() / 2 - display.getFrameWidth() / 6, display.getFrameHeight() / 2 + 50, display.getFrameWidth() / 3, 50)))
            mainSearchBarLocked = true;
        else if (display.getMousePressed())
            mainSearchBarLocked = false;

        g.setFont(new Font("Arial", Font.BOLD, 20));

        if (!mainSearchBarData.isEmpty() || mainSearchBarLocked){
            g.setColor(new Color(40, 40, 42));
            g.drawString(mainSearchBarData, display.getFrameWidth() / 2 - display.getFrameWidth() / 6 + 10, display.getFrameHeight() / 2 + 83);
        }
        else{
            g.setColor(new Color(70, 70, 72));
            g.drawString("Search Aurora database", display.getFrameWidth() / 2 - display.getFrameWidth() / 6 + 10, display.getFrameHeight() / 2 + 83);
        }

        if (mainSearchBarLocked){
            mainSearchBarTick -= 1;
            if (mainSearchBarTick <= 0){
                mainSearchBarOn = !mainSearchBarOn;
                mainSearchBarTick = 200;
            }

            if (mainSearchBarOn)
                g.fillRect(display.getFrameWidth() / 2 - display.getFrameWidth() / 6 + 13 + g.getFontMetrics().stringWidth(mainSearchBarData), display.getFrameHeight() / 2 + 61, 2, 30);

            if (mainSearchBarDelay <= 0){
                int keyIndx = 0;
                boolean[] keys = display.getKeys();

                boolean isUppercase = false;
                if (keys[KeyEvent.VK_SHIFT]){
                    isUppercase = true;
                }

                for (boolean key : keys){
                    if (key && keyIndx != KeyEvent.VK_SHIFT && keyIndx != KeyEvent.VK_CONTROL && keyIndx != KeyEvent.VK_TAB && keyIndx != KeyEvent.VK_NUM_LOCK && keyIndx != KeyEvent.VK_ALT && keyIndx != KeyEvent.VK_CAPS_LOCK && keyIndx != KeyEvent.VK_ESCAPE) {
                        if (keyIndx == KeyEvent.VK_ENTER){
                            tabs.remove(selectedTabIndex);
                            tabs.add(selectedTabIndex, "#" + mainSearchBarData);
                            selectedTab = "#" + mainSearchBarData;
                            mainSearchBarData = "";
                            queries = new HashMap<>();
                        }
                        else if (keyIndx == KeyEvent.VK_BACK_SPACE){
                            if (!mainSearchBarData.isEmpty()){
                                mainSearchBarData = mainSearchBarData.substring(0, mainSearchBarData.length() - 1);
                                mainSearchBarDelay = 15;
                            }
                        }
                        else{
                            char letter = (char) keyIndx;
                            if (!isUppercase){
                                letter = Character.toLowerCase(letter);
                            }

                            mainSearchBarData += letter;
                            mainSearchBarDelay = 15;
                        }
                    }

                    keyIndx++;
                }
            }
            else{
                mainSearchBarDelay--;
            }
        }
    }

    private void drawPage(Graphics2D g, Display display) throws Exception {
        drawScrollBar(g, display);

        if (pageData.isEmpty()){
            pageData = Loader.loadPage(userFilePath + "\\OneDrive\\AuroraSearchEngine\\Websites" + selectedTab, null, userFilePath, isDarkMode);
        }

        String selectedForm = "global";
        for (String conditional : pageData.keySet()){
            if (conditional.startsWith("width ")){
                if (conditional.contains(">")){
                    if (display.getFrameWidth() > Integer.parseInt(conditional.split(">")[1].strip())){
                        selectedForm = conditional;
                    }
                }
                else if (conditional.contains("<")){
                    if (display.getFrameWidth() < Integer.parseInt(conditional.split(">")[1].strip())){
                        selectedForm = conditional;
                    }
                }
            }
        }

        BufferedImage currentPage = (BufferedImage) pageData.get(selectedForm).get(0);
        pageLinks = (HashMap<String, LinkedList<Integer>>) pageData.get(selectedForm).get(1);
        g.drawImage(currentPage, 0, 190 - (scrollY * 2), currentPage.getWidth(), currentPage.getHeight(),null);

        for (String link : pageLinks.keySet()){
            if (display.getMousePressed() && display.mouseCollide(new Rectangle(pageLinks.get(link).getFirst(), pageLinks.get(link).get(1) + 35, pageLinks.get(link).get(2), pageLinks.get(link).getLast()))){
                tabs.remove(selectedTabIndex);
                tabs.add(selectedTabIndex, "\\" + link.replace("u", ""));
                pageData.clear();
                selectedTab = "\\" + link.replace("u", "");
                mainSearchBarData = "";
                queries = new HashMap<>();
            }
        }
    }

    private void drawQueryPage(Graphics2D g, Display display){
        drawScrollBar(g, display);

        if (queries.isEmpty()){
            queries = Querier.query(selectedTab.replace("#", ""));
        }

        int y = 170 - (scrollY * 2);

        for (String query : queries.keySet()){
            if (display.mouseCollide(new Rectangle(20, 70 + y, display.getFrameWidth() - 40, 100)) && !scrollLocked){
                if (display.getMousePressed()){
                    tabs.remove(selectedTabIndex);
                    tabs.add(selectedTabIndex, "\\" + query);
                    selectedTab = "\\" + query;
                    pageData.clear();
                }

                if (isDarkMode){
                    g.setColor(new Color(50, 50, 52));
                }
                else{
                    g.setColor(new Color(170, 170, 172));
                }
                g.fillRoundRect(20, 70 + y, display.getFrameWidth() - 60, 100, 20, 20);
            }
            else{
                if (isDarkMode){
                    g.setColor(new Color(40, 40, 42));
                }
                else{
                    g.setColor(new Color(160, 160, 162));
                }
                g.fillRoundRect(20, 70 + y, display.getFrameWidth() - 60, 100, 20, 20);
            }

            if (isDarkMode){
                g.setColor(new Color(60, 60, 62));
            }
            else{
                g.setColor(new Color(145, 145, 147));
            }
            g.drawRoundRect(20, 70 + y, display.getFrameWidth() - 60, 100, 20, 20);

            g.setFont(new Font("Arial", Font.BOLD, 20));
            if (isDarkMode){
                g.setColor(new Color(180, 180, 180));
            }
            else{
                g.setColor(new Color(60, 60, 60));
            }

            if (g.getFontMetrics().stringWidth(query) > display.getFrameWidth() - 100){
                g.drawString(query.substring(0, (display.getFrameWidth() - 100) / (g.getFontMetrics().stringWidth(query) / query.length())), 40, 130 + y);
            }
            else{
                g.drawString(query, 40, 130 + y);
            }

            y += 110;

            if (y > display.getFrameHeight())
                break;
        }
    }

    private void drawScrollBar(@NotNull Graphics g, @NotNull Display display){
        g.setColor(new Color(60, 60, 62));
        g.fillRect(display.getFrameWidth() - 20, 70, 30, display.getFrameHeight() - 70);
        g.setColor(new Color(70, 70, 72));
        g.fillRect(display.getFrameWidth() - 20, scrollY, 30, 50);

        if (display.getMousePressed()){
            if (display.mouseCollide(new Rectangle(display.getFrameWidth() - 20, 70, 20, display.getFrameHeight() - 70))){
                scrollLocked = true;
            }
        }
        else{
            scrollLocked = false;
        }

        if (settingsMenuOpen) scrollLocked = false;

        if (scrollLocked){
            scrollY = display.getMouseY() - 25;
            if (scrollY < 70)
                scrollY = 70;
            if (scrollY > display.getFrameHeight() - 50)
                scrollY = display.getFrameHeight() - 50;
        }
    }

    private void drawSettingsMenu(@NotNull Graphics2D g, @NotNull Display display) throws Exception {
        if (isDarkMode){
            g.setColor(new Color(41, 41, 43));
            g.fillRect(display.getFrameWidth() - settingsWidth, 71, settingsWidth, display.getFrameHeight() - 71);
            g.setColor(new Color(50, 50, 52));
            g.drawLine(display.getFrameWidth() - settingsWidth, 70, display.getFrameWidth() - settingsWidth, display.getFrameHeight());
        }
        else{
            g.setColor(new Color(161, 161, 163));
            g.fillRect(display.getFrameWidth() - settingsWidth, 71, settingsWidth, display.getFrameHeight() - 71);
            g.setColor(new Color(170, 170, 172));
            g.drawLine(display.getFrameWidth() - settingsWidth, 70, display.getFrameWidth() - settingsWidth, display.getFrameHeight());
        }

        boolean oDarkMode = isDarkMode;
        isDarkMode = drawBooleanInput(g, display, 0, "Dark Mode", isDarkMode);
        if (isDarkMode != oDarkMode && selectedTab.startsWith("\\")){
            pageData = Loader.loadPage(userFilePath + "\\OneDrive\\AuroraSearchEngine\\Websites" + selectedTab, null, userFilePath, isDarkMode);
        }

        if (settingsMenuOpen){
            if (settingsWidth < display.getFrameWidth() / 4){
                settingsWidth += 2;
            }
            else if (settingsWidth > display.getFrameWidth() / 4 + 5){
                settingsWidth -= 2;
            }
        }
        else if (settingsWidth > 0){
            settingsWidth -= 2;
        }
    }

    private void drawSettingsBars(@NotNull Graphics g, @NotNull Display display){
        if (isDarkMode){
            g.setColor(new Color(80, 80, 82));
        }
        else{
            g.setColor(new Color(200, 200, 202));
        }

        if (display.mouseCollide(new Rectangle(display.getFrameWidth() - 57, 43, 30, 20))){
            if (settingBarHeight < 15)
                settingBarHeight += 0.15;

            if (isDarkMode){
                g.setColor(new Color(100, 100, 102));
            }
            else{
                g.setColor(new Color(220, 220, 222));
            }

            if (display.getMousePressed() && clickDelay <= 0 && !scrollLocked){
                clickDelay = 200;
                settingsMenuOpen = !settingsMenuOpen;
            }
        }
        else if (settingBarHeight > 5)
            settingBarHeight -= 0.15;

        if (display.getMousePressed() && !display.mouseCollide(new Rectangle(display.getFrameWidth() - 57, 43, 30, 20)) && !display.mouseCollide(new Rectangle(display.getFrameWidth() - settingsWidth, 71, settingsWidth, display.getFrameHeight() - 71))){
            settingsMenuOpen = false;
        }

        g.fillOval(display.getFrameWidth() - 55, 50 - (int) ((settingBarHeight - 6) / 2), 5, 5);
        g.fillRect(display.getFrameWidth() - 55, 53 - (int) ((settingBarHeight - 6) / 2), 5, (int) settingBarHeight - 6);
        g.fillOval(display.getFrameWidth() - 55, 50 + (int) ((settingBarHeight - 6) / 2), 5, 5);

        g.fillOval(display.getFrameWidth() - 45, 50 - (int) ((settingBarHeight - 6) / 2), 5, 5);
        g.fillRect(display.getFrameWidth() - 45, 53 - (int) ((settingBarHeight - 6) / 2), 5, (int) settingBarHeight - 6);
        g.fillOval(display.getFrameWidth() - 45, 50 + (int) ((settingBarHeight - 6) / 2), 5, 5);

        g.fillOval(display.getFrameWidth() - 35, 50 - (int) ((settingBarHeight - 6) / 2), 5, 5);
        g.fillRect(display.getFrameWidth() - 35, 53 - (int) ((settingBarHeight - 6) / 2), 5, (int) settingBarHeight - 6);
        g.fillOval(display.getFrameWidth() - 35, 50 + (int) ((settingBarHeight - 6) / 2), 5, 5);
    }

    private void drawBookmarkedTabs(@NotNull Graphics2D g, Display display){
        int x = 10;
        g.setColor(new Color(180, 180, 180));
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        for (String tab : bookmarkedSites){
            g.drawString(tab.replace("\\", ""), x, 60);

            if (display.getMousePressed() && display.mouseCollide(new Rectangle(x - 5, 45, g.getFontMetrics().stringWidth(tab.replace("\\", "")) + 10, 16))){
                selectedTab = tab;
                tabs.remove(selectedTabIndex);
                tabs.add(selectedTabIndex, tab);
                pageData.clear();
            }

            x += g.getFontMetrics().stringWidth(tab.replace("\\", "")) + 25;

            if (x > display.getFrameWidth())
                break;
        }

        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("+", display.getFrameWidth() - 85, 60);

        if (display.getMousePressed()){
            if (display.mouseCollide(new Rectangle(display.getFrameWidth() - 85, 46, 12, 12))){
                if (clickDelay <= 0){
                    bookmarkName = "";
                    addBookmarkMenu = !addBookmarkMenu;
                    clickDelay = 100;
                }
            }
            else if (!display.mouseCollide(new Rectangle(display.getFrameWidth() - 150, 65, 125, addBookmarkMenuLength))){
                addBookmarkMenu = false;
            }
        }
    }

    private void drawAddBookmarkMenu(@NotNull Graphics2D g, Display display){
        if (addBookmarkMenu){
            if (addBookmarkMenuLength < 125){
                addBookmarkMenuLength += 2;
            }

            if (isDarkMode){
                g.setColor(new Color(45, 45, 47));
                g.fillRoundRect(display.getFrameWidth() - 150, 65, 125, addBookmarkMenuLength, 20, 20);
                g.setColor(new Color(50, 50, 52));
                g.drawRoundRect(display.getFrameWidth() - 150, 65, 125, addBookmarkMenuLength, 20, 20);

                g.setColor(new Color(180, 180, 180));
                g.setFont(new Font("Arial", Font.PLAIN, (int) (addBookmarkMenuLength / 8.5)));
                g.drawString("Add Book Mark", (display.getFrameWidth() - 140), 65 + addBookmarkMenuLength / 5);
            }
            else{
                g.setColor(new Color(165, 165, 167));
                g.fillRoundRect(display.getFrameWidth() - 150, 65, 125, addBookmarkMenuLength, 20, 20);
                g.setColor(new Color(170, 170, 172));
                g.drawRoundRect(display.getFrameWidth() - 150, 65, 125, addBookmarkMenuLength, 20, 20);

                g.setColor(new Color(60, 60, 60));
                g.setFont(new Font("Arial", Font.PLAIN, (int) (addBookmarkMenuLength / 8.5)));
                g.drawString("Add Book Mark", (display.getFrameWidth() - 140), 65 + addBookmarkMenuLength / 5);
            }

            drawBookmarkTypeBar(g, display);
        }
        else if (addBookmarkMenuLength > 0){
            addBookmarkMenuLength -= 2;
        }
    }

    private void drawBookmarkTypeBar(@NotNull Graphics2D g, @NotNull Display display){
        g.drawLine((display.getFrameWidth() - 140), 73 + addBookmarkMenuLength / 2, (display.getFrameWidth() - 40), 73 + addBookmarkMenuLength / 2);
        if (isDarkMode)
            g.drawImage(searchLogo, display.getFrameWidth() - 145, 54 + addBookmarkMenuLength / 2, 14, 14, null);
        else
            g.drawImage(searchLogo, display.getFrameWidth() - 143, 56 + addBookmarkMenuLength / 2, 10, 10, null);

        if (bookmarkName.isEmpty()){
            g.setColor(new Color(100, 100, 100));
            g.drawString("Enter URL", (display.getFrameWidth() - 130), 65 + addBookmarkMenuLength / 2);
        }
        else
            g.drawString(bookmarkName, (display.getFrameWidth() - 130), 65 + addBookmarkMenuLength / 2);

        if (display.getMousePressed()){
            bookmarkNameLocked = display.mouseCollide(new Rectangle(display.getFrameWidth() - 145, 49 + addBookmarkMenuLength / 2, 115, 30));
        }

        if (bookmarkNameLocked){
            bookmarkNameTick--;
            if (bookmarkNameTick <= 0){
                bookmarkNameOn = !bookmarkNameOn;
                bookmarkNameTick = 200;
            }

            if (bookmarkNameOn){
                if (isDarkMode)
                    g.setColor(new Color(180, 180, 180));
                else
                    g.setColor(new Color(60, 60, 60));
                g.fillRect(display.getFrameWidth() - 130 + g.getFontMetrics().stringWidth(bookmarkName), 50 + addBookmarkMenuLength / 2, 3, 20);
            }

            if (bookmarkNameDelay <= 0){
                int keyIndx = 0;
                boolean[] keys = display.getKeys();

                boolean isUppercase = keys[KeyEvent.VK_SHIFT];

                for (boolean key : keys){
                    if (key && keyIndx != KeyEvent.VK_SHIFT && keyIndx != KeyEvent.VK_CONTROL && keyIndx != KeyEvent.VK_TAB && keyIndx != KeyEvent.VK_NUM_LOCK && keyIndx != KeyEvent.VK_ALT && keyIndx != KeyEvent.VK_CAPS_LOCK && keyIndx != KeyEvent.VK_ESCAPE) {
                        if (keyIndx == KeyEvent.VK_ENTER){
                            bookmarkedSites.add("\\" + bookmarkName);
                            addBookmarkMenu = false;
                        }
                        else if (keyIndx == KeyEvent.VK_BACK_SPACE){
                            if (!bookmarkName.isEmpty()){
                                bookmarkName = bookmarkName.substring(0, bookmarkName.length() - 1);
                                bookmarkNameDelay = 10;
                            }
                        }
                        else{
                            char letter = (char) keyIndx;
                            if (!isUppercase)
                                letter = Character.toLowerCase(letter);

                            bookmarkName += letter;
                            bookmarkNameDelay = 10;
                        }
                    }

                    keyIndx++;
                }
            }
            else{
                bookmarkNameDelay--;
            }
        }
    }

    private boolean drawTab(@NotNull Graphics2D g, @NotNull Display display, int x, @NotNull String tabName, boolean isSelected){
        boolean result = false;

        if (isSelected){
            if (isDarkMode){
                g.setColor(new Color(40, 40, 42));
            }
            else{
                g.setColor(new Color(160, 160, 162));
            }

            if (display.mouseCollide(new Rectangle(x, 10, 150, 30))){
                if (display.getMousePressed()){
                    result = true;
                }

                if (isDarkMode){
                    g.setColor(new Color(43, 43, 49));
                }
                else{
                    g.setColor(new Color(163, 163, 169));
                }
            }

            g.fillRoundRect(x, 10, 150, 30, 10, 10);
        }
        else {
            if (display.mouseCollide(new Rectangle(x, 10, 150, 30))){
                if (display.getMousePressed()){
                    result = true;
                }

                if (isDarkMode){
                    g.setColor(new Color(40, 40, 50));
                }
                else{
                    g.setColor(new Color(160, 160, 170));
                }
                g.fillRoundRect(x, 10, 150, 25, 10, 10);
            }
        }

        if (tabName.length() > 13){
            tabName = tabName.substring(0, 10) + "...";
        }

        g.setColor(new Color(180, 180, 180));
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString(tabName, x + 5,  30);

        return result;
    }

    private void drawTabs(Graphics2D g, Display display){
        int x = 5;
        try{
            for (String tab : tabs){
                if ((x - 5) / 155 == selectedTabIndex){
                    drawTab(g, display, x, tab, true);

                    g.setFont(new Font("Arial", Font.BOLD, 20));
                    g.drawString("×", x + 130, 30);

                    if (display.getMousePressed() && display.mouseCollide(new Rectangle(x + 130, 19, 10, 10))){
                        if (tabs.size() == 1){
                            if (removalDelay <= 0) {
                                removalDelay = 100;
                                tabs.remove(tab);
                                tabs.add("Home");
                                selectedTab = "Home";
                                selectedTabIndex = 0;
                                queries = new HashMap<>();
                            }
                        }
                        else{
                            if (removalDelay <= 0){
                                removalDelay = 100;
                                selectedTabIndex = (x - 5) / 155;
                                tabs.remove(tab);
                                selectedTab = tabs.get(selectedTabIndex);
                            }
                        }
                    }

                    x += 155;
                    continue;
                }

                if (drawTab(g, display, x, tab, false)){
                    selectedTab = tab;
                    selectedTabIndex = (x - 5) / 155;
                    mainSearchBarData = "";
                    queries = new HashMap<>();
                }

                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.drawString("×", x + 130, 30);

                if (display.getMousePressed() && display.mouseCollide(new Rectangle(x + 130, 19, 10, 10))){
                    if (tab.equals(selectedTab)){
                        if (tabs.size() == 1){
                            if (removalDelay <= 0){
                                tabs.remove(tab);
                                tabs.add("Home");
                                selectedTab = "Home";
                                selectedTabIndex = 0;
                                mainSearchBarData = "";
                                queries = new HashMap<>();
                            }
                        }
                        else{
                            if (removalDelay <= 0){
                                removalDelay = 100;
                                selectedTabIndex = (x - 5) / 155;
                                tabs.remove(tab);
                                selectedTab = tabs.get(selectedTabIndex);
                            }
                        }
                    }
                    else{
                        if (removalDelay <= 0){
                            removalDelay = 100;
                            selectedTabIndex = (x - 5) / 155;
                            tabs.remove(tab);
                            selectedTab = tabs.get(selectedTabIndex);
                        }
                    }
                }

                x += 155;
            }
        }
        catch(ConcurrentModificationException cm){}

        if (removalDelay > 0){
            removalDelay--;
        }

        if (tabs.size() < 6){
            g.drawString("+", x + 5, 30);
            if (display.getMousePressed() && display.mouseCollide(new Rectangle(x + 5, 19, 10, 10))){
                tabs.add("Home");
                selectedTab = "Home";
                selectedTabIndex = (x - 5) / 155;
                mainSearchBarData = "";
            }
        }
        else{
            if (isDarkMode){
                g.setColor(new Color(100, 100, 100));
            }
            else{
                g.setColor(new Color(160, 160, 160));
            }
            g.drawString("+", x + 5, 30);
        }
    }

    private boolean drawBooleanInput(@NotNull Graphics2D g, @NotNull Display display, int y, String text, boolean isOn){
        if (isDarkMode){
            g.setColor(new Color(180, 180, 180));
        }
        else{
            g.setColor(new Color(60, 60, 60));
        }
        g.drawString(text, display.getFrameWidth() - settingsWidth + 20, 97 + y);

        if (display.getMousePressed() && display.mouseCollide(new Rectangle(display.getFrameWidth() - 100, 80 + y, 50, 20)) && clickDelay <= 0){
            isOn = !isOn;
            clickDelay = 200;
        }

        if (isOn){
            if (settingsWidth + (int) (settingsWidth / 1.5) < 500){
                if (isDarkMode){
                    g.setColor(new Color(50, 50, 60));
                    g.fillRoundRect(display.getFrameWidth() - settingsWidth + (int) (settingsWidth / 1.5), 80 + y, 50, 20, 20, 20);
                    g.setColor(new Color(60, 60, 70));
                    g.fillOval(display.getFrameWidth() - settingsWidth + (int) (settingsWidth / 1.5) + 30, 80 + y, 20, 20);
                }
                else{
                    g.setColor(new Color(150, 150, 160));
                    g.fillRoundRect(display.getFrameWidth() - settingsWidth + (int) (settingsWidth / 1.5), 80 + y, 50, 20, 20, 20);
                    g.setColor(new Color(160, 160, 170));
                    g.fillOval(display.getFrameWidth() - settingsWidth + (int) (settingsWidth / 1.5) + 30, 80 + y, 20, 20);
                }
            }
            else{
                if (isDarkMode){
                    g.setColor(new Color(50, 50, 55));
                    g.fillRoundRect(display.getFrameWidth() - 100, 80 + y, 50, 20, 20, 20);
                    g.setColor(new Color(60, 60, 70));
                    g.fillOval(display.getFrameWidth() - 70, 80 + y, 20, 20);
                }
                else{
                    g.setColor(new Color(150, 150, 155));
                    g.fillRoundRect(display.getFrameWidth() - 100, 80 + y, 50, 20, 20, 20);
                    g.setColor(new Color(160, 160, 170));
                    g.fillOval(display.getFrameWidth() - 70, 80 + y, 20, 20);
                }
            }
        }
        else{
            if (settingsWidth + (int) (settingsWidth / 1.5) < 500){
                if (isDarkMode){
                    g.setColor(new Color(45, 45, 45));
                    g.fillRoundRect(display.getFrameWidth() - settingsWidth + (int) (settingsWidth / 1.5), 80 + y, 50, 20, 20, 20);
                    g.setColor(new Color(50, 50, 53));
                    g.fillOval(display.getFrameWidth() - settingsWidth + (int) (settingsWidth / 1.5), 80 + y, 20, 20);
                }
                else{
                    g.setColor(new Color(145, 145, 145));
                    g.fillRoundRect(display.getFrameWidth() - settingsWidth + (int) (settingsWidth / 1.5), 80 + y, 50, 20, 20, 20);
                    g.setColor(new Color(150, 150, 153));
                    g.fillOval(display.getFrameWidth() - settingsWidth + (int) (settingsWidth / 1.5), 80 + y, 20, 20);
                }
            }
            else{
                if (isDarkMode){
                    g.setColor(new Color(45, 45, 45));
                    g.fillRoundRect(display.getFrameWidth() - 100, 80 + y, 50, 20, 20, 20);
                    g.setColor(new Color(50, 50, 53));
                    g.fillOval(display.getFrameWidth() - 100, 80 + y, 20, 20);
                }
                else{
                    g.setColor(new Color(145, 145, 145));
                    g.fillRoundRect(display.getFrameWidth() - 100, 80 + y, 50, 20, 20, 20);
                    g.setColor(new Color(150, 150, 153));
                    g.fillOval(display.getFrameWidth() - 100, 80 + y, 20, 20);
                }
            }
        }

        return isOn;
    }
}

