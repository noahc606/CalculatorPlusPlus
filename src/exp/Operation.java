package exp;
import main.Main;
public class Operation extends ExpElement
{
    String opType = "null";

    public Operation(char c) {
        super.type = ExpElType.OPERATION;

        if(Main.opChars.contains(c+"") ) {
            opType = c+"";
        }
    }

    public String toString() {
        if( opType.length()==1 && Main.opChars.contains(opType) ) {
            return opType;
        }
        return "null";
    }
}
