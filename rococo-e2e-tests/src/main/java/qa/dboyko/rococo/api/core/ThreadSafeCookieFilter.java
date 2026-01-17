package qa.dboyko.rococo.api.core;

import io.restassured.filter.cookie.CookieFilter;
import org.apache.http.cookie.Cookie;

public enum ThreadSafeCookieFilter {
    INSTANCE;

    private final ThreadLocal<CookieFilter> cookieFilter =
            ThreadLocal.withInitial(CookieFilter::new);

    public CookieFilter get() {
        return cookieFilter.get();
    }

    public String cookieValue(String name) {
        System.out.println(get()
                .getCookieStore()
                .getCookies());
        return get()
                .getCookieStore()
                .getCookies()
                .stream()
                .filter(c -> c.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow();
    }

    public void clear() {
        cookieFilter.remove();
    }
}
