/*
 * Copyright (c) 2007 David Crawshaw <david@zentus.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package org.sqlite;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements a JDBC ResultSet.
 */
final class RS extends Unused implements ResultSet, ResultSetMetaData, Codes
{
    private final Stmt stmt;
    private final DB   db;

    boolean            open     = false; // true means have results and can iterate them
    int                maxRows;         // max. number of rows as set by a Statement
    String[]           cols     = null; // if null, the RS is closed()
    String[]           colsMeta = null; // same as cols, but used by Meta interface
    boolean[][]        meta     = null;

    private int        limitRows;       // 0 means no limit, must check against maxRows
    private int        row      = 0;    // number of current row, starts at 1 (0 is for before loading data)
    private int        lastCol;         // last column accessed, for wasNull(). -1 if none

    /**
     * Default constructor for a given statement.
     * @param stmt The statement.
     */
    RS(Stmt stmt) {
        this.stmt = stmt;
        this.db = stmt.db;
    }

    // INTERNAL FUNCTIONS ///////////////////////////////////////////

    /**
     * Checks the status of the result set.
     * @return True if has results and can iterate them; false otherwise.
     */
    boolean isOpen() {
        return open;
    }

    /**
     * @throws SQLException if ResultSet is not open.
     */
    void checkOpen() throws SQLException {
        if (!open) {
            throw new SQLException("ResultSet closed");
        }
    }

    /**
     * Takes col in [1,x] form, returns in [0,x-1] form
     * @param col
     * @return
     * @throws SQLException
     */
    private int checkCol(int col) throws SQLException {
        if (colsMeta == null) {
            throw new IllegalStateException("SQLite JDBC: inconsistent internal state");
        }
        if (col < 1 || col > colsMeta.length) {
            throw new SQLException("column " + col + " out of bounds [1," + colsMeta.length + "]");
        }
        return --col;
    }

    /**
     * Takes col in [1,x] form, marks it as last accessed and returns [0,x-1]
     * @param col
     * @return
     * @throws SQLException
     */
    private int markCol(int col) throws SQLException {
        checkOpen();
        checkCol(col);
        lastCol = col;
        return --col;
    }

    /**
     * @throws SQLException
     */
    private void checkMeta() throws SQLException {
        checkCol(1);
        if (meta == null) {
            meta = db.column_metadata(stmt.pointer);
        }
    }

    // ResultSet Functions //////////////////////////////////////////

    /**
     * @see java.sql.ResultSet#close()
     */
    public void close() throws SQLException {
        cols = null;
        colsMeta = null;
        meta = null;
        open = false;
        limitRows = 0;
        row = 0;
        lastCol = -1;

        if (stmt == null) {
            return;
        }
        if (stmt != null && stmt.pointer != 0) {
            db.reset(stmt.pointer);
        }
    }

    /**
     * returns col in [1,x] form
     * @see java.sql.ResultSet#findColumn(java.lang.String)
     */
    public int findColumn(String col) throws SQLException {
        checkOpen();
        int c = -1;
        for (int i = 0; i < cols.length; i++) {
            if (col.equalsIgnoreCase(cols[i])
                    || (cols[i].toUpperCase().endsWith(col.toUpperCase()) && cols[i].charAt(cols[i].length()
                            - col.length()) == '.')) {
                if (c == -1) {
                    c = i;
                }
                else {
                    throw new SQLException("ambiguous column: '" + col + "'");
                }
            }
        }
        if (c == -1) {
            throw new SQLException("no such column: '" + col + "'");
        }
        else {
            return c + 1;
        }
    }

    /**
     * @see java.sql.ResultSet#next()
     */
    public boolean next() throws SQLException {
        if (!open)
         {
            return false; // finished ResultSet
        }
        lastCol = -1;

        // first row is loaded by execute(), so do not step() again
        if (row == 0) {
            row++;
            return true;
        }

        // check if we are row limited by the statement or the ResultSet
        if (maxRows != 0 && row > maxRows) {
            return false;
        }

        // do the real work
        int statusCode = db.step(stmt.pointer);
        switch (statusCode) {
        case SQLITE_DONE:
            close(); // agressive closing to avoid writer starvation
            return false;
        case SQLITE_ROW:
            row++;
            return true;
        case SQLITE_BUSY:
        default:
            db.throwex(statusCode);
            return false;
        }
    }

    /**
     * @see java.sql.ResultSet#getType()
     */
    public int getType() throws SQLException {
        return TYPE_FORWARD_ONLY;
    }

    /**
     * @see java.sql.ResultSet#getFetchSize()
     */
    public int getFetchSize() throws SQLException {
        return limitRows;
    }

    /**
     * @see java.sql.ResultSet#setFetchSize(int)
     */
    public void setFetchSize(int rows) throws SQLException {
        if (0 > rows || (maxRows != 0 && rows > maxRows)) {
            throw new SQLException("fetch size " + rows + " out of bounds " + maxRows);
        }
        limitRows = rows;
    }

    /**
     * @see java.sql.ResultSet#getFetchDirection()
     */
    public int getFetchDirection() throws SQLException {
        checkOpen();
        return ResultSet.FETCH_FORWARD;
    }

    /**
     * @see java.sql.ResultSet#setFetchDirection(int)
     */
    public void setFetchDirection(int d) throws SQLException {
        checkOpen();
        if (d != ResultSet.FETCH_FORWARD) {
            throw new SQLException("only FETCH_FORWARD direction supported");
        }
    }

    /**
     * @see java.sql.ResultSet#isAfterLast()
     */
    public boolean isAfterLast() throws SQLException {
        return !open;
    }

    /**
     * @see java.sql.ResultSet#isBeforeFirst()
     */
    public boolean isBeforeFirst() throws SQLException {
        return open && row == 0;
    }

    /**
     * @see java.sql.ResultSet#isFirst()
     */
    public boolean isFirst() throws SQLException {
        return row == 1;
    }

    /**
     * @see java.sql.ResultSet#isLast()
     */
    public boolean isLast() throws SQLException { // FIXME
        throw new SQLException("function not yet implemented for SQLite");
    }

    /**
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws SQLException {
        close();
    }

    /**
     * @see java.sql.ResultSet#getRow()
     */
    public int getRow() throws SQLException {
        return row;
    }

    /**
     * @see java.sql.ResultSet#wasNull()
     */
    public boolean wasNull() throws SQLException {
        return db.column_type(stmt.pointer, markCol(lastCol)) == SQLITE_NULL;
    }

    // DATA ACCESS FUNCTIONS ////////////////////////////////////////

    /**
     * @see java.sql.ResultSet#getBigDecimal(int)
     */
    public BigDecimal getBigDecimal(int col) throws SQLException {
        final String stringValue = getString(col);
		if (stringValue == null) {
			return null;
		}
		else {
			try {
				return new BigDecimal(stringValue);
			}
			catch (NumberFormatException e) {
				throw new SQLException("Bad value for type BigDecimal : " + stringValue);
			}
		}
	}

    /**
     * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
     */
    public BigDecimal getBigDecimal(String col) throws SQLException {
        return getBigDecimal(findColumn(col));
    }

    /**
     * @see java.sql.ResultSet#getBoolean(int)
     */
    public boolean getBoolean(int col) throws SQLException {
        return getInt(col) == 0 ? false : true;
    }

    /**
     * @see java.sql.ResultSet#getBoolean(java.lang.String)
     */
    public boolean getBoolean(String col) throws SQLException {
        return getBoolean(findColumn(col));
    }

    /**
     * @see java.sql.ResultSet#getByte(int)
     */
    public byte getByte(int col) throws SQLException {
        return (byte) getInt(col);
    }

    /**
     * @see java.sql.ResultSet#getByte(java.lang.String)
     */
    public byte getByte(String col) throws SQLException {
        return getByte(findColumn(col));
    }

    /**
     * @see java.sql.ResultSet#getBytes(int)
     */
    public byte[] getBytes(int col) throws SQLException {
        return db.column_blob(stmt.pointer, markCol(col));
    }

    /**
     * @see java.sql.ResultSet#getBytes(java.lang.String)
     */
    public byte[] getBytes(String col) throws SQLException {
        return getBytes(findColumn(col));
    }

    /**
     * @see java.sql.ResultSet#getCharacterStream(int)
     */
    public Reader getCharacterStream(int col) throws SQLException {
        return new StringReader(getString(col));
    }

    /**
     * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
     */
    public Reader getCharacterStream(String col) throws SQLException {
        return getCharacterStream(findColumn(col));
    }

    /**
     * @see java.sql.ResultSet#getDate(int)
     */
    public Date getDate(int col) throws SQLException {
        if (db.column_type(stmt.pointer, markCol(col)) == SQLITE_NULL) {
            return null;
        }
        return new Date(db.column_long(stmt.pointer, markCol(col)));
    }

    /**
     * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
     */
    public Date getDate(int col, Calendar cal) throws SQLException {
        if (db.column_type(stmt.pointer, markCol(col)) == SQLITE_NULL) {
            return null;
        }
        if (cal == null) {
            return getDate(col);
        }
        cal.setTimeInMillis(db.column_long(stmt.pointer, markCol(col)));
        return new Date(cal.getTime().getTime());
    }

    /**
     * @see java.sql.ResultSet#getDate(java.lang.String)
     */
    public Date getDate(String col) throws SQLException {
        return getDate(findColumn(col), Calendar.getInstance());
    }

    /**
     * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
     */
    public Date getDate(String col, Calendar cal) throws SQLException {
        return getDate(findColumn(col), cal);
    }

    /**
     * @see java.sql.ResultSet#getDouble(int)
     */
    public double getDouble(int col) throws SQLException {
        if (db.column_type(stmt.pointer, markCol(col)) == SQLITE_NULL) {
            return 0;
        }
        return db.column_double(stmt.pointer, markCol(col));
    }

    /**
     * @see java.sql.ResultSet#getDouble(java.lang.String)
     */
    public double getDouble(String col) throws SQLException {
        return getDouble(findColumn(col));
    }

    /**
     * @see java.sql.ResultSet#getFloat(int)
     */
    public float getFloat(int col) throws SQLException {
        if (db.column_type(stmt.pointer, markCol(col)) == SQLITE_NULL) {
            return 0;
        }
        return (float) db.column_double(stmt.pointer, markCol(col));
    }

    /**
     * @see java.sql.ResultSet#getFloat(java.lang.String)
     */
    public float getFloat(String col) throws SQLException {
        return getFloat(findColumn(col));
    }

    /**
     * @see java.sql.ResultSet#getInt(int)
     */
    public int getInt(int col) throws SQLException {
        return db.column_int(stmt.pointer, markCol(col));
    }

    /**
     * @see java.sql.ResultSet#getInt(java.lang.String)
     */
    public int getInt(String col) throws SQLException {
        return getInt(findColumn(col));
    }

    /**
     * @see java.sql.ResultSet#getLong(int)
     */
    public long getLong(int col) throws SQLException {
        return db.column_long(stmt.pointer, markCol(col));
    }

    /**
     * @see java.sql.ResultSet#getLong(java.lang.String)
     */
    public long getLong(String col) throws SQLException {
        return getLong(findColumn(col));
    }

    /**
     * @see java.sql.ResultSet#getShort(int)
     */
    public short getShort(int col) throws SQLException {
        return (short) getInt(col);
    }

    /**
     * @see java.sql.ResultSet#getShort(java.lang.String)
     */
    public short getShort(String col) throws SQLException {
        return getShort(findColumn(col));
    }

    /**
     * @see java.sql.ResultSet#getString(int)
     */
    public String getString(int col) throws SQLException {
        return db.column_text(stmt.pointer, markCol(col));
    }

    /**
     * @see java.sql.ResultSet#getString(java.lang.String)
     */
    public String getString(String col) throws SQLException {
        return getString(findColumn(col));
    }

    /**
     * @see java.sql.ResultSet#getTime(int)
     */
    public Time getTime(int col) throws SQLException {
        if (db.column_type(stmt.pointer, markCol(col)) == SQLITE_NULL) {
            return null;
        }
        return new Time(db.column_long(stmt.pointer, markCol(col)));
    }

    /**
     * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
     */
    public Time getTime(int col, Calendar cal) throws SQLException {
        if (cal == null) {
            return getTime(col);
        }
        if (db.column_type(stmt.pointer, markCol(col)) == SQLITE_NULL) {
            return null;
        }
        cal.setTimeInMillis(db.column_long(stmt.pointer, markCol(col)));
        return new Time(cal.getTime().getTime());
    }

    /**
     * @see java.sql.ResultSet#getTime(java.lang.String)
     */
    public Time getTime(String col) throws SQLException {
        return getTime(findColumn(col));
    }

    /**
     * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
     */
    public Time getTime(String col, Calendar cal) throws SQLException {
        return getTime(findColumn(col), cal);
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(int)
     */
    public Timestamp getTimestamp(int col) throws SQLException {
        if (db.column_type(stmt.pointer, markCol(col)) == SQLITE_NULL) {
            return null;
        }
        return new Timestamp(db.column_long(stmt.pointer, markCol(col)));
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
     */
    public Timestamp getTimestamp(int col, Calendar cal) throws SQLException {
        if (cal == null) {
            return getTimestamp(col);
        }
        if (db.column_type(stmt.pointer, markCol(col)) == SQLITE_NULL) {
            return null;
        }
        cal.setTimeInMillis(db.column_long(stmt.pointer, markCol(col)));
        return new Timestamp(cal.getTime().getTime());
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(java.lang.String)
     */
    public Timestamp getTimestamp(String col) throws SQLException {
        return getTimestamp(findColumn(col));
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
     */
    public Timestamp getTimestamp(String c, Calendar ca) throws SQLException {
        return getTimestamp(findColumn(c), ca);
    }

    /**
     * @see java.sql.ResultSet#getObject(int)
     */
    public Object getObject(int col) throws SQLException {
        switch (db.column_type(stmt.pointer, checkCol(col))) {
        case SQLITE_INTEGER:
            long val = getLong(col);
            if (val > Integer.MAX_VALUE || val < Integer.MIN_VALUE) {
                return new Long(val);
            }
            else {
                return new Integer((int) val);
            }
        case SQLITE_FLOAT:
            return new Double(getDouble(col));
        case SQLITE_BLOB:
            return getBytes(col);
        case SQLITE_NULL:
            return null;
        case SQLITE_TEXT:
        default:
            return getString(col);
        }
    }

    /**
     * @see java.sql.ResultSet#getObject(java.lang.String)
     */
    public Object getObject(String col) throws SQLException {
        return getObject(findColumn(col));
    }

    /**
     * @see java.sql.ResultSet#getStatement()
     */
    public Statement getStatement() {
        return stmt;
    }

    /**
     * @see java.sql.ResultSet#getCursorName()
     */
    public String getCursorName() throws SQLException {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getWarnings()
     */
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    /**
     * @see java.sql.ResultSet#clearWarnings()
     */
    public void clearWarnings() throws SQLException {}

    // ResultSetMetaData Functions //////////////////////////////////

    /**
     * Pattern used to extract the column type name from table column definition.
     */
    protected final static Pattern COLUMN_TYPENAME = Pattern.compile("([^\\(]*)");

    /**
     * Pattern used to extract the column type name from a cast(col as type)
     */
    protected final static Pattern COLUMN_TYPECAST = Pattern.compile("cast\\(.*?\\s+as\\s+(.*?)\\s*\\)");

    /**
     * Pattern used to extract the precision and scale from column meta returned by the JDBC driver.
     */
    protected final static Pattern COLUMN_PRECISION = Pattern.compile(".*?\\(((.*?)|\2,\2)\\)");

    // we do not need to check the RS is open, only that colsMeta
    // is not null, done with checkCol(int).

    /**
     * @see java.sql.ResultSet#getMetaData()
     */
    public ResultSetMetaData getMetaData() throws SQLException {
        return this;
    }

    /**
     * @see java.sql.ResultSetMetaData#getCatalogName(int)
     */
    public String getCatalogName(int col) throws SQLException {
        return db.column_table_name(stmt.pointer, checkCol(col));
    }

    /**
     * @see java.sql.ResultSetMetaData#getColumnClassName(int)
     */
    public String getColumnClassName(int col) throws SQLException {
        checkCol(col);
        return "java.lang.Object";
    }

    /**
     * @see java.sql.ResultSetMetaData#getColumnCount()
     */
    public int getColumnCount() throws SQLException {
        checkCol(1);
        return colsMeta.length;
    }

    /**
     * @see java.sql.ResultSetMetaData#getColumnDisplaySize(int)
     */
    public int getColumnDisplaySize(int col) throws SQLException {
        return Integer.MAX_VALUE;
    }

    /**
     * @see java.sql.ResultSetMetaData#getColumnLabel(int)
     */
    public String getColumnLabel(int col) throws SQLException {
        return getColumnName(col);
    }

    /**
     * @see java.sql.ResultSetMetaData#getColumnName(int)
     */
    public String getColumnName(int col) throws SQLException {
        return db.column_name(stmt.pointer, checkCol(col));
    }

    /**
     * @see java.sql.ResultSetMetaData#getColumnType(int)
     */
    public int getColumnType(int col) throws SQLException {
        String typeName = getColumnTypeName(col);
        int valueType = db.column_type(stmt.pointer, checkCol(col));

        if (valueType == SQLITE_INTEGER || valueType == SQLITE_NULL) {
            if ("BOOLEAN".equals(typeName)) {
                return Types.BOOLEAN;
            }

            if ("TINYINT".equals(typeName)) {
                return Types.TINYINT;
            }

            if ("SMALLINT".equals(typeName) || "INT2".equals(typeName)) {
                return Types.SMALLINT;
            }

            if ("BIGINT".equals(typeName) || "INT8".equals(typeName) ||
                "UNSIGNED BIG INT".equals(typeName)) {
                return  Types.BIGINT;
            }

            if ("DATE".equals(typeName) || "DATETIME".equals(typeName)) {
                return Types.DATE;
            }
    
            if (valueType == SQLITE_INTEGER ||
                "INT".equals(typeName) ||
                "INTEGER".equals(typeName) ||
                "MEDIUMINT".equals(typeName)) {
                return Types.INTEGER;
            }
        }

        if (valueType == SQLITE_FLOAT || valueType == SQLITE_NULL) {
            if ("DECIMAL".equals(typeName)) {
                return Types.DECIMAL;
            }

            if ("DOUBLE".equals(typeName) || "DOUBLE PRECISION".equals(typeName)) {
                return Types.DOUBLE;
            }

            if ("NUMERIC".equals(typeName)) {
                return Types.NUMERIC;
            }

            if ("REAL".equals(typeName)) {
                return Types.REAL;
            }
    
            if (valueType == SQLITE_FLOAT ||
                "FLOAT".equals(typeName)) {
                return Types.FLOAT;
            }
        }

        if (valueType == SQLITE_TEXT || valueType == SQLITE_NULL) {
            if ("CHARACTER".equals(typeName) || "NCHAR".equals(typeName) ||
                "NATIVE CHARACTER".equals(typeName)) {
                return Types.CHAR;
            }

            if ("CLOB".equals(typeName)) {
                return Types.CLOB;
            }

            if ("DATE".equals(typeName) || "DATETIME".equals(typeName)) {
                return Types.DATE;
            }
    
            if (valueType == SQLITE_TEXT ||
                "VARCHAR".equals(typeName) ||
                "VARYING CHARACTER".equals(typeName) ||
                "NVARCHAR".equals(typeName) ||
                "TEXT".equals(typeName)) {
                    return Types.VARCHAR;
            }
        }

        if (valueType == SQLITE_BLOB || valueType == SQLITE_NULL) {
            if ("BINARY".equals(typeName)) {
                return Types.BINARY;
            }

            if (valueType == SQLITE_BLOB ||
                "BLOB".equals(typeName)) {
                return Types.BLOB;
            }
        }

        return Types.NULL;
    }

    /**
     * @return The data type from either the 'create table' statement,
     * or CAST(expr AS TYPE) otherwise sqlite3_value_type.
     * @see java.sql.ResultSetMetaData#getColumnTypeName(int)
     */
    public String getColumnTypeName(int col) throws SQLException {
        String declType = getColumnDeclType(col);

        if (declType != null) {
            Matcher matcher = COLUMN_TYPENAME.matcher(declType);

            matcher.find();
            return matcher.group(1).toUpperCase(Locale.ENGLISH);
        }

        switch (db.column_type(stmt.pointer, checkCol(col))) {
        case SQLITE_INTEGER:
            return "INTEGER";
        case SQLITE_FLOAT:
            return "FLOAT";
        case SQLITE_BLOB:
            return "BLOB";
        case SQLITE_NULL:
            return "NULL";
        case SQLITE_TEXT:
        default:
            return "NULL";
        }
    }

    /**
     * @see java.sql.ResultSetMetaData#getPrecision(int)
     */
    public int getPrecision(int col) throws SQLException {
        String declType = getColumnDeclType(col);

        if (declType != null) {
            Matcher matcher = COLUMN_PRECISION.matcher(declType);

            return matcher.find() ? Integer.parseInt(matcher.group(1).split(",")[0]) : 0;
        }

        return 0;
    }

    private String getColumnDeclType(int col) throws SQLException {
        String declType = db.column_decltype(stmt.pointer, checkCol(col));

        if (declType == null) {
            Matcher matcher = COLUMN_TYPECAST.matcher(db.column_name(stmt.pointer, checkCol(col)));
            declType = matcher.find() ? matcher.group(1) : null;
        }

        return declType;
    }
    /**
     * @see java.sql.ResultSetMetaData#getScale(int)
     */
    public int getScale(int col) throws SQLException {
        String declType = getColumnDeclType(col);

        if (declType != null) {
            Matcher matcher = COLUMN_PRECISION.matcher(declType);

            return matcher.find() ? Integer.parseInt(matcher.group(1).split(",")[1]) : 0;
        }

        return 0;
    }

    /**
     * @see java.sql.ResultSetMetaData#getSchemaName(int)
     */
    public String getSchemaName(int col) throws SQLException {
        return "";
    }

    /**
     * @see java.sql.ResultSetMetaData#getTableName(int)
     */
    public String getTableName(int col) throws SQLException {
        return db.column_table_name(stmt.pointer, checkCol(col));
    }

    /**
     * @see java.sql.ResultSetMetaData#isNullable(int)
     */
    public int isNullable(int col) throws SQLException {
        checkMeta();
        return meta[checkCol(col)][1] ? columnNoNulls : columnNullable;
    }

    /**
     * @see java.sql.ResultSetMetaData#isAutoIncrement(int)
     */
    public boolean isAutoIncrement(int col) throws SQLException {
        checkMeta();
        return meta[checkCol(col)][2];
    }

    /**
     * @see java.sql.ResultSetMetaData#isCaseSensitive(int)
     */
    public boolean isCaseSensitive(int col) throws SQLException {
        return true;
    }

    /**
     * @see java.sql.ResultSetMetaData#isCurrency(int)
     */
    public boolean isCurrency(int col) throws SQLException {
        return false;
    }

    /**
     * @see java.sql.ResultSetMetaData#isDefinitelyWritable(int)
     */
    public boolean isDefinitelyWritable(int col) throws SQLException {
        return true;
    } // FIXME: check db file constraints?

    /**
     * @see java.sql.ResultSetMetaData#isReadOnly(int)
     */
    public boolean isReadOnly(int col) throws SQLException {
        return false;
    }

    /**
     * @see java.sql.ResultSetMetaData#isSearchable(int)
     */
    public boolean isSearchable(int col) throws SQLException {
        return true;
    }

    /**
     * @see java.sql.ResultSetMetaData#isSigned(int)
     */
    public boolean isSigned(int col) throws SQLException {
        return false;
    }

    /**
     * @see java.sql.ResultSetMetaData#isWritable(int)
     */
    public boolean isWritable(int col) throws SQLException {
        return true;
    }

    /**
     * @see java.sql.ResultSet#getConcurrency()
     */
    public int getConcurrency() throws SQLException {
        return CONCUR_READ_ONLY;
    }

    /**
     * @see java.sql.ResultSet#rowDeleted()
     */
    public boolean rowDeleted() throws SQLException {
        return false;
    }

    /**
     * @see java.sql.ResultSet#rowInserted()
     */
    public boolean rowInserted() throws SQLException {
        return false;
    }

    /**
     * @see java.sql.ResultSet#rowUpdated()
     */
    public boolean rowUpdated() throws SQLException {
        return false;
    }
}
