import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class Querier {
    @Contract(pure = true)
    public static @NotNull HashMap<String, String> query(String queryRequest){
        HashMap<String, String> queries = new HashMap<>();

        userFilePath = System.getProperty("user.home");

        for (File file : Objects.requireNonNull(new File(userFilePath + "/OneDrive/AuroraSearchEngine/Websites").listFiles())){
            String websiteName = file.toString().replace(userFilePath + "\\OneDrive\\AuroraSearchEngine\\Websites\\", "");

            if (websiteName.toLowerCase().replace(" ", "").startsWith(queryRequest.toLowerCase().replace(" ", ""))) {
                queries.put(websiteName, file.toString());
            }
        }

        return queries;
    }
}
