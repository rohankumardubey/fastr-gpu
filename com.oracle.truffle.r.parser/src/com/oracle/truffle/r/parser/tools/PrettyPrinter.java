/*
 * This material is distributed under the GNU General Public License
 * Version 2. You may review the terms of this license at
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * Copyright (c) 2012-2014, Purdue University
 * Copyright (c) 2013, 2015, Oracle and/or its affiliates
 *
 * All rights reserved.
 */
package com.oracle.truffle.r.parser.tools;

import java.io.PrintStream;
import java.util.List;

import com.oracle.truffle.r.parser.ast.ASTNode;
import com.oracle.truffle.r.parser.ast.ArgNode;
import com.oracle.truffle.r.parser.ast.BinaryOperation;
import com.oracle.truffle.r.parser.ast.Break;
import com.oracle.truffle.r.parser.ast.Constant;
import com.oracle.truffle.r.parser.ast.FieldAccess;
import com.oracle.truffle.r.parser.ast.For;
import com.oracle.truffle.r.parser.ast.Function;
import com.oracle.truffle.r.parser.ast.FunctionCall;
import com.oracle.truffle.r.parser.ast.If;
import com.oracle.truffle.r.parser.ast.Next;
import com.oracle.truffle.r.parser.ast.Repeat;
import com.oracle.truffle.r.parser.ast.Sequence;
import com.oracle.truffle.r.parser.ast.SimpleAccessVariable;
import com.oracle.truffle.r.parser.ast.SimpleAssignVariable;
import com.oracle.truffle.r.parser.ast.UnaryOperation;
import com.oracle.truffle.r.parser.ast.UpdateField;
import com.oracle.truffle.r.parser.ast.UpdateVector;
import com.oracle.truffle.r.parser.ast.While;

public class PrettyPrinter extends BasicVisitor<Void> {

    private static final boolean PARENTHESIS = false;

    private int level = 0;
    private final PrintStream out;
    private final StringBuilder buff = new StringBuilder();

    public PrettyPrinter(PrintStream stream) {
        out = stream;
    }

    public void print(ASTNode n) {
        n.accept(this);
        flush();
    }

    public void println(ASTNode n) {
        n.accept(this);
        println("");
    }

    private void inc() {
        level++;
    }

    private void dec() {
        if (level == 0) {
            throw new RuntimeException("Unbalanced stack for indentation");
        }
        level--;
    }

    private void indent() {
        for (int i = 0; i < level; i++) {
            out.append('\t');
        }
    }

    private void print(String arg) {
        buff.append(arg);
    }

    private void println(String arg) {
        print(arg);
        buff.append('\n');
        flush();
    }

    private void flush() {
        out.print(buff);
        out.flush();
        buff.setLength(0);
    }

    @Override
    public Void visit(ASTNode n) {
        print("##(TODO: " + n + ")##");
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public Void visit(Next n) {
        print("next");
        return null;
    }

    @Override
    public Void visit(Break n) {
        print("break");
        return null;
    }

    @Override
    public Void visit(Sequence n) {
        ASTNode[] exprs = n.getExpressions();
        switch (exprs.length) {
            case 0:
                print("{}");
                break;
            case 1:
                print("{ ");
                exprs[0].accept(this);
                print(" }");

                break;
            default:
                println("{");
                inc();
                for (ASTNode e : exprs) {
                    indent();
                    e.accept(this);
                    println("");
                }
                dec();
                indent();
                print("}");
        }
        return null;
    }

    @Override
    public Void visit(If n) {
        print("if(");
        n.getCondition().accept(this);
        print(") ");
        n.getTrueCase().accept(this);
        ASTNode f = n.getFalseCase();
        if (f != null) {
            print(" else ");
            f.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(BinaryOperation op) {
        ASTNode left = op.getLHS();
        ASTNode right = op.getRHS();
        if (PARENTHESIS) {
            print("(");
        }
        // FIXME this is not the right place to do it but we need the parent otherwise
        int precedence = op.getPrecedence();
        if (left.getPrecedence() < precedence && !PARENTHESIS) { // FIXME should be <= if right
            // associative
            print("(");
            left.accept(this);
            print(")");
        } else {
            left.accept(this);
        }
        print(" ");
        print(op.getOperator().getName());
        print(" ");
        if (right.getPrecedence() < precedence && !PARENTHESIS) { // FIXME should be <= if left
            // associative
            print("(");
            right.accept(this);
            print(")");
        } else {
            right.accept(this);
        }
        if (PARENTHESIS) {
            print(")");
        }
        return null;
    }

    @Override
    public Void visit(UnaryOperation op) {
        if (PARENTHESIS) {
            print("(");
        }
        print(op.getOperator().getName());
        op.getLHS().accept(this);
        if (PARENTHESIS) {
            print(")");
        }
        return null;
    }

    @Override
    public Void visit(Constant n) {
        print(n.prettyValue());
        return null;
    }

    @Override
    public Void visit(Repeat n) {
        print("repeat ");
        n.getBody().accept(this);
        return null;
    }

    @Override
    public Void visit(While n) {
        print("while(");
        n.getCondition().accept(this);
        print(") ");
        n.getBody().accept(this);
        return null;
    }

    @Override
    public Void visit(For n) {
        print("for(");
        print(n.getVariable());
        print(" in ");
        n.getRange().accept(this);
        print(") ");
        n.getBody().accept(this);
        return null;
    }

    @Override
    public Void visit(SimpleAssignVariable n) {
        print(n.getVariable());
        print(" <- ");
        n.getExpr().accept(this);
        return null;
    }

    @Override
    public Void visit(UpdateVector n) {
        print(n.getVector());
        print(" <- ");
        print(n.getRHS());
        return null;
    }

    @Override
    public Void visit(FunctionCall n) {
        print(n.getName() + "(");
        print(n.getArguments(), true);
        print(")");
        return null;
    }

    @Override
    public Void visit(Function n) {
        print("function(");
        print(n.getSignature(), false);
        print(") ");
        n.visitAll(this);
        return null;
    }

    @Override
    public Void visit(SimpleAccessVariable n) {
        print(n.getVariable());
        return null;
    }

    @Override
    public Void visit(FieldAccess n) {
        print(n.getLhs());
        print("$");
        print(n.getFieldName());
        return null;
    }

    @Override
    public Void visit(UpdateField n) {
        n.getVector().accept(this);
        print(" <- ");
        n.getRHS().accept(this);
        return null;
    }

    private void print(List<ArgNode> alist, boolean isCall) {
        boolean f = true;
        for (ArgNode arg : alist) {
            if (!f) {
                print(", ");
            } else {
                f = false;
            }
            print(arg, isCall);
        }
    }

    private void print(ArgNode arg, boolean isCall) {
        String n = arg.getName();
        ASTNode v = arg.getValue();
        if (n != null) {
            print(n);
            if (isCall || v != null) {
                print("=");
            }
        }
        if (v != null) {
            v.accept(this);
        }
    }
}
