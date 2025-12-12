package myLogging.myHandlerRecords;

import java.util.logging.Handler;

import myLogging.myHandlerTypes.MyHandlerType;

public record MyHandlerRecord(Handler handler, MyHandlerType MyHandlerType) {

}
