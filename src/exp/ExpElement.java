package exp;

public class ExpElement
{
    protected ExpElType type = ExpElType.UNKNOWN;

    public String toString() {
        switch (type) {
            case SUB_EXPRESSION, OPERATION, FUNCTION, NUMBER, VARIABLE: {
                return this.toString();
            }
        }

        // If opType is unknown, return "?"
        return "?";
    }

    public ExpElType getType() { return type; }
    public boolean isType(ExpElType et) { return type==et; }
    public boolean is(ExpElType et) { return isType(et); }
    public boolean eq(String s) { return toString().equals(s); }
    public boolean eq(char c) { return toString().equals( String.valueOf(c) ); }
}