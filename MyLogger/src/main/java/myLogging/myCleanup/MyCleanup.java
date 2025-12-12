package myLogging.myCleanup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import myLogging.MyLogger;

public class MyCleanup {

	public static final Instant instant = LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

	public static final Predicate<? super Path> isLog = new Predicate<>() {

		Predicate<String> isNameMatch;

		{
			final String simpleName = MyLogger.class.getSimpleName();

			final String format = String.format("^%1$s.*log$|^%1$s.*log.lck$", simpleName);

			isNameMatch = Pattern.compile(format, Pattern.CASE_INSENSITIVE).asPredicate();

		}

		@Override
		public boolean test(final Path path) {
			return isNameMatch.test(path.getFileName().toString());
		}

	};

	public static final Predicate<? super Path> isOld = path -> {
		return MyCleanup.pathToInstant.apply(path).isBefore(instant);
	};

	public static final Function<Path, Instant> pathToInstant = path -> {

		try {
			return Files.getLastModifiedTime(path).toInstant();
		} catch (final IOException exception) {
			return instant;
		}

	};

	public static synchronized void deleteLogFiles(final Path startPath) {

		try (Stream<Path> stream = Files.walk(startPath)) {

			final List<Path> paths = stream.filter(Files::isRegularFile).filter(isLog).toList();

			paths.stream().filter(isOld).forEach(path -> {
				try {
					Files.delete(path);
					MyLogger.log(Level.INFO, "{0} deleted", path.toAbsolutePath().normalize());
				} catch (final IOException exception) {
					MyLogger.log(Level.SEVERE, exception, "");
				}
			});

		} catch (final Exception exception) {
			MyLogger.log(Level.SEVERE, exception, "");
		}

	}

}
