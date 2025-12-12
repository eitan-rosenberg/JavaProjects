package myLogging.myStandardInterfaces;

import java.awt.Color;
import java.util.List;
import java.util.logging.Level;

import myLogging.MyLogger;

public interface MyStandardInterface {

	static void showInLog(final List<MyStandardInterface> myStandardInterfaces) {

		myStandardInterfaces.stream().forEach(myStandardInterface -> {
			MyLogger.printf(myStandardInterface.getLevel(), "%15s", myStandardInterface);
		});

	}

	Color getAwtColor();

	Level getLevel();

	String getWebColor();

}