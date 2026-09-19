package parser;

public class ConditionalExpression {

    public ConditionalExpression(Expression condition, Expression thenArm, Expression elseArm) {
        this.condition = condition;
        this.thenArm = thenArm;
        this.elseArm = elseArm;
    }

    public final Expression condition;
    public final Expression thenArm;
    public final Expression elseArm;

}
