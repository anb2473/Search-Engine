import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class Querier {
    @Contract(pure = true)
    public static @NotNull HashMap<String, String> query(String queryRequest){
        HashMap<String, String> queries = new HashMap<>();

        for (File file : Objects.requireNonNull(new File("Websites").listFiles())){
            String websiteName = file.toString().replace("Websites\\", "");

            if (websiteName.toLowerCase().replace(" ", "").startsWith(queryRequest.toLowerCase().replace(" ", ""))) {
                queries.put(websiteName, file.toString());
            }
        }

        return queries;
    }
}
