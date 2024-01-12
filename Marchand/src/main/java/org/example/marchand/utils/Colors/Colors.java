package org.example.marchand.utils.Colors;

public class Colors {
    public static String BrightRED      = "\033[91m";
    public static String BrightGreen    = "\033[92m";
    public static String BrightYellow   = "\033[93m";
    public static String BrightBlue     = "\033[94m";
    public static String BrightMangenta = "\033[95m";
    public static String BrightCyan     = "\033[96m";
    public static String Default        = "\033[0m";

    public static String fromRGB(int r, int g, int b){
        return "\033[38;2;" + r + ";" + g + ";" + b + "m";
    }
}