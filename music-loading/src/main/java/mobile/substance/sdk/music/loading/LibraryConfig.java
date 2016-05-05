package mobile.substance.sdk.music.loading;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julian Os on 05.05.2016.
 */
public class LibraryConfig {
    private List<LibraryData> data = new ArrayList<>();

    public LibraryConfig put(LibraryData item) {
        data.add(item);
        return this;
    }

    List<LibraryData> get() {
        return data;
    }

}
