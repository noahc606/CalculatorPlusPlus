package ui;

import exp.Expression;
import exp.Number;

public class UserInput
{
    String lastInputStr = null;
    Expression lastVarExpression = null;
    boolean pluggingIn = false;

    public void takeIn(String s)
    {
        s = s.trim();
        s = s.toLowerCase();

        lastInputStr = s;

        if( pluggingIn ) {
            pluggingIn = !evaluateExpression();
            return;
        }

        // If user didn't input anything
        if( lastInputStr.length()==0 ) {

            // If user wants to graph a previous expression
        } else
        if( lastInputStr.equals("graph") ) {
            if(lastVarExpression==null) {
                System.out.println("Make sure you have typed in the expression you want to graph. Type \"help 3\" for more info.");
            } else {
                graph( lastVarExpression.toStringNormal() );
            }
        // If user wants to graph a new expresssion
        } else
        if( lastInputStr.length()>5 && lastInputStr.startsWith("graph:") ) {
            String toGraph = lastInputStr.substring(6);
            toGraph = toGraph.trim();
            graph(toGraph);
        // If user input is an expression
        } else {
            evaluateExpression();
        }
    }

    public boolean evaluateExpression() {
        // Create an Expression 'expr' from the String 'lastInputStr'
        Expression expr = new Expression(lastInputStr);

        if( pluggingIn ) {
            if(expr.hasVariables()) {
                System.out.println("You can only plug numerical values into variables. Please try again...");
                return false;
            }
            Expression varExpr = new Expression(lastVarExpression);
            varExpr.plugIn( 'x', new Number(expr.toStringNormal()) );
            System.out.print("Evaluating this expression with x="+expr.toStringNormal()+": ");
            System.out.println("\""+lastVarExpression.toStringNormal()+"\""+"...");
            varExpr.startEvaluateValue();
            System.out.println("expr="+varExpr.toStringNormal());
            return true;
        }

        // If 'expr' has variables in it, set 'lastVarExpression' to be a copy of 'expr'
        if( expr.hasVariables() ) {
            lastVarExpression = new Expression(expr);
            System.out.print("You have entered this expression: ");
            System.out.println("\""+expr.toStringNormal()+"\""+"...");
            System.out.println("Plug in a value for "+expr.getVariableList()+"...");
            pluggingIn = true;

        // If 'expr' doesn't have variables in it, evaluate the numerical value of 'expr'.
        } else {
            System.out.print("Evaluating this expression: ");
            System.out.println("\""+expr.toStringNormal()+"\""+"...");
            expr.startEvaluateValue();
            System.out.println("expr="+expr.toStringNormal());
        }

        return true;
    }

    public void graph(String toGraph) {
        System.out.println("Graphing \"y="+toGraph+"\"...");

        lastVarExpression = new Expression(toGraph);
        Graph graph = new Graph(toGraph);
        graph.draw();

        if( toGraph.length()==0 ) {
            System.out.println("Input error: Make sure you actually input something...");
        }
    }

    public void cmdIntro() {
        System.out.println("[ INTRO: ]");
        System.out.println("Calculator++ is a console-based multi-purpose calculator. It can evaluate expressions, solve expressions given a variable, and even graph equations.");
        System.out.println("There are a number of things Calculator++ is able to do. To learn about these, type \"help\".");
        System.out.println();
        System.out.println("[ IMPORTANT note regarding order of operations: ]");
        System.out.println("Calculator++ takes into account order of operations even though the typing goes from left to right.");
        System.out.println("Make use of parentheses to make sure things get evaluated the way you intend:");
        System.out.println("\tExample 1: \"1+2*5\" evaluates -> \"11\"   (may be unexpected, but is intended behavior)");
        System.out.println("\tExample 2: \"(1+2)*5\" evaluates -> \"15\" (expected and intended behavior)");
    }

    public void cmdHelp(String s) {
        String unknownParam = null;

        if(s.length()==4) {
            System.out.println("Calculator++ can do multiple things:");
            System.out.println("1. It can evaluate expressions");
            System.out.println("2. It can turn variable expressions into equations by plugging in a number for a variable.");
            System.out.println("3. It can graph functions of a single variable");
            System.out.println("Type \"help <#>\" to learn more about a topic.");
        } else if(s.length()==5) {
            System.out.println("No parameter given for command.");
        } else if(s.length()==6) {
            switch (s.charAt(5)) {
                case '1': {
                    System.out.println("[ Evaluating expressions: ]");
                    System.out.println("This is a simple process. Type in a parseable expression and press Enter/Return.");
                    System.out.println("\tExample 1: \"43*12\" -> [ENTER] -> \"expr=516\"");
                    System.out.println("\tExample 2: \"6+(12*12)\" -> [ENTER] -> \"expr=150\"");
                    System.out.println("\tExample 3: \"9/12345+1\" -> [ENTER] -> \"expr=150\"");
                } break;
                case '2': {
                    System.out.println("[ Evaluating variable expressions: ]");
                    System.out.println("This is similar to #1. Type in a variable expression and press Enter/Return.");
                    System.out.println("Then, type in the value(s) for the variable(s), pressing Enter each time.");
                    System.out.println("\tExample 1: \"12x+3 -> [ENTER] -> x=5 -> [ENTER] -> 12x+3=63\"");
                    System.out.println("\tExample 2: \"56wx^2+123x+456 -> [ENTER] -> x=432 -> [ENTER] -> w=1 -> [ENTER] -> 56wx^2+123x+456=10504536\"");

                } break;
                case '3': {
                    System.out.println("[ Graphing variable expressions of 1 variable: ]");
                    System.out.println("Type in a variable expression as in #2, except starting with the prompt \"graph: y=\".");
                    System.out.println("\tExample 1: \"graph: 12 -> [ENTER] -> Get graph of the horizontal line y=12");
                    System.out.println("\tExample 1: \"graph: 56x^2+123x+456 -> [ENTER] -> Get graph of a parabola");
                    System.out.println("The graph will be automatically scaled so that values between x=-16 to x=15 all fit within the visible area.");
                    System.out.println("Additionally, you can graph whatever your last expression was simply by typing in \"graph\".");
                } break;
                default: {
                    unknownParam = String.valueOf(s.charAt(5));
                } break;
            }
        } else {
            unknownParam = s.substring(5);
        }

        if(unknownParam!=null) {
            System.out.println("Error: Unknown parameter \""+unknownParam+"\". Type \"help\" if you don't know what you're doing.");
        }
    }
}
