package hhs.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * A better mousetrap, in this case a String "split" method.
 * 
 * @author wjohnson000
 */
public final  class StringHelper {

    /**
     * Make the constructor private to help ensure that this can't be instantiated
     */
    private StringHelper() { }

    /**
     * Convenience method to split a String into tokens based on a single-character
     * delimiter.  This method will honor multiple delimiters, as such:
     * <ul>
     *   <li><strong>a|b|c|d</strong> --> [a b c d]
     *   <li><strong>a||c|d</strong> --> [a (blank) c d]
     *   <li><strong>a|||</strong> --> [a (blank) (blank) (blank)]
     * </ul>
     * 
     * @param dataToSplit data to split
     * @param delimiter character delimiter
     * 
     * @return Array of tokens.
     */
    public static String[] split( String dataToSplit, char delimiter ) {
        List<String> results = new ArrayList<>();
        int one = 0;
        int ndx = dataToSplit.indexOf( delimiter );

        while ( ndx >= 0 ) {
            if ( ndx == one ) {
                results.add( "" );
            } else {
                results.add( dataToSplit.substring( one,ndx ) );
            }
            one = ndx+1;
            ndx = dataToSplit.indexOf( delimiter, one );
        }
        results.add( dataToSplit.substring( one ) );

        return results.toArray( new String[ results.size() ] );
    }
}
