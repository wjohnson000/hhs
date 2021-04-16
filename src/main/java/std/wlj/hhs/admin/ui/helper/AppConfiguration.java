/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin.ui.helper;

/**
 * @author wjohnson000
 *
 */
public class AppConfiguration {

    private static boolean isProd = false;
    private static String  prodSessionId;
    private static String  devSessionId;

    public static boolean isProd() {
        return isProd;
    }

    public static void setIsProd(boolean isProdNew) {
        isProd = isProdNew;
    }

    public static String getProdSessionId() {
        return prodSessionId;
    }

    public static void setProdSessionId(String prodSessionIdNew) {
        prodSessionId = prodSessionIdNew;
    }

    public static String getDevSessionId() {
        return devSessionId;
    }

    public static void setDevSessionId(String devSessionIdNew) {
        devSessionId = devSessionIdNew;
    }

    public static String getSessionId() {
        return (isProd()) ? getProdSessionId() : getDevSessionId();
    }
}
