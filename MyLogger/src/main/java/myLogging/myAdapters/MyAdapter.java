package myLogging.myAdapters;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 *
 * An empty implementation of the java.util.logging.Handler class, provided as a
 * convenience to simplify the task of creating handlers, by extending and
 * implementing only the methods of interest.
 *
 * @author eitan
 *
 */
public abstract class MyAdapter extends Handler {

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	@Override
	public abstract void publish(LogRecord logRecord);

}
