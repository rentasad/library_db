package rentasad.library.db.helpers;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This Annotation marks fields in a class which should be transferred
 * to a database. The optional value can be used to change the column name
 * for persistin the field value.
 * <br>
 * On classes it can be used to define the table name of the class
 *
 * @author Pascal Bihler
 */
@Retention(RUNTIME)
@Target({ TYPE, FIELD })
@Documented
public @interface DBPersisted
{
	String value() default "";

	int sqlType() default -1;
}
