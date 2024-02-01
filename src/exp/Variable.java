package exp;

public class Variable extends ExpElement
{
    char variableName = '`';

    public Variable(char vn) {
        super.type = ExpElType.VARIABLE;
        variableName = vn;
    }

    public String toString() {
        return variableName+"";
    }
}
