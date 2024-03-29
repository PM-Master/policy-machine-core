package gov.nist.csd.pm.policy.pml.compiler.visitor;

import gov.nist.csd.pm.policy.pml.antlr.PMLBaseVisitor;
import gov.nist.csd.pm.policy.pml.antlr.PMLParser;
import gov.nist.csd.pm.policy.pml.model.context.VisitorContext;
import gov.nist.csd.pm.policy.pml.model.expression.Literal;
import gov.nist.csd.pm.policy.pml.model.expression.MapLiteral;
import gov.nist.csd.pm.policy.pml.model.expression.Type;
import gov.nist.csd.pm.policy.pml.model.expression.ArrayLiteral;
import gov.nist.csd.pm.policy.pml.model.scope.PMLScopeException;
import gov.nist.csd.pm.policy.pml.statement.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiteralExprVisitor extends PMLBaseVisitor<Literal> {

    private final VisitorContext visitorCtx;

    public LiteralExprVisitor(VisitorContext visitorCtx) {
        this.visitorCtx = visitorCtx;
    }

    public Literal visitLiteral(PMLParser.LiteralContext literalCtx) {
        if (literalCtx instanceof PMLParser.StringLiteralContext stringLiteralCtx) {
            return parseStringLiteral(stringLiteralCtx);
        } else if (literalCtx instanceof PMLParser.NumberLiteralContext numberLiteralCtx) {
            return parseNumberLiteral(numberLiteralCtx);
        } else if (literalCtx instanceof PMLParser.BooleanLiteralContext booleanLiteralCtx) {
            return parseBooleanLiteral(booleanLiteralCtx);
        } else if (literalCtx instanceof PMLParser.ArrayLiteralContext arrayLiteralCtx) {
            return parseArrayLiteral(arrayLiteralCtx);
        } else  {
            return parseMapLiteral((PMLParser.MapLiteralContext) literalCtx);
        }
    }

    @Override
    public Literal visitNumberLiteral(PMLParser.NumberLiteralContext ctx) {
        return visitLiteral(ctx);
    }

    @Override
    public Literal visitStringLiteral(PMLParser.StringLiteralContext ctx) {
        return visitLiteral(ctx);
    }

    @Override
    public Literal visitBooleanLiteral(PMLParser.BooleanLiteralContext ctx) {
        return visitLiteral(ctx);
    }

    @Override
    public Literal visitArrayLiteral(PMLParser.ArrayLiteralContext ctx) {
        return visitLiteral(ctx);
    }

    @Override
    public Literal visitMapLiteral(PMLParser.MapLiteralContext ctx) {
        return visitLiteral(ctx);
    }

    private Literal parseNumberLiteral(PMLParser.NumberLiteralContext ctx) {
        return new Literal(Integer.parseInt(ctx.NUMBER().getText()));
    }

    private Literal parseStringLiteral(PMLParser.StringLiteralContext ctx) {
        return new Literal(removeQuotes(ctx.STRING().getText()));
    }

    private Literal parseBooleanLiteral(PMLParser.BooleanLiteralContext ctx) {
        return new Literal(ctx.BOOLEAN().getText().equalsIgnoreCase("true"));
    }

    private Literal parseArrayLiteral(PMLParser.ArrayLiteralContext ctx) {
        PMLParser.ArrayContext arrayCtx = ctx.array();
        Type elementType = null;
        if (arrayCtx.expression().isEmpty()) {
            elementType = Type.any();
        }

        // determine the type of the array literal elements
        // if all the elements are of the same type then that is the element type
        // if the elements are of different types then the type is ANY
        List<Expression> exprs = new ArrayList<>();
        for (PMLParser.ExpressionContext expressionCtx : arrayCtx.expression()) {
            Expression expr = Expression.compile(visitorCtx, expressionCtx);
            Type type = Type.any();
            try {
                type = expr.getType(visitorCtx.scope());
            } catch (PMLScopeException e) {
                visitorCtx.errorLog().addError(expressionCtx, e.getMessage());
            }

            if (elementType == null) {
                elementType = type;
            } else if (!type.equals(elementType)) {
                elementType = Type.any();
            }

            exprs.add(expr);
        }

        return new Literal(new ArrayLiteral(exprs.toArray(new Expression[]{}), elementType));
    }

    private Literal parseMapLiteral(PMLParser.MapLiteralContext ctx) {
        Map<Expression, Expression> map = new HashMap<>();

        Type keyType = null;
        Type valueType = null;

        for(PMLParser.MapEntryContext mapEntryCtx : ctx.map().mapEntry()) {
            Expression keyExpr = Expression.compile(visitorCtx, mapEntryCtx.key);
            Expression valueExpr = Expression.compile(visitorCtx, mapEntryCtx.value);

            Type keyExprType = Type.any();
            Type valueExprType = Type.any();
            try {
                keyExprType = keyExpr.getType(visitorCtx.scope());
                valueExprType = valueExpr.getType(visitorCtx.scope());
            } catch (PMLScopeException e) {
                visitorCtx.errorLog().addError(mapEntryCtx, e.getMessage());
            }

            // check that all map keys are the same type
            if (keyType == null) {
                keyType = keyExprType;
            } else if (!keyExprType.equals(keyType)) {
                visitorCtx.errorLog().addError(
                        ctx,
                        "expected map keys to be of the same type but found: "
                                + keyExprType + " and " + keyType
                );
            }

            // if map values are different types then the value type for this map is ANY
            if (valueType == null) {
                valueType = valueExprType;
            } else if (!valueExprType.equals(valueType)) {
                valueType = Type.any();
            }

            map.put(keyExpr, valueExpr);
        }

        if (keyType == null) {
            keyType = Type.any();
        }

        if (valueType == null) {
            valueType = Type.any();
        }

        return new Literal(new MapLiteral(map, keyType, valueType));
    }

    private String removeQuotes(String s) {
        if ((s.startsWith("'") && s.endsWith("'")) || (s.startsWith("\"") && s.endsWith("\""))) {
            return s.substring(1, s.length() - 1);
        } else {
            return s;
        }
    }
}
