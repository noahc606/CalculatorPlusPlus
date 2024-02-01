package exp;

import helper.StringIterator;
import helper.Evaluator;
import main.Main;
import java.util.ArrayList;
import java.util.List;

import static exp.ExpElType.*;

public class Expression extends ExpElement
{
    List<ExpElement> expElements = new ArrayList<>();
    static StringIterator sitr = new StringIterator();

    public Expression(String s) {
        super.type = ExpElType.SUB_EXPRESSION;

        // Remove whitespace from string
        s = sitr.removeWhitespace(s);
        if(s.equals("()")) {
            sitr.printParenthesesError("Empty set of parentheses \"().\"");
            return;
        }

        // Check parentheses
        sitr.checkParentheses(s);
        s = sitr.removeEdgeParentheses(s);

        // Populate 'expElements'
        buildExpression(s);
    }
    public Expression(Expression expr) { copy(expr); }
    public Expression() {}

    /**
     * Clears this expression's list, and copies the list from 'e2' to this expression's list.
     * @param e2 The expression to be copied from.
     */
    public void copy(Expression e2) {
        expElements.clear();
        expElements.addAll(e2.expElements);
    }

    /**
     * Iterates through all expression elements in order to find the single numerical value of the full expression.
     * Transforms the original expression (the object you're calling this method from).
     * @param depth integer that represents the depth of the function (how many times it has called itself)
     * @param normalUser: If true, this function may print out extra error messages to the user if too many steps are done.
     */
    public void evaluateValue(int depth, boolean normalUser) {
        try {
            evaluateValueNoTryCatch(depth, normalUser);
        } catch( ArithmeticException ae ) {
            System.out.println(ae);
        }
    }
    public void evaluateOneStep() {
        evaluateValue(Main.maxEvaluationSteps+1, false);
    }
    public void evaluateValue(int depth) {
        evaluateValue(depth, true);
    }
    public void startEvaluateValue() { evaluateValue(0 ); }

    /**
     * Concatenates all expression elements' toString() values and returns as one string.
     * @return String representation of this expression. 0 if there are no expression elements.
     */
    public String toStringNormal() {
        // Create empty String that we will build and return later
        StringBuilder res = new StringBuilder();

        // If list size is 0
        if(expElements.size()==0) {
            return "0";
        // If list size is 1
        } else if(expElements.size()==1) {
            res = new StringBuilder(expElements.get(0).toString());
            // If list size is >= 2
        } else {
            for(int i = 0; i< expElements.size(); i++) {
                res.append(expElements.get(i).toString());
            }
        }

        // Return 'res'
        return res.toString();
    }
    /**
     * Returns a list of each element in exprElements. Helpful for debugging.
     * WARNING: DOES NOT return an expression string
     * @return List elements formatted as a single string to help with debugging. Example: 1+1 -> "{NUMBER:"1", OPERATION:"+", NUMBER:"1"}"
     */
    public String toStringDebug() {
        StringBuilder res = new StringBuilder("{");

        for(int i = 0; i< expElements.size(); i++) {
            res.append(expElements.get(i).getType()).append(":\"");
            res.append(expElements.get(i).toString());
            if( i< expElements.size()-1 ) {
                res.append("\", ");
            }
        }

        return res+"\"}";
    }
    /**
     * Gives string representation of this expression to be used in another calculation
     * NOTE: This function is only used in recursive parentheses evaluation. I recommend using toStringNormal() instead.
     * @return toStringNormal() w/ parentheses around.
     */
    public String toString() {
        return "("+toStringNormal()+")";
    }

    public boolean hasVariables() {
        for (int i = 0; i<expElements.size(); i++) {
            // If a variable was found in the expression, return true
            if( expElements.get(i).is(VARIABLE) ) {
                return true;
            // If a subexpression was found in the expression, return subExpression.hasVariables().
            } else
            if( expElements.get(i).is(SUB_EXPRESSION) ) {
                return ((Expression)expElements.get(i)).hasVariables();
            }
        }
        return false;
    }

    public String getVariableList() {
        // Add each variable to 'vList' when they are encountered.
        String vList = "";
        for (int i = 0; i<expElements.size(); i++) {
            // If a variable was found in the expression, return true
            if( expElements.get(i).is(VARIABLE) ) {
                vList += ( expElements.get(i).toString() );
            // If a subexpression was found in the expression, return subExpression.hasVariables().
            } else
            if( expElements.get(i).is(SUB_EXPRESSION) ) {
                vList += ( ((Expression)expElements.get(i)).getVariableList() );
            }
        }

        // Rebuild a new version of 'vList' with no variables repeated
        String res = "";
        for (int i = 0; i<vList.length(); i++) {
            if( !res.contains( vList.charAt(i)+"" ) ) {
                // Just character
                if( vList.length()==1 ) {
                    res += ( vList.charAt(i) );
                } else
                // No comma (end of list)
                if( i==vList.length()-1 && i>0 ) {
                    res += ( "and " + vList.charAt(i) );
                // Comma
                } else {
                    res += ( vList.charAt(i) + ", " );
                }
            }
        }

        return res;
    }

    /**
     * Replaces all instances of a variable with a number in an expression. Useful in graphing equations.
     * @param variable - Name of varaible
     * @param num - Value that variable should take on
     */
    public void plugIn(char variable, Number num) {
        for(int i = 0; i< expElements.size(); i++) {
            // If we see a variable
            if( expElements.get(i).isType(VARIABLE) ) {
                // If
                if( expElements.get(i).eq(variable) ) {
                    expElements.remove(i);
                    expElements.add(i, num);
                }
            // If we see a sub-expression
            } else if( expElements.get(i).isType(SUB_EXPRESSION) ) {
                // Plug variable into sub-expression
                Expression subExpr = (Expression) expElements.get(i);
                subExpr.plugIn(variable, num);
            }
        }
    }

    private void buildExpression(String expr) {
        int numParenthesesLeft = 0;
        boolean buildingSubExpr = false;
        StringBuilder subExpr = new StringBuilder();

        StringBuilder numStr = new StringBuilder();     // This String will later be converted to a long double
        for(int i = 0; i<expr.length(); i++) {
            char prev = sitr.getPrevChar(expr, i);
            char curr = sitr.getCurrChar(expr, i);
            char next = sitr.getNextChar(expr, i);

            // If 'curr' is a '('
            if(curr=='(') {
                numParenthesesLeft++;
                buildingSubExpr = true;
            } else if(curr==')') {
                if(numParenthesesLeft==0) {
                    sitr.printParenthesesError("')' has no available '(' behind it.");
                }
                numParenthesesLeft--;
                if(numParenthesesLeft==0) {
                    buildingSubExpr = false;
                    subExpr.append(curr);
                    expElements.add(new Expression(subExpr.toString()));
                    subExpr = new StringBuilder();
                    continue;
                }
            }

            if(buildingSubExpr) {
                subExpr.append(curr);
            } else
            {
                // If 'curr' is a number character (a digit || in "-.eE")
                if(sitr.isNumChar(curr)) {
                    // If 'curr' is a '-'
                    if( curr=='-' ) {
                        // If 'curr' is a minus sign (NOT a negative sign)
                        if( sitr.isDigitChar(prev) || prev==')' ) {
                            // The minus sign should be added to the operation
                        // If 'curr' is a negative sign (NOT a minus sign)
                        } else {
                            numStr.append(curr); //Add 'curr' to number string
                        }
                    } else {
                        numStr.append(curr); //Add 'curr' to number string
                    }

                    // Test if we are currently at the last digit of a number
                    if( sitr.isDigitChar(curr) ) {
                        if(
                            ( i==expr.length()-1 ) ||                           // If we are at the end of the string       OR
                            ( "()[]".contains(next+"") ) ||                     // If we are behind any parentheses symbol  OR
                            ( next!='E'&&next!='e'&&sitr.isAlphaChar(next) ) || // If we are behind a non-E alphabet character  OR
                            ( sitr.isOperationChar(next) )                      // If we are behind an operation
                        ) {
                            // Create new expression element from the 'numStr' that was just built and add it to exprElements
                            ExpElement exElNum = new Number(numStr.toString());
                            expElements.add(exElNum);
                            // Reset numStr
                            numStr = new StringBuilder();
                            continue;
                        }
                    }
                }

                // If 'curr' is an operation (and not a negative symbol), add operation
                if(
                    ( sitr.isOperationChar(curr) ) &&                           // If curr is an operation AND
                    !( curr=='-'&&(prev=='`'||sitr.isOperationChar(prev)) )     // NOT If curr a negative sumbol
                ) {
                    ExpElement res = new Operation(curr);
                    expElements.add(res);
                    continue;
                }

                // If 'curr' is a letter, we need to find out if it is a variable or a function
                if( sitr.isAlphaChar(curr) ) {
                    String fn = sitr.findFunctionString(i, expr);
                    // If we found a variable or string of variables
                    if( !fn.contains("[") && !fn.contains("]") ) {
                        ExpElement res = new Variable(fn.charAt(0));
                        expElements.add(res);
                    // If we found a function, add the function itself and the subexpression within it
                    } else {
                        expElements.add( new Function(fn) );
                        expElements.add( new Expression(sitr.findFunctionExpr(fn)) );


                    }
                    continue;
                }

            }
        }
    }

    /**
     * Implementation for evaluateValue that isn't surrounded by try/catch. Use the public evaluateValue() methods instead of this one!
     */
    private void evaluateValueNoTryCatch(int depth, boolean normalUser) {
        // Before evaluation, store expression as a string
        String prevExpr = toStringNormal();
        for(int i = 0; i< expElements.size(); i++) {
            // STEP 1: get prev, curr, and next elements, if they exist
            ExpElement prev = null;
            if( i>0 ) prev = (ExpElement) expElements.get(i-1);
            ExpElement curr = (ExpElement) expElements.get(i);
            ExpElement next = null;
            if( i< expElements.size()-1 ) next = (ExpElement) expElements.get(i+1);
            ExpElement nextnext = null;
            if( i< expElements.size()-2 ) nextnext = (ExpElement) expElements.get(i+2);

            // If we have a lone expression, find the value of it and remove the parentheses
            if( (prev==null || prev.is(OPERATION)) && curr.is(SUB_EXPRESSION) && (next==null || next.is(OPERATION)) ) {
                if( curr.is(SUB_EXPRESSION) ) {
                    // Evaluate the expression inside parentheses
                    Expression currE = (Expression)curr;
                    currE.evaluateValue(depth, normalUser);

                    // Get the expression as a string with the parentheses removed
                    Number res = new Number(currE.toStringNormal());

                    //Remove old curr and add new curr
                    expElements.remove(i);
                    expElements.add(i, res);
                    break;
                }
            }

            // If previous and next expression objects exist
            if( prev!=null && next!=null ) {
                // Case: If we have two negatives and then a expression/number: We must have a double negative
                if( prev.is(OPERATION) && prev.eq("-") &&
                    curr.is(OPERATION) && curr.eq("-")
                ) {
                    Evaluator.doMinusMinusX(i, curr, expElements);
                    break;
                }

                // Case: If conditions are suitable to perform an operation between two numbers, do it
                if( prev.is(NUMBER) &&
                    curr.is(OPERATION) &&
                    next.is(NUMBER)
                ) {
                    // We must make sure to do any further non-addition operations first (for ex: 5^1*2 = 5+(1*2) )
                    if( nextnext==null ) {
                        Evaluator.doNumberOperationNumber(i, prev, curr, next, expElements);
                    } else {
                        if( curr.eq("+") || curr.eq("-") ) {
                            if( // exponents, multiplication, division should be evaluated first
                                nextnext.is(OPERATION) && nextnext.eq("^") ||
                                nextnext.is(SUB_EXPRESSION) || nextnext.is(NUMBER) || nextnext.is(VARIABLE) ||
                                nextnext.is(OPERATION) && nextnext.eq("*") ||
                                nextnext.is(OPERATION) && nextnext.eq("/")
                            ) { continue; }
                        } else
                        if( curr.eq("*") || curr.eq("/") || curr.eq("^") ) {
                            if( // Terms with exponents and power towers should be evaluated right to left
                                nextnext.is(OPERATION) && nextnext.eq("^")
                            ) { continue; }
                        }

                        // Evaluate operation normally
                        Evaluator.doNumberOperationNumber(i, prev, curr, next, expElements);
                        break;
                    }
                    continue;
                }
            }

            // If conditions are suitable to perform multiplication between two expressions or two numbers, add a multiplication symbol
            if( prev!=null ) {
                if( (prev.is(SUB_EXPRESSION) || prev.is(NUMBER)) &&
                    (curr.is(SUB_EXPRESSION) || curr.is(NUMBER))
                ) {
                    expElements.add(i, new Operation('*'));
                    break;
                }
            }

        }

        // Store new evaluated expression as a string
        String newExpr = toStringNormal();

        //Check if we hit 'ain.maxEvaluationSteps' steps. If so, we need to stop the function
        if( depth<Main.maxEvaluationSteps ) {
            // If 'prevExpr' is different from 'newExpr', we need to keep going.
            if( !prevExpr.equals(newExpr) ) {
                depth++;
                evaluateValue(depth, normalUser);
            }
        } else {
            if(normalUser) {
                System.out.println("Evaluation error: Expression has more than 10000 steps.");
            }
        }
    }
}