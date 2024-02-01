package exp;
import main.Main;
public class Function extends ExpElement
{
    String funcType = "null";
    Expression subExpr = null;

    public Function(String s) {
        super.type = ExpElType.FUNCTION;

        if( isValidFunction(s) ) {
            funcType = s;
        }
    }

    public String toString() {
        if(isValidFunction(funcType))
            return funcType;
        return "(UNKNOWN FUNCTION)";
    }

    public static boolean isValidFunction(String funcName) {
        for(int i = 0; i<Main.funcStrs.length; i++) {
            if(funcName.equals(Main.funcStrs[i])) {
                return true;
            }
        }

        return false;
    }
}
