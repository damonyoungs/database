package org.damon.database.config;

import ch.qos.logback.classic.spi.CallerData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.db.DBAppenderBase;
import ch.qos.logback.core.db.DBHelper;
import cn.hutool.core.date.DateUtil;
import org.damon.database.util.DateUtils;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * @Author chengrong.yang
 * @Date 2020/12/25 16:55
 */
@Configuration
public class LogDBAppender extends DBAppenderBase<ILoggingEvent> {

    protected static final Method GET_GENERATED_KEYS_METHOD;

    protected String insertSQL;

    static final int LEVEL = 1;

    static final int PROJECT_INDEX = 2;

    static final int CLASS_INDEX = 3;

    static final int CLASSPATH_INDEX = 4;

    static final int METHOD_INDEX = 5;

    static final int THREADNAME_INDEX = 6;

    static final int MSG_INDEX = 7;

    static final int CREATEDATE_INDEX = 8;

    static final StackTraceElement EMPTY_CALLER_DATA = CallerData.naInstance();

    static {
        Method getGeneratedKeysMethod;
        try {
            getGeneratedKeysMethod = PreparedStatement.class.getMethod("getGeneratedKeys", (Class<?>) null);
        } catch (Exception ex) {
            getGeneratedKeysMethod = null;
        }
        GET_GENERATED_KEYS_METHOD = getGeneratedKeysMethod;
    }

    @Override
    public void start() {
        insertSQL = buildInsertSQL();
        super.start();
    }

    private static String buildInsertSQL() {
        return  "INSERT INTO logging (level,project,class,classpath,method,thread_name,msg,created_time)" +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected Method getGeneratedKeysMethod() {
        return GET_GENERATED_KEYS_METHOD;
    }

    @Override
    protected String getInsertSQL() {
        return insertSQL;
    }


    @Override
    protected void subAppend(ILoggingEvent eventObject, Connection connection, PreparedStatement insertStatement) throws Throwable {
        bindLoggingMyInfoWithPreparedStatement(insertStatement, eventObject);
        int updateCount = insertStatement.executeUpdate();
        if (updateCount != 1) {
            addWarn("Failed to insert loggingEvent");
        }
    }

    /**
     * 主要修改的方法
     */
    private void bindLoggingMyInfoWithPreparedStatement(PreparedStatement stmt, ILoggingEvent event) throws SQLException {
        stmt.setString(LEVEL, event.getLevel().toString());
        stmt.setString(PROJECT_INDEX, event.getLoggerContextVO().getPropertyMap().get("app.name"));
        StackTraceElement caller = extractFirstCaller(event.getCallerData());
        stmt.setString(CLASS_INDEX, caller.getFileName());
        stmt.setString(CLASSPATH_INDEX, caller.getClassName());
        stmt.setString(METHOD_INDEX, caller.getMethodName());
        stmt.setString(THREADNAME_INDEX, event.getThreadName());
        stmt.setString(MSG_INDEX, event.getFormattedMessage());
        stmt.setString(CREATEDATE_INDEX, DateUtil.format(new Date(),DateUtils.DEFAULT_DATETIMEFORMAT));
    }

    private StackTraceElement extractFirstCaller(StackTraceElement[] callerDataArray) {
        StackTraceElement caller = EMPTY_CALLER_DATA;
        if (hasAtLeastOneNonNullElement(callerDataArray))
            caller = callerDataArray[0];
        return caller;
    }

    private boolean hasAtLeastOneNonNullElement(StackTraceElement[] callerDataArray) {
        return callerDataArray != null && callerDataArray.length > 0 && callerDataArray[0] != null;
    }

    @Override
    protected void secondarySubAppend(ILoggingEvent eventObject, Connection connection, long eventId) {
        //
    }

    @Override
    public void append(ILoggingEvent eventObject) {
        Connection connection = null;
        try {
            connection = connectionSource.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement insertStatement;
            insertStatement = connection.prepareStatement(getInsertSQL());
            synchronized (this) {
                subAppend(eventObject, connection, insertStatement);
            }
            insertStatement.close();
            connection.commit();
        } catch (Throwable sqle) {
            addError("problem appending event", sqle);
        } finally {
            DBHelper.closeConnection(connection);
        }
    }
}
