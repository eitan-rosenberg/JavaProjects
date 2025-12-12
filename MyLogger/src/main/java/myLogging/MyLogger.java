package myLogging;

import java.lang.StackWalker.StackFrame;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import myLogging.myFormatters.MyFormatter;
import myLogging.myHandlerRecords.MyHandlerRecord;
import myLogging.myHandlerTypes.MyHandlerType;

public class MyLogger {

	private static final Map<Handler, MyHandlerType> handlerMap = new HashMap<>();

	private static final Logger logger;

	static {

		{
			logger = Logger.getLogger(MyLogger.class.getName());
			logger.setUseParentHandlers(false);
			logger.setLevel(Level.ALL);
		}
		{
			addConsoleHandler();
		}
		{

			final UncaughtExceptionHandler uncaughtExceptionHandler = (thread, throwable) -> {
				MyLogger.log(Level.SEVERE, throwable, "Thread.UncaughtException at: {0}", thread.getName());
			};

			Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

		}
	}

	public static synchronized Optional<Handler> addConsoleHandler() {

		final ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.ALL);
		consoleHandler.setFormatter(new MyFormatter(MyFormatter.FormatDelimiter.None));

		logger.addHandler(consoleHandler);

		handlerMap.put(consoleHandler, MyHandlerType.ConsoleHandler);

		return Optional.ofNullable(consoleHandler);

	}

	public static synchronized Optional<Handler> addFileHandler() {
		return addFileHandler(MyHandlerType.FileHandlerLocal);
	}

	public static synchronized Optional<Handler> addFileHandler(final MyHandlerType myHandlerType) {
		return addFileHandler(myHandlerType, "");
	}

	public static synchronized Optional<Handler> addFileHandler(final MyHandlerType myHandlerType,
			final String text_1) {

		final int tenMegaByte = 1000 * 1024 * 10;

		final String clazz = MyLogger.class.getSimpleName();

		// The "%u" and "%g" is part of the FileHandler name pattern.

		final String text_2 = text_1.trim().isEmpty() ? "" : String.format("_%s", text_1);

		final String fileName = switch (myHandlerType) {
		case FileHandlerLocal -> {
			final String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			final String format = String.format("%s_%s%s_%s_%s.log", clazz, date, text_2, "%u", "%g");
			yield format;
		}
		case FileHandlerTemp -> {
			final String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			final Path tmpdir = Path.of(System.getProperty("java.io.tmpdir"));
			final String format = String.format("%s/%s%s%s_%s_%s.log", tmpdir, clazz, time, text_2, "%u", "%g");
			yield format;
		}
		default -> throw new IllegalArgumentException("Unexpected value: " + myHandlerType);
		};

		try {

			final FileHandler fileHandler = new FileHandler(fileName, tenMegaByte, 1000, true);
			fileHandler.setLevel(Level.ALL);
			fileHandler.setFormatter(new MyFormatter(MyFormatter.FormatDelimiter.Tabs));

			MyLogger.logger.addHandler(fileHandler);

			handlerMap.put(fileHandler, myHandlerType);

			return Optional.ofNullable(fileHandler);

		} catch (final Exception exception) {
			exception.printStackTrace();
		}

		return Optional.empty();

	}

	public static void addHandler(final Handler handler) throws SecurityException {
		logger.addHandler(handler);
	}

	public static List<MyHandlerRecord> getHandlers() {

		final List<MyHandlerRecord> list = Arrays.stream(logger.getHandlers()).map(Handler -> {

			final MyHandlerType myHandlerType = handlerMap.getOrDefault(Handler, MyHandlerType.Handler);

			return new MyHandlerRecord(Handler, myHandlerType);

		}).toList();

		return list;

	}

	public static Optional<StackFrame> getStackFrame(final int skip) {
		return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
				.walk(stream -> stream.skip(skip).findFirst());
	}

	public static void log(final Level level, final String msg) {
		logger.log(level, () -> msg);
	}

	public static void log(final Level level, final String pattern, final Object... args) {
		logger.log(level, () -> MessageFormat.format(pattern, args));
	}

	public static void log(final Level level, final Supplier<String> supplier) {
		logger.log(level, supplier);
	}

	public static void log(final Level level, final Throwable throwable, final String msg) {
		logger.log(level, throwable, () -> msg);
	}

	public static void log(final Level level, final Throwable throwable, final String pattern, final Object... args) {
		logger.log(level, throwable, () -> MessageFormat.format(pattern, args));
	}

	public static void log(final Level level, final Throwable throwable, final Supplier<String> supplier) {
		logger.log(level, throwable, supplier);
	}

	public static void printf(final Level level, final String pattern, final Object... args) {
		logger.log(level, () -> String.format(pattern, args));
	}

	public static void removeHandler(final Handler handler) throws SecurityException {
		logger.removeHandler(handler);
	}

	public static void setLevel(final Level level) throws SecurityException {
		logger.setLevel(level);
	}

	private MyLogger() {
	}

}
