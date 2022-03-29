package server.helpers;

import java.net.URI;
import java.util.OptionalInt;

public class QueryParamGetter {
    public static OptionalInt getIdValueFromQuery(URI uri) {
        String[] params =  uri.getQuery().split("&");

        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            if (name.equals("id")) {
                return OptionalInt.of(Integer.parseInt(value));
            }
        }

        return OptionalInt.empty();
    }
}
