package exp;


import java.math.BigDecimal;
public class Number extends ExpElement {
    private BigDecimal val = BigDecimal.valueOf(0);     //Value digits of the number
    private boolean hasNoValue = true;

    public Number(String s) {
        super.type = ExpElType.NUMBER;

        try {
            BigDecimal bd = new BigDecimal(s);
            set(bd);
        } catch (NumberFormatException nfe) {
            // Try to extract number from string
            Expression expr = new Expression(s);
            expr.evaluateValue(0);
            if( expr.expElements.size()==1 && expr.expElements.get(0).isType(ExpElType.NUMBER) ) {
                try {
                    set( ((Number)expr.expElements.get(0)) );
                } catch (NumberFormatException nfe2) {
                    System.out.println();
                }

            // If unsuccessful
            } else {
                System.out.println("Parse error: The string \""+s+"\" could not be parsed into a number!");
            }
        }
    }
    public Number(int num) {
        super.type = ExpElType.NUMBER;
        set(BigDecimal.valueOf(num));
    }
    public Number(Number num) {
        super.type = ExpElType.NUMBER;
        set(num);
    }

    public boolean hasNoValue() {
        return hasNoValue;
    }
    public BigDecimal getVal() { return val; }
    public BigDecimal v() { return getVal(); }
    public String toString() { if(hasNoValue ) return "[null#]"; return val.toPlainString(); }

    /**
     * If number 'n2' is greater than this Number
     */
    public boolean gt(Number n2) { return v().compareTo(n2.v()) > 0; }
    /**
     * If number 'n2' is greater than or equal to this Number
     */
    public boolean ge(Number n2) { return !gt(n2); }
    /**
     * If number 'n2' is less than this Number
     */
    public boolean lt(Number n2) { return v().compareTo(n2.v()) < 0; }
    /**
     * If number 'n2' is less than or equal to this Number
     */
    public boolean le(Number n2) { return !gt(n2); }

    public void set(BigDecimal d) {
        hasNoValue = false;
        val = d;
    }
    public void set(Number n) {
        if( n.hasNoValue() ) {
            hasNoValue = true;
            val = BigDecimal.valueOf(0);
        } else {
            hasNoValue = false;
            val = n.getVal();
        }
    }
}