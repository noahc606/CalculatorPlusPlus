package helper;

import exp.Function;
import main.Main;

public class StringIterator
{

    public boolean isNumChar(char c) {
        return Main.numberChars.contains(String.valueOf(c));
    }
    public boolean isDigitChar(char c) {
        return Main.digitChars.contains(String.valueOf(c));
    }
    public boolean isAlphaChar(char c) {
        return Main.alphaChars.contains(String.valueOf(c));
    }
    public boolean isOperationChar(char c) {
        return Main.opChars.contains(String.valueOf(c));
    }

    public char getPrevChar(String s, int i) {
        if( i>=1 && i<=s.length() ) {
            return s.charAt(i-1);
        }
        return '`';
    }
    public char getCurrChar(String s, int i) {
        if( i>=0 && i<=s.length()-1 ) {
            return s.charAt(i);
        }
        return '`';
    }
    public char getNextChar(String s, int i) {
        if( i>=-1 && i<=s.length()-2 ) {
            return s.charAt(i+1);
        }
        return '`';
    }

    public String removeWhitespace(String s) {
        // Rebuild string, removing all characters that are contained within 'main.Main.wsChars'
        String res = "";
        for (int i = 0; i < s.length(); i++) {
            char curr = s.charAt(i);
            if (!Main.wsChars.contains( String.valueOf(curr) )) {
                res += curr;
            }
        }

        // Return result
        return res;
    }
    public String removeEdgeParentheses(String s) {
        // Build the 'exprTrimmed' string which we may or may not return in the end
        String exprTrimmed = s;
        // If exprTrimmed is at least 3 chars long
        if(exprTrimmed.length()>=3) {
            // If exprTrimmed ends and starts with parentheses
            if( exprTrimmed.charAt(0)=='(' && exprTrimmed.charAt(exprTrimmed.length()-1)==')' ) {
                // Set exprTrimmed = to itself with the end chars excluded
                exprTrimmed = exprTrimmed.substring(1, exprTrimmed.length()-1);
            }
        } else {
            return s;
        }

        if( s.charAt(0)!='(' ) {
            return s;
        }

        int numParenthesesLeft = 1;
        for(int i = 1; i<s.length()-1; i++) {
            if(s.charAt(i)=='(') {
                numParenthesesLeft++;
            } else if( s.charAt(i)==')' ) {
                if(numParenthesesLeft==0) {
                    printParenthesesError("')' has no available '(' behind it.");
                }
                numParenthesesLeft--;
                if(numParenthesesLeft==0) {
                    return s;
                }
            }
        }

        if( s.charAt(s.length()-1)==')' ) {
            if(numParenthesesLeft==1) {
                return exprTrimmed;
            }
        }

        return s;
    }

    public void checkParentheses(String expr) {
        int leftParentheses = 0;
        int rightParentheses = 0;
        for(int i = 0; i<expr.length(); i++) {
            if(expr.charAt(i)=='(') { leftParentheses++; continue; }
            if(expr.charAt(i)==')') { rightParentheses++; }
        }
        if( leftParentheses!=rightParentheses ) {
            printParenthesesError("In expression, # of '('s does not equal # of ')'s.");
        }
    }
    public void printParenthesesError(String s) {
        System.out.println("Parentheses error: "+s);
    }

    public String findFunctionString(int index, String s) {

        // A function is a set of subsequent alphabetical chars followed by a square bracket expression
        // For example: in the expression "12+4sin[12]*123", "sin[12]" is the function.
        int numBracketsLeft = 0;
        StringBuilder pfs = new StringBuilder(); // 'pfs' = potential function string
        for( int i = index; i<s.length(); i++ ) {
            // Find next and current chars
            char next = getNextChar(s, i);
            char curr = getCurrChar(s, i);

            // When we see an open square bracket
            if(curr=='[') {
                numBracketsLeft++;
            // When we see a closed square bracket
            } else if ( curr==']' ) {
                numBracketsLeft--;
                // If we reached the closing square bracket of the original open square bracket
                if(numBracketsLeft==0) {
                    // Return pfs which was just built
                    return pfs.toString();
                }
            }

            // When we reach the end of a function
            if(next=='[') {
                // Check if it is a valid function name
                if( !Function.isValidFunction(pfs.toString()) ) {
                    System.out.println("Error: unknown function name \""+pfs+"\"!");
                    return "(UNKNOWN FUNCTION)";
                }
            }

            // When we see a character that is not an alphabetical nor square brackets
            if( !isAlphaChar(curr) && !("[]".contains(curr+"")) ) {
                return pfs.toString();
            }

            // Add to string builder
            pfs.append(curr);
        }

        // If no function was found, we must be looking at a variable or there is an error of some sort
        return pfs.toString();
    }
    /**
     * Returns the string "..." within "blahblahblah[...]".
     * @param s Any String in the form of "blahblahblah[...]"
     * @return See description
     */
    public String findFunctionExpr(String s) {
        StringBuilder res = new StringBuilder();
        boolean foundLeftBracket = false;
        for(int i = 0; i<s.length()-1; i++) {   //i<length-1 since we don't care about the last ']'
            if( s.charAt(i)=='[' ) {
                foundLeftBracket = true;
                continue;
            }

            if( foundLeftBracket ) {
                res.append( s.charAt(i) );
            }
        }

        return res.toString();
    }
}
