package main;
import exp.Expression;
import exp.Number;
import ui.Graph;
import ui.UserInput;

import java.util.Scanner;
public class Main
{
    public static final String digitChars = "012345679";                    // List of chars that are digits
    public static final String alphaChars = "abcdefghijklmnopqrstuvwxyz";   // List of chars that can be variables
    public static final String numberChars = digitChars+".Ee-";             // List of chars that make up a number
    public static final String wsChars = " _\t";                            // List of chars that are considered whitespace
    public static final String opChars = "+-*/^";                           // List of chars that are operators
    public static final String[] funcStrs
        = new String[]{"sin", "cos", "tan", "asin", "acos", "atan"};        // Array of strings that are functions
    public static final int maxEvaluationSteps = 99999;                     // Maximum number of steps that can be taken in evaluating an expression
    public static final int maxExponent = 999999999;                        // Maximum power you can raise a number to

    public static void main(String[] args) {
        Scanner snr = new Scanner(System.in);
        UserInput ui = new UserInput();

        if(!true) {
            test();
            return;
        }

        System.out.println("Welcome to Calculator++! Type in \"intro\" if you've never used this program before.");

        while(true) {

            // Take in user Input
            System.out.print("> ");
            String in = snr.nextLine().trim();

            if( in.length()>=5 && in.substring(0,5).equals("help ") ) {
                ui.cmdHelp(in);
                continue;
            }

            // Process input
            switch (in) {
                case "intro": {
                    ui.cmdIntro();
                } break;
                case "help": {
                    ui.cmdHelp(in);
                } break;
                default: {
                    ui.takeIn(in);
                } break;
            }
        }
    }

    private static void test() {
        System.out.println("Testing...");
        {
            //Expression expr = new Expression("(5x^2)+1234");
            Expression expr = new Expression("x^3-16x^2-4x+3");
            Graph graph = new Graph(expr);
            graph.draw();
        }

        if(!true) {
            Expression expr = new Expression("10^(999999999)");
            expr.evaluateOneStep();
            System.out.println(expr.toStringDebug());
            //exel.Number n1 = new exel.Number("1238812");
            //System.out.println(n1.toString());

        }


    }
}