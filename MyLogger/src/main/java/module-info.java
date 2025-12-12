module cookies.made.home.myLogging {

	exports myLogging.myAdapters;
	exports myLogging.myCleanup;
	exports myLogging.myFormatters;
	exports myLogging.myHandlerRecords;
	exports myLogging.myHandlerTypes;
	exports myLogging.myStandardInterfaces;
	exports myLogging.myStandardLevels;
	exports myLogging;

	requires transitive java.desktop;
	requires transitive java.logging;

}