package org.gustini.library.db;
/**
 *
 * Gustini GmbH (2016)
 * Creation: 07.06.2016
 * Library
 * gustini.library.db
 *
 * @author Matthias Staud
 *
 *
 * Description: Encodings fuer Connection-String
 *
 */
public enum MySqlEncodingEnum
{
    cp1250,
    utf8,
    latin1;


    public static boolean containsKey(String key)
    {
       switch (key)
    {
        case "cp1250":
            return true;
        case "utf8":
            return true;
        case "latin1":
            return true;
        default:
            return false;
    }


    }
}
