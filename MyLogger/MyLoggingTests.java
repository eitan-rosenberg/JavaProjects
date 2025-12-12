package myLoggingTests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import myLogging.MyLogger;
import myLogging.myCleanup.MyCleanup;
import myLogging.myHandlerTypes.MyHandlerType;
import myLogging.myStandardInterfaces.MyStandardInterface;
import myLogging.myStandardLevels.MyStandardLevel;

public class MyLoggingTests {

	public static void main(final String[] args) {
		new MyLoggingTests();
	}

	public MyLoggingTests() {

//		test_04_myStandardLevel();
//		test_05_myStandardInterface();
//		test_06_deleteLogFiles();
//		test_07_threadUncaughtException();
//		test_08_loggerText();

	}

	@SuppressWarnings("unused")
	private void test_04_myStandardLevel() {

		MyLogger.log(Level.INFO, " ");
		MyLogger.log(Level.INFO, "{0}", MyLogger.getStackFrame(1).get());
		MyLogger.log(Level.INFO, " ");

		final Comparator<Map.Entry<Level, MyStandardInterface>> comparator = Comparator.comparing(entry -> {
			return entry.getKey().intValue();
		});

		MyStandardLevel.getValueMap().entrySet().stream().sorted(comparator).forEach(entry -> {
			MyLogger.printf(entry.getKey(), "key: %-15s value: %-100s", entry.getKey(), entry.getValue());
		});

	}

	@SuppressWarnings("unused")
	private void test_05_myStandardInterface() {

		MyLogger.log(Level.INFO, " ");
		MyLogger.log(Level.INFO, "{0}", MyLogger.getStackFrame(1).get());
		MyLogger.log(Level.INFO, " ");

		MyStandardInterface.showInLog(MyStandardLevel.getValueList());

	}

	@SuppressWarnings("unused")
	private void test_06_deleteLogFiles() {

		MyLogger.log(Level.INFO, " ");
		MyLogger.log(Level.INFO, "{0}", MyLogger.getStackFrame(1).get());
		MyLogger.log(Level.INFO, " ");

		final Path tmpdir = Path.of(System.getProperty("java.io.tmpdir"));

		IntStream.rangeClosed(0, 10).forEach(index -> {
			final String format = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now().minusDays(index));
			MyLogger.addFileHandler(MyHandlerType.FileHandlerTemp, String.format("_Test_%02d_%s", index, format));
		});

		IntStream.rangeClosed(1, 10).forEach(index -> {
			MyLogger.log(Level.INFO, "Result->{0}<-", index);
		});

		MyLogger.getHandlers().stream().filter(myHandlerRecord -> {
			return myHandlerRecord.handler() instanceof FileHandler;
		}).filter(myHandlerRecord -> {
			return myHandlerRecord.MyHandlerType() == MyHandlerType.FileHandlerTemp;
		}).forEach(myHandlerRecord -> {
			myHandlerRecord.handler().close();
		});

		{

			final ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now());

			try (Stream<Path> stream = Files.walk(tmpdir)) {

				final AtomicInteger counter = new AtomicInteger(0);

				stream.sorted().filter(MyCleanup.isLog).forEach(path -> {

					try {

						final LocalDateTime localDateTime = LocalDate.now().minusDays(counter.get())
								.atTime(LocalTime.now());

						final FileTime fileTime = FileTime.from(localDateTime.toInstant(zoneOffset));

						Files.setLastModifiedTime(path, fileTime);

					} catch (final Exception exception) {
						exception.printStackTrace();
					}

					counter.incrementAndGet();

				});

			} catch (final IOException exception) {
				exception.printStackTrace();
			}

		}

		MyCleanup.deleteLogFiles(tmpdir);

	}

	@SuppressWarnings("unused")
	private void test_07_threadUncaughtException() {

		MyLogger.log(Level.INFO, " ");
		MyLogger.log(Level.INFO, "{0}", MyLogger.getStackFrame(1).get());
		MyLogger.log(Level.INFO, " ");

		final int value = 10 / 0;

	}

	@SuppressWarnings("unused")
	private void test_08_loggerText() {

//		MyLogger.addFileHandler(MyHandlerType.FileHandlerLocal);
//		MyLogger.addFileHandler(MyHandlerType.FileHandlerLocal, this.getClass().getSimpleName());

		MyLogger.addFileHandler(MyHandlerType.FileHandlerTemp);
		MyLogger.addFileHandler(MyHandlerType.FileHandlerTemp, this.getClass().getSimpleName());

	}

}
