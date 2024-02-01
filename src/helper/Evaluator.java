package helper;

import exp.ExpElement;
import exp.Number;
import exp.Operation;
import main.Main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static exp.ExpElType.NUMBER;

public class Evaluator
{
    /**
     * Helper methods that are used in Expression.evaluateValue().
     * @param i The current value for i in the for loop
     * @param expElements The list of expression elements
     * @return The new value for i in the for loop
     */

    public static int doMinusMinusX(int i, ExpElement next, List<ExpElement> expElements) {
        // Number in 'curr' already gets negated; all we need to do is remove one unnecessary subtract operation
        if( next.is(NUMBER) ) {
            expElements.remove(i);
            i++;
            return i;
            // Turn two minus symbols into one addition symbol
        } else {
            expElements.remove(i-1);
            expElements.remove(i-1);
            expElements.add(i-1, new Operation('+'));
            i--;
            return i;
        }
    }

    public static int doNumberOperationNumber(int i, ExpElement prev, ExpElement curr, ExpElement next, List<ExpElement> expElements) {
        Number prevN = (Number)prev;
        Operation currO = (Operation)curr;
        Number nextN = (Number)next;
        Number res = new Number("0");

        switch ( currO.toString() ) {
            case "+": { res.set( prevN.getVal().add(nextN.getVal()) );                                      } break;
            case "-": { res.set( prevN.getVal().subtract(nextN.getVal()) );                                 } break;
            case "*": { res.set( prevN.getVal().multiply(nextN.getVal()) );                                 } break;
            case "/": {
                    res.set( prevN.getVal().divide(nextN.getVal(), 100, RoundingMode.HALF_UP) );


            } break;
            case "^": {

                if( nextN.getVal().remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0 ||
                    nextN.getVal().compareTo(BigDecimal.valueOf(Main.maxExponent))>0 ||
                    nextN.getVal().compareTo(BigDecimal.ZERO)<0
                ) {
                    System.out.println("Exponent error: Exponents must be an integer from 0 to " + Main.maxExponent + ".");
                } else {
                    res.set( prevN.getVal().pow( nextN.getVal().intValue() ) );
                }
            } break;
        }

        expElements.remove(i-1);
        expElements.remove(i-1);
        expElements.remove(i-1);
        expElements.add(i-1, res);
        i--;
        return i;
    }
}
