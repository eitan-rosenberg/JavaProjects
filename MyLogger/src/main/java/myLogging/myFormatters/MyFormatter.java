package myLogging.myFormatters;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MyFormatter extends Formatter {

	public enum FormatDelimiter {

		None(" "),

		Tabs("\t");

		private final String delimiter;

		FormatDelimiter(final String delimiter) {
			this.delimiter = delimiter;
		}

	}

	private final static DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("uuuu-MM-dd");
	private final static DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");

	private final static ZoneId zoneId = ZoneId.systemDefault();

	private final FormatDelimiter formatDelimiter;

	private final StringBuilder stringBuilder = new StringBuilder();

	public MyFormatter() {
		this(FormatDelimiter.None);
	}

	public MyFormatter(final FormatDelimiter formatDelimiter) {
		this.formatDelimiter = formatDelimiter;
	}

	@Override
	public String format(final LogRecord logRecord) {

		final Instant instant = Instant.ofEpochMilli(logRecord.getMillis());

		final LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();

		// date, Time, sequenceNumber ,level, message, thrown

		{

			stringBuilder.setLength(0);

			stringBuilder.append(formatterDate.format(localDateTime));
			stringBuilder.append(formatDelimiter.delimiter);

			stringBuilder.append(localDateTime.format(formatterTime));
			stringBuilder.append(formatDelimiter.delimiter);

			stringBuilder.append(String.format("%07d", logRecord.getSequenceNumber() + 1));
			stringBuilder.append(formatDelimiter.delimiter);

			stringBuilder.append(String.format("%-7s", logRecord.getLevel()));
			stringBuilder.append(formatDelimiter.delimiter);

			stringBuilder.append(super.formatMessage(logRecord));
			stringBuilder.append(formatDelimiter.delimiter);

			stringBuilder.append(getThrown(logRecord));
			stringBuilder.append(formatDelimiter.delimiter);

			stringBuilder.append("\r\n");

		}

		return stringBuilder.toString();

	}

	/**
	 * Based on java.util.logging.SimpleFormatter source code.
	 */
	private String getThrown(final LogRecord logRecord) {

		if (Objects.isNull(logRecord.getThrown())) {
			return "";
		}

		try (StringWriter stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter)) {

			printWriter.println();
			printWriter.print("\t");

			logRecord.getThrown().printStackTrace(printWriter);

			printWriter.close();

			final String string = stringWriter.toString();

			return string.substring(0, string.length() - 2);

		} catch (final IOException exception) {
			exception.printStackTrace();
			return "Problems at myLogging.myFormatter.getThrown()";
		}

	}

}