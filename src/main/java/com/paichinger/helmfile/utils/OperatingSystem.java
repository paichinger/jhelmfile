package com.paichinger.helmfile.utils;

public class OperatingSystem {
	public enum OS {
		WINDOWS, LINUX, MAC, NOT_SUPPORTED
	}
	
	private static OS os = null;
	
	public static OS getOS() {
		if (os == null) {
			String operSys = System.getProperty("os.name").toLowerCase();
			if (operSys.contains("win")) {
				os = OS.WINDOWS;
			}
			else if (operSys.contains("nix") || operSys.contains("nux")
					|| operSys.contains("aix")) {
				os = OS.LINUX;
			}
			else if (operSys.contains("mac")) {
				os = OS.MAC;
			}
			else {
				os = OS.NOT_SUPPORTED;
			}
		}
		return os;
	}
}
