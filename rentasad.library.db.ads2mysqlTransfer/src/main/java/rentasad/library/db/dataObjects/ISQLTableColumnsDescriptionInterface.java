package rentasad.library.db.dataObjects;

import java.sql.Time;
import java.util.Date;
/**
 * 
 * Gustini GmbH (2015)
 * Creation: 16.02.2015
 * Rentasad Library
 * rentasad.lib.dbtransfer2.dataobjects
 *
 * @author Matthias Staud
 *
 *
 * Description: 
 *
 */
public interface ISQLTableColumnsDescriptionInterface
{
    public static final int LENGTH_IS_NOT_DEFINED = -999999;
    public static final int OBJECT_DATA_TYPE_INT = 0;
    
    public static final int OBJECT_DATA_TYPE_STRING = 1;
    public static final int OBJECT_DATA_TYPE_DATE = 2;
    public static final int OBJECT_DATA_TYPE_BOOLEAN = 3;
    public static final int OBJECT_DATA_TYPE_FLOAT = 4;
    public static final int OBJECT_DATA_TYPE_DOUBLE = 5;
    public static final int OBJECT_DATA_TYPE_TEXT = 16;
        
    
    public static final int OBJECT_DATA_TYPE_UNDEFINED = 999;
    
    public final static String[] DATATYP_NAME_ARRAY = {
        "BIT",
        "TINYINT",
        "BOOL",
        "SMALLINT",
        "MEDIUMINT",
        "INT",
        "BIGINT",
        "DECIMAL",
        "FLOAT",
        "DOUBLE",
        "DATE",
        "DATETIME",
        "TIMESTAMP",
        "TIME",
        "YEAR",
        "VARCHAR",
        "LONGTEXT",
        "TEXT",
        "CHAR"
    };
    
    /*
     * 11.1 Data Type Overview [+/-]
     */

    /*
     * 11.2 Numeric Types [+/-]
     * 11.3 Date and Time Types [+/-]
     * 11.4 String Types [+/-]
     * 11.5 Extensions for Spatial Data [+/-]
     * 11.6 Data Type Default Values
     * 11.7 Data Type Storage Requirements
     * 11.8 Choosing the Right Type for a Column
     * 11.9 Using Data Types from Other Database Engines
     */
    /*
     * 11.2 Numeric Types [+/-]
     */

    /**
     * A bit-field type. M indicates the number of bits per value, from 1 to 64.
     * The default is 1 if M is omitted.
     * 
     * This data type was added in MySQL 5.0.3 for MyISAM, and extended in 5.0.5
     * to MEMORY, InnoDB, BDB, and NDBCLUSTER. Before 5.0.3, BIT is a synonym
     * for TINYINT(1).
     */
    public final static int DATATYPE_NUM_BIT = 0;
    /**
     * A very small integer. The signed range is -128 to 127. The unsigned range
     * is 0 to 255.
     */
    public final static int DATATYPE_NUM_TINYINT = 1;
    /**
     * These types are synonyms for TINYINT(1). A value of zero is considered
     * false. Nonzero values are considered true:
     * 
     * mysql> SELECT IF(0, 'true', 'false');
     * +------------------------+
     * | IF(0, 'true', 'false') |
     * +------------------------+
     * | false |
     * +------------------------+
     * 
     * mysql> SELECT IF(1, 'true', 'false');
     * +------------------------+
     * | IF(1, 'true', 'false') |
     * +------------------------+
     * | true |
     * +------------------------+
     * 
     * mysql> SELECT IF(2, 'true', 'false');
     * +------------------------+
     * | IF(2, 'true', 'false') |
     * +------------------------+
     * | true |
     * +------------------------+
     * 
     * However, the values TRUE and FALSE are merely aliases for 1 and 0,
     * respectively, as shown here:
     * 
     * mysql> SELECT IF(0 = FALSE, 'true', 'false');
     * +--------------------------------+
     * | IF(0 = FALSE, 'true', 'false') |
     * +--------------------------------+
     * | true |
     * +--------------------------------+
     * 
     * mysql> SELECT IF(1 = TRUE, 'true', 'false');
     * +-------------------------------+
     * | IF(1 = TRUE, 'true', 'false') |
     * +-------------------------------+
     * | true |
     * +-------------------------------+
     * 
     * mysql> SELECT IF(2 = TRUE, 'true', 'false');
     * +-------------------------------+
     * | IF(2 = TRUE, 'true', 'false') |
     * +-------------------------------+
     * | false |
     * +-------------------------------+
     * 
     * mysql> SELECT IF(2 = FALSE, 'true', 'false');
     * +--------------------------------+
     * | IF(2 = FALSE, 'true', 'false') |
     * +--------------------------------+
     * | false |
     * +--------------------------------+
     * 
     * The last two statements display the results shown because 2 is equal to
     * neither 1 nor 0.
     */
    public final static int DATATYPE_NUM_BOOL = 2;
    /**
     * A small integer. The signed range is -32768 to 32767. The unsigned range
     * is 0 to 65535.
     */
    public final static int DATATYPE_NUM_SMALLINT = 3;
    /**
     * A medium-sized integer. The signed range is -8388608 to 8388607. The
     * unsigned range is 0 to 16777215.
     */
    public final static int DATATYPE_NUM_MEDIUMINT = 4;
    /**
     * A normal-size integer. The signed range is -2147483648 to 2147483647. The
     * unsigned range is 0 to
     */
    public final static int DATATYPE_NUM_INT = 5;
    /**
     * A large integer. The signed range is -9223372036854775808 to
     * 9223372036854775807. The unsigned range is 0 to 18446744073709551615.
     * SERIAL is an alias for BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE.
     * 
     * Some things you should be aware of with respect to BIGINT columns:
     * 
     * All arithmetic is done using signed BIGINT or DOUBLE values, so you
     * should not use unsigned big integers larger than 9223372036854775807 (63
     * bits) except with bit functions! If you do that, some of the last digits
     * in the result may be wrong because of rounding errors when converting a
     * BIGINT value to a DOUBLE.
     * 
     * MySQL can handle BIGINT in the following cases:
     * 
     * When using integers to store large unsigned values in a BIGINT column.
     *
     * In MIN(col_name) or MAX(col_name), where col_name refers to a BIGINT
     * column.
     * 
     * When using operators (+, -, *, and so on) where both operands are
     * integers.
     * 
     * You can always store an exact integer value in a BIGINT column by storing
     * it using a string. In this case, MySQL performs a string-to-number
     * conversion that involves no intermediate double-precision representation.
     * 
     * The -, +, and * operators use BIGINT arithmetic when both operands are
     * integer values. This means that if you multiply two big integers (or
     * results from functions that return integers), you may get unexpected
     * results when the result is larger than 9223372036854775807.
     */
    public final static int DATATYPE_NUM_BIGINT = 6;
    /**
     * For MySQL 5.0.3 and above:
     * 
     * A packed "exact" fixed-point number. M is the total number of digits (the
     * precision) and D is the number of digits after the decimal point (the
     * scale). The decimal point and (for negative numbers) the "-" sign are not
     * counted in M. If D is 0, values have no decimal point or fractional part.
     * The maximum number of digits (M) for DECIMAL is 65 (64 from 5.0.3 to
     * 5.0.5). The maximum number of supported decimals (D) is 30. If D is
     * omitted, the default is 0. If M is omitted, the default is 10.
     * 
     * UNSIGNED, if specified, disallows negative values.
     * 
     * All basic calculations (+, -, *, /) with DECIMAL columns are done with a
     * precision of 65 digits.
     * 
     * Before MySQL 5.0.3:
     * 
     * An unpacked fixed-point number. Behaves like a CHAR column; "unpacked"
     * means the number is stored as a string, using one character for each
     * digit of the value. M is the total number of digits and D is the number
     * of digits after the decimal point. The decimal point and (for negative
     * numbers) the "-" sign are not counted in M, although space for them is
     * reserved. If D is 0, values have no decimal point or fractional part. The
     * maximum range of DECIMAL values is the same as for DOUBLE, but the actual
     * range for a given DECIMAL column may be constrained by the choice of M
     * and D. If D is omitted, the default is 0. If M is omitted, the default is
     * 10.
     * 
     * UNSIGNED, if specified, disallows negative values.
     * 
     * The behavior used by the server for DECIMAL columns in a table depends on
     * the version of MySQL used to create the table. If your server is from
     * MySQL 5.0.3 or higher, but you have DECIMAL columns in tables that were
     * created before 5.0.3, the old behavior still applies to those columns. To
     * convert the tables to the newer DECIMAL format, dump them with mysqldump
     * and reload them.
     */
    public final static int DATATYPE_NUM_DECIMAL = 7;
    /**
     * A small (single-precision) floating-point number. Permissible values are
     * -3.402823466E+38 to -1.175494351E-38, 0, and 1.175494351E-38 to
     * 3.402823466E+38. These are the theoretical limits, based on the IEEE
     * standard. The actual range might be slightly smaller depending on your
     * hardware or operating system.
     * 
     * M is the total number of digits and D is the number of digits following
     * the decimal point. If M and D are omitted, values are stored to the
     * limits permitted by the hardware. A single-precision floating-point
     * number is accurate to approximately 7 decimal places.
     * 
     * UNSIGNED, if specified, disallows negative values.
     * 
     * Using FLOAT might give you some unexpected problems because all
     * calculations in MySQL are done with double precision. See Section
     * B.5.5.7, "Solving Problems with No Matching Rows".
     */
    public final static int DATATYPE_NUM_FLOAT = 8;
    /**
     * A normal-size (double-precision) floating-point number. Permissible
     * values are -1.7976931348623157E+308 to -2.2250738585072014E-308, 0, and
     * 2.2250738585072014E-308 to 1.7976931348623157E+308. These are the
     * theoretical limits, based on the IEEE standard. The actual range might be
     * slightly smaller depending on your hardware or operating system.
     * 
     * M is the total number of digits and D is the number of digits following
     * the decimal point. If M and D are omitted, values are stored to the
     * limits permitted by the hardware. A double-precision floating-point
     * number is accurate to approximately 15 decimal places.
     * 
     * UNSIGNED, if specified, disallows negative values.
     */
    public final static int DATATYPE_NUM_DOUBLE = 9;

    /*
     * 11.3 Date and Time Types [+/-]
     */
    /**
     * A date. The supported range is '1000-01-01' to '9999-12-31'. MySQL
     * displays DATE values in 'YYYY-MM-DD' format, but permits assignment of
     * values to DATE columns using either strings or numbers.
     */
    public final static int DATATYPE_DATE_DATE = 10;
    /**
     * A date and time combination. The supported range is '1000-01-01 00:00:00'
     * to '9999-12-31 23:59:59'. MySQL displays DATETIME values in 'YYYY-MM-DD
     * HH:MM:SS' format, but permits assignment of values to DATETIME columns
     * using either strings or numbers.
     */
    public final static int DATATYPE_DATE_DATETIME = 11;
    /**
     * A timestamp. The range is '1970-01-01 00:00:01' UTC to '2038-01-19
     * 03:14:07' UTC. TIMESTAMP values are stored as the number of seconds since
     * the epoch ('1970-01-01 00:00:00' UTC). A TIMESTAMP cannot represent the
     * value '1970-01-01 00:00:00' because that is equivalent to 0 seconds from
     * the epoch and the value 0 is reserved for representing '0000-00-00
     * 00:00:00', the "zero" TIMESTAMP value.
     * 
     * Unless specified otherwise, the first TIMESTAMP column in a table is
     * defined to be automatically set to the date and time of the most recent
     * modification if not explicitly assigned a value. This makes TIMESTAMP
     * useful for recording the timestamp of an INSERT or UPDATE operation. You
     * can also set any TIMESTAMP column to the current date and time by
     * assigning it a NULL value, unless it has been defined with the NULL
     * attribute to permit NULL values. The automatic initialization and
     * updating to the current date and time can be specified using DEFAULT
     * CURRENT_TIMESTAMP and ON UPDATE CURRENT_TIMESTAMP clauses, as described
     * in Section 11.3.5, "Automatic Initialization and Updating for TIMESTAMP".
     * Note
     * 
     * The TIMESTAMP format that was used prior to MySQL 4.1 is not supported in
     * MySQL 5.0; see MySQL 3.23, 4.0, 4.1 Reference Manual for information
     * regarding the old format.
     */
    public final static int DATATYPE_DATE_TIMESTAMP = 12;
    /**
     * A time. The range is '-838:59:59' to '838:59:59'. MySQL displays TIME
     * values in 'HH:MM:SS' format, but permits assignment of values to TIME
     * columns using either strings or numbers.
     */
    public final static int DATATYPE_DATE_TIME = 13;
    /**
     * A year in two-digit or four-digit format. The default is four-digit
     * format. YEAR(2) or YEAR(4) differ in display format, but have the same
     * range of values. In four-digit format, values display as 1901 to 2155,
     * and 0000. In two-digit format, values display as 70 to 69, representing
     * years from 1970 to 2069. MySQL displays YEAR values in YYYY or YY format,
     * but permits assignment of values to YEAR columns using either strings or
     * numbers.
     * 
     * For additional information about YEAR display format and interpretation
     * of input values, see Section 11.3.3, "The YEAR Type".
     * 
     * The SUM() and AVG() aggregate functions do not work with temporal values.
     * (They convert the values to numbers, losing everything after the first
     * nonnumeric character.) To work around this problem, convert to numeric
     * units, perform the aggregate operation, and convert back to a temporal
     * value. Examples:
     * 
     * SELECT SEC_TO_TIME(SUM(TIME_TO_SEC(time_col))) FROM tbl_name;
     * SELECT FROM_DAYS(SUM(TO_DAYS(date_col))) FROM tbl_name;
     * 
     * Note
     * 
     * The MySQL server can be run with the MAXDB SQL mode enabled. In this
     * case, TIMESTAMP is identical with DATETIME. If this mode is enabled at
     * the time that a table is created, TIMESTAMP columns are created as
     * DATETIME columns. As a result, such columns use DATETIME display format,
     * have the same range of values, and there is no automatic initialization
     * or updating to the current date and time. See Section 5.1.7, "Server SQL
     * Modes".
     */
    public final static int DATATYPE_DATE_YEAR = 14;
    
    
    /*
     * 11.1.3 String Type Overview
     */
    public final static int DATATYPE_STRING_VARCHAR = 15;
    
    public final static int DATATYPE_STRING_LONGTEXT = 16;

    public final static int DATATYPE_UNDEFINED = 999;

    /**
     * indicates the column name.
     * 
     * @return the field
     */
    public abstract String getField();

    /**
     * indicates the column name.
     * 
     * @param field
     *            the field to set
     */
    public abstract void setField(String field);

    /**
     * indicates the column data type.
     * 
     * @return the type
     */
    public abstract int getType();

    /**
     * indicates the column data type.
     * 
     * @param type
     *            the type to set
     */
    public abstract void setType(int type);

    /**
     * indicates the collation for nonbinary string columns, or NULL for other
     * columns. This value is displayed only if you use the FULL keyword.
     * 
     * @return the collation
     */
    public abstract String getCollation();

    /**
     * indicates the collation for nonbinary string columns, or NULL for other
     * columns. This value is displayed only if you use the FULL keyword.
     * 
     * @param collation
     *            the collation to set
     */
    public abstract void setCollation(String collation);

    /**
     * @return the length
     */
    public abstract int getLength();

    /**
     * @param length
     *            the length to set
     */
    public abstract void setLength(int length);

    /**
     * The Null field contains YES if NULL values can be stored in the column.
     * If not, the column contains NO as of
     * 
     * @return the canBeNull
     */
    public abstract boolean isCanBeNull();

    /**
     * The Null field contains YES if NULL values can be stored in the column.
     * If not, the column contains NO as of
     * 
     * @param canBeNull
     *            the canBeNull to set
     */
    public abstract void setCanBeNull(boolean canBeNull);

    /**
     * The Key field indicates whether the column is indexed:
     * 
     * If Key is empty, the column either is not indexed or is indexed only as a
     * secondary column in a multiple-column, nonunique index.
     * 
     * If Key is PRI, the column is a PRIMARY KEY or is one of the columns in a
     * multiple-column PRIMARY KEY.
     * 
     * If Key is UNI, the column is the first column of a unique-valued index
     * that cannot contain NULL values.
     * 
     * If Key is MUL, multiple occurrences of a given value are permitted within
     * the column. The column is the first column of a nonunique index or a
     * unique-valued index that can contain NULL values.
     * 
     * If more than one of the Key values applies to a given column of a table,
     * Key displays the one with the highest priority, in the order PRI, UNI,
     * MUL.
     * 
     * A UNIQUE index may be displayed as PRI if it cannot contain NULL values
     * and there is no PRIMARY KEY in the table. A UNIQUE index may display as
     * MUL if several columns form a composite UNIQUE index; although the
     * combination of the columns is unique, each column can still hold multiple
     * occurrences of a given value.
     * 
     * Before MySQL 5.0.11, if the column permits NULL values, the Key value can
     * be MUL even when a single-column UNIQUE index is used. The rationale was
     * that multiple rows in a UNIQUE index can hold a NULL value if the column
     * is not declared NOT NULL. As of MySQL 5.0.11, the display is UNI rather
     * than MUL regardless of whether the column permits NULL; you can see from
     * the Null field whether or not the column can contain NULL.
     * 
     * @return the key
     */
    public abstract String getKey();

    /**
     * The Key field indicates whether the column is indexed:
     * 
     * If Key is empty, the column either is not indexed or is indexed only as a
     * secondary column in a multiple-column, nonunique index.
     * 
     * If Key is PRI, the column is a PRIMARY KEY or is one of the columns in a
     * multiple-column PRIMARY KEY.
     * 
     * If Key is UNI, the column is the first column of a unique-valued index
     * that cannot contain NULL values.
     * 
     * If Key is MUL, multiple occurrences of a given value are permitted within
     * the column. The column is the first column of a nonunique index or a
     * unique-valued index that can contain NULL values.
     * 
     * If more than one of the Key values applies to a given column of a table,
     * Key displays the one with the highest priority, in the order PRI, UNI,
     * MUL.
     * 
     * A UNIQUE index may be displayed as PRI if it cannot contain NULL values
     * and there is no PRIMARY KEY in the table. A UNIQUE index may display as
     * MUL if several columns form a composite UNIQUE index; although the
     * combination of the columns is unique, each column can still hold multiple
     * occurrences of a given value.
     * 
     * Before MySQL 5.0.11, if the column permits NULL values, the Key value can
     * be MUL even when a single-column UNIQUE index is used. The rationale was
     * that multiple rows in a UNIQUE index can hold a NULL value if the column
     * is not declared NOT NULL. As of MySQL 5.0.11, the display is UNI rather
     * than MUL regardless of whether the column permits NULL; you can see from
     * the Null field whether or not the column can contain NULL.
     * 
     * @param key
     *            the key to set
     */
    public abstract void setKey(String key);

    /**
     * The Extra field contains any additional information that is available
     * about a given column. The value is auto_increment for columns that have
     * the AUTO_INCREMENT attribute and empty otherwise.
     * 
     * @return the extra
     */
    public abstract String getExtra();

    /**
     * The Extra field contains any additional information that is available
     * about a given column. The value is auto_increment for columns that have
     * the AUTO_INCREMENT attribute and empty otherwise.
     * 
     * @param extra
     *            the extra to set
     */
    public abstract void setExtra(String extra);

    /**
     * Privileges indicates the privileges you have for the column. This value
     * is displayed only if you use the FULL keyword.
     * 
     * @return the privileges
     */
    public abstract String getPrivileges();

    /**
     * Privileges indicates the privileges you have for the column. This value
     * is displayed only if you use the FULL keyword.
     * 
     * @param privileges
     *            the privileges to set
     */
    public abstract void setPrivileges(String privileges);

    /**
     * indicates any comment the column has. This value is displayed only if you
     * use the FULL keyword.
     * 
     * @return the comment
     */
    public abstract String getComment();

    /**
     * indicates any comment the column has. This value is displayed only if you
     * use the FULL keyword.
     * 
     * @param comment
     *            the comment to set
     */
    public abstract void setComment(String comment);

    /**
     * The Default field indicates the default value that is assigned to the
     * column. This is NULL if the column has an explicit default of NULL. As of
     * MySQL 5.0.50, Default is also NULL if the column definition has no
     * DEFAULT clause.
     * 
     * @return the defaultInt
     */
    public abstract int getDefaultInt();

    /**
     * * The Default field indicates the default value that is assigned to the
     * column. This is NULL if the column has an explicit default of NULL. As of
     * MySQL 5.0.50, Default is also NULL if the column definition has no
     * DEFAULT clause.
     * 
     * @param defaultInt
     *            the defaultInt to set
     */
    public abstract void setDefaultInt(int defaultInt);

    /**
     * * The Default field indicates the default value that is assigned to the
     * column. This is NULL if the column has an explicit default of NULL. As of
     * MySQL 5.0.50, Default is also NULL if the column definition has no
     * DEFAULT clause.
     * 
     * @return the defaultDouble
     */
    public abstract double getDefaultDouble();

    /**
     * The Default field indicates the default value that is assigned to the
     * column. This is NULL if the column has an explicit default of NULL. As of
     * MySQL 5.0.50, Default is also NULL if the column definition has no
     * DEFAULT clause.
     * 
     * @param defaultDouble
     *            the defaultDouble to set
     */
    public abstract void setDefaultDouble(double defaultDouble);

    /**
     * @return the defaultString
     */
    public abstract String getDefaultString();

    /**
     * The Default field indicates the default value that is assigned to the
     * column. This is NULL if the column has an explicit default of NULL. As of
     * MySQL 5.0.50, Default is also NULL if the column definition has no
     * DEFAULT clause.
     * 
     * @param defaultString
     *            the defaultString to set
     */
    public abstract void setDefaultString(String defaultString);

    /**
     * The Default field indicates the default value that is assigned to the
     * column. This is NULL if the column has an explicit default of NULL. As of
     * MySQL 5.0.50, Default is also NULL if the column definition has no
     * DEFAULT clause.
     * 
     * @return the defaultDate
     */
    public abstract Date getDefaultDate();

    /**
     * The Default field indicates the default value that is assigned to the
     * column. This is NULL if the column has an explicit default of NULL. As of
     * MySQL 5.0.50, Default is also NULL if the column definition has no
     * DEFAULT clause.
     * 
     * @param defaultDate
     *            the defaultDate to set
     */
    public abstract void setDefaultDate(Date defaultDate);

    /**
     * The Default field indicates the default value that is assigned to the
     * column. This is NULL if the column has an explicit default of NULL. As of
     * MySQL 5.0.50, Default is also NULL if the column definition has no
     * DEFAULT clause.
     * 
     * @return the defaultTime
     */
    public abstract Time getDefaultTime();

    /**
     * The Default field indicates the default value that is assigned to the
     * column. This is NULL if the column has an explicit default of NULL. As of
     * MySQL 5.0.50, Default is also NULL if the column definition has no
     * DEFAULT clause.
     * 
     * @param defaultTime
     *            the defaultTime to set
     */
    public abstract void setDefaultTime(Time defaultTime);
    
    
    public abstract void setDefaultBoolean(boolean defaultBoolean);
    public abstract boolean isDefaultBoolean();
    
    public abstract boolean isLengthDefined();
    public abstract int getJavaDatatypeFromISQLTyp(int typ);

    
}
