package server.helpers;

import java.net.URI;
import java.net.URL;
import java.util.Optional;

public class QueryParamGetter {
    /**
     * Вернуть id задачи
     * @param url
     * @return
     * Вернет индентификатор задачи, если в запросе был найден параметр id.
     */
    public static Optional<Integer> getTaskId(URI url) {
        String[] params =  url.getQuery().split("&");

        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            if (name.equals("id")) {
                return Optional.of(Integer.parseInt(value));
            }
        }
        return Optional.empty();
    }
}
