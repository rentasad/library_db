package rentasad.library.db.helpers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Updated annotation to mark the primary key field
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface PrimaryKey
{
}
