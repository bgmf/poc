package eu.dzim.shared.util;

public class OSHelper {
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	public static boolean isWindows() {
		return OS.toLowerCase().contains("win");
	}
	
	public static boolean isMac() {
		return OS.toLowerCase().contains("mac");
	}
	
	public static boolean isUnixLinux() {
		return OS.toLowerCase().contains("nix") || OS.toLowerCase().contains("nux") || OS.toLowerCase().contains("aix");
	}
	
	public static boolean isSolaris() {
		return OS.toLowerCase().contains("sunos");
	}
	
	public static String getOS() {
		return OS;
	}
}