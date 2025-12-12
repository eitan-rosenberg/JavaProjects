package myLogging.myStandardLevels;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

import myLogging.myStandardInterfaces.MyStandardInterface;

/**
 * Idea from org.apache.logging.log4j.spi.StandardLevel <br>
 * Allows for the benefits of using ENUM (e.g. switch structure) <br>
 *
 */
public enum MyStandardLevel implements MyStandardInterface {

	ALL(Level.ALL, "White", new Color(255, 255, 255)),

	CONFIG(Level.CONFIG, "Silver", new Color(192, 192, 192)),

	FINE(Level.FINE, "DarkGray", new Color(169, 169, 169)),

	FINER(Level.FINER, "Gray", new Color(128, 128, 128)),

	FINEST(Level.FINEST, "DimGray", new Color(105, 105, 105)),

	INFO(Level.INFO, "LawnGreen", new Color(124, 252, 000)),

	OFF(Level.OFF, "Blue", new Color(000, 000, 255)),

	SEVERE(Level.SEVERE, "Red", new Color(255, 000, 000)),

	WARNING(Level.WARNING, "Yellow", new Color(255, 255, 000)),

	;

	private static final List<MyStandardInterface> value_list;
	private static final Map<Level, MyStandardInterface> value_map;

	static {

		value_list = List.of(MyStandardLevel.values()).stream()
				.sorted(Comparator.comparing(myStandardLevel -> myStandardLevel.getLevel().intValue()))
				.collect(Collectors.toList());

		value_map = value_list.stream().collect(Collectors.toMap(MyStandardInterface::getLevel, Function.identity()));

	}

	public static List<MyStandardInterface> getValueList() {
		return Collections.unmodifiableList(value_list);
	}

	public static Map<Level, MyStandardInterface> getValueMap() {
		return Collections.unmodifiableMap(value_map);
	}

	private final Color awtColor;
	private final Level level;
	private final String webColor;

	MyStandardLevel(final Level level, final String webColor, final Color awtColor) {

		this.level = level;
		this.webColor = webColor;
		this.awtColor = awtColor;

	}

	@Override
	public Color getAwtColor() {
		return awtColor;
	}

	@Override
	public Level getLevel() {
		return level;
	}

	@Override
	public String getWebColor() {
		return webColor;
	}

	@Override
	public String toString() {

		final int red = awtColor.getRed();
		final int green = awtColor.getGreen();
		final int blue = awtColor.getBlue();

		final String format = String.format("%-16s %16d %-16s (%03d,%03d,%03d)", name(), level.intValue(), webColor,
				red, green, blue);

		return format;

	}

}
