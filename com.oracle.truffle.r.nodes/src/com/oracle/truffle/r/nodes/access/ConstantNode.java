/*
 * Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.truffle.r.nodes.access;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.frame.*;
import com.oracle.truffle.r.nodes.*;
import com.oracle.truffle.api.source.*;
import com.oracle.truffle.r.runtime.*;
import com.oracle.truffle.r.runtime.data.*;
import com.oracle.truffle.r.runtime.data.model.*;

public abstract class ConstantNode extends RNode implements VisibilityController {

    public final Object getValue() {
        return execute(null);
    }

    public static ConstantNode create(Object value) {
        if (value instanceof Integer) {
            return new ConstantIntegerScalarNode((Integer) value);
        } else if (value instanceof Double) {
            return new ConstantDoubleScalarNode((Double) value);
        } else if (value instanceof Boolean) {
            return new ConstantLogicalScalarNode((Boolean) value);
        } else if (value instanceof Byte) {
            return new ConstantLogicalScalarNode((Byte) value);
        } else if (value instanceof String) {
            return new ConstantStringScalarNode((String) value);
        } else if (value == RNull.instance) {
            return new ConstantNullNode();
        } else if (value == RMissing.instance) {
            return new ConstantMissingNode();
        } else if (value == EMPTY_OBJECT_ARRAY) {
            return new ConstantEmptyObjectArrayNode();
        } else if (value instanceof RComplex) {
            return new ConstantComplexNode((RComplex) value);
        } else if (value instanceof RAbstractVector) {
            return new ConstantVectorNode((RAbstractVector) value);
        } else if (value instanceof RDataFrame) {
            return new ConstantDataFrameNode((RDataFrame) value);
        } else if (value instanceof Object[]) {
            return new ConstantObjectArrayNode((Object[]) value);
        } else if (value instanceof RRaw) {
            return new ConstantRawNode((RRaw) value);
        } else if (value instanceof RFunction) {
            return new ConstantFunctionNode((RFunction) value);
        } else if (value instanceof RFormula) {
            return new ConstantFormulaNode((RFormula) value);
        }
        CompilerDirectives.transferToInterpreter();
        throw new UnsupportedOperationException(value.getClass().getName());
    }

    public static ConstantNode create(SourceSection src, Object value) {
        ConstantNode cn = create(value);
        cn.assignSourceSection(src);
        return cn;
    }

    private static final class ConstantDoubleScalarNode extends ConstantNode {

        private final Double objectValue;
        private final double doubleValue;

        public ConstantDoubleScalarNode(double value) {
            this.objectValue = value;
            this.doubleValue = value;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            controlVisibility();
            return objectValue;
        }

        @Override
        public double executeDouble(VirtualFrame frame) {
            controlVisibility();
            return doubleValue;
        }

    }

    private static final class ConstantLogicalScalarNode extends ConstantNode {

        private final double doubleValue;
        private final int intValue;
        private final byte logicalValue;

        public ConstantLogicalScalarNode(boolean value) {
            this.logicalValue = RRuntime.asLogical(value);
            this.doubleValue = value ? 1.0d : 0.0d;
            this.intValue = value ? 1 : 0;
        }

        public ConstantLogicalScalarNode(byte value) {
            this.logicalValue = value;
            this.doubleValue = value == RRuntime.LOGICAL_TRUE ? 1.0d : 0.0d;
            this.intValue = value == RRuntime.LOGICAL_TRUE ? 1 : 0;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            controlVisibility();
            return logicalValue;
        }

        @Override
        public int executeInteger(VirtualFrame frame) {
            controlVisibility();
            return intValue;
        }

        @Override
        public byte executeByte(VirtualFrame frame) {
            controlVisibility();
            return logicalValue;
        }

        @Override
        public double executeDouble(VirtualFrame frame) {
            controlVisibility();
            return doubleValue;
        }

    }

    private static final class ConstantIntegerScalarNode extends ConstantNode {

        private final Integer objectValue;
        private final int intValue;
        private final double doubleValue;

        public ConstantIntegerScalarNode(int value) {
            this.objectValue = value;
            this.intValue = value;
            this.doubleValue = value;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            controlVisibility();
            return objectValue;
        }

        @Override
        public int executeInteger(VirtualFrame frame) {
            controlVisibility();
            return intValue;
        }

        @Override
        public double executeDouble(VirtualFrame frame) {
            controlVisibility();
            return doubleValue;
        }

    }

    private static final class ConstantStringScalarNode extends ConstantNode {

        private final String objectValue;

        public ConstantStringScalarNode(String value) {
            this.objectValue = value;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            controlVisibility();
            return objectValue;
        }

        @Override
        public String executeString(VirtualFrame frame) {
            controlVisibility();
            return objectValue;
        }

    }

    private static final class ConstantComplexNode extends ConstantNode {

        private final RComplex complexValue;

        public ConstantComplexNode(RComplex value) {
            this.complexValue = value;
        }

        @Override
        public RComplex executeRComplex(VirtualFrame frame) {
            controlVisibility();
            return complexValue;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            controlVisibility();
            return complexValue;
        }

    }

    private static final class ConstantNullNode extends ConstantNode {

        @Override
        public RNull executeNull(VirtualFrame frame) {
            controlVisibility();
            return RNull.instance;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            controlVisibility();
            return RNull.instance;
        }
    }

    public static final class ConstantMissingNode extends ConstantNode {

        @Override
        public RMissing executeMissing(VirtualFrame frame) {
            controlVisibility();
            return RMissing.instance;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            controlVisibility();
            return RMissing.instance;
        }
    }

    private static final class ConstantEmptyObjectArrayNode extends ConstantNode {

        @Override
        public Object[] executeArray(VirtualFrame frame) {
            controlVisibility();
            return EMPTY_OBJECT_ARRAY;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            controlVisibility();
            return EMPTY_OBJECT_ARRAY;
        }
    }

    private static final class ConstantObjectArrayNode extends ConstantNode {

        private final Object[] array;

        public ConstantObjectArrayNode(Object[] array) {
            this.array = array;
        }

        @Override
        public Object[] executeArray(VirtualFrame frame) {
            controlVisibility();
            return array;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            controlVisibility();
            return array;
        }
    }

    private static final class ConstantVectorNode extends ConstantNode {

        private final RAbstractVector vector;

        public ConstantVectorNode(RAbstractVector vector) {
            this.vector = vector;
        }

        @Override
        public RAbstractVector executeRAbstractVector(VirtualFrame frame) {
            controlVisibility();
            return vector;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            controlVisibility();
            return vector;
        }
    }

    private static final class ConstantDataFrameNode extends ConstantNode {

        private final RDataFrame dataFrame;

        public ConstantDataFrameNode(RDataFrame dataFrame) {
            this.dataFrame = dataFrame;
        }

        @Override
        public RDataFrame executeRDataFrame(VirtualFrame frame) {
            controlVisibility();
            return dataFrame;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            controlVisibility();
            return dataFrame;
        }
    }

    private static final class ConstantRawNode extends ConstantNode {

        private final RRaw data;

        public ConstantRawNode(RRaw data) {
            this.data = data;
        }

        @Override
        public RRaw executeRRaw(VirtualFrame frame) {
            controlVisibility();
            return data;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            controlVisibility();
            return data;
        }
    }

    private static final class ConstantFunctionNode extends ConstantNode {

        private final RFunction function;

        public ConstantFunctionNode(RFunction function) {
            this.function = function;
        }

        @Override
        public RFunction executeFunction(VirtualFrame frame) {
            controlVisibility();
            return function;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            controlVisibility();
            return function;
        }
    }

    private static final class ConstantFormulaNode extends ConstantNode {

        private final RFormula formula;

        public ConstantFormulaNode(RFormula formula) {
            this.formula = formula;
        }

        @Override
        public RFormula executeFormula(VirtualFrame frame) {
            controlVisibility();
            return formula;
        }

        @Override
        public Object execute(VirtualFrame frame) {
            controlVisibility();
            return formula;
        }
    }
}
