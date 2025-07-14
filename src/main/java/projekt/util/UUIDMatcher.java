package projekt.util;

import java.util.UUID;
import java.util.regex.Pattern;

public class UUIDMatcher {
    public static Boolean isValid(String uuid) {
        Pattern UUID_REGEX =
                Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        return UUID_REGEX.matcher(uuid).matches();
    }
}
