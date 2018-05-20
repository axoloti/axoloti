/**
 * Copyright (C) 2013, 2014 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
package generatedobjects;

import axoloti.object.AxoObject;
import axoloti.object.AxoObjectAbstract;
import axoloti.object.attribute.AxoAttributeSpinner;
import axoloti.object.inlet.InletFrac32;
import axoloti.object.inlet.InletFrac32Buffer;
import axoloti.object.inlet.InletFrac32BufferPos;
import axoloti.object.inlet.InletFrac32Pos;
import axoloti.object.inlet.InletInt32;
import axoloti.object.outlet.OutletFrac32;
import axoloti.object.outlet.OutletFrac32Buffer;
import axoloti.object.outlet.OutletInt32;
import axoloti.object.parameter.ParameterFrac32UMapGain;
import axoloti.object.parameter.ParameterFrac32UMapGain16;
import static generatedobjects.GenTools.writeAxoObject;
import java.util.ArrayList;

/**
 *
 * @author Johannes Taelman
 */
class Arithmetic extends GenTools {

    static void generateAll() {
        String catName = "math";
        writeAxoObject(catName, createSKIFracOneOp("inv", "negate, negative, y = -x", "-", ""));
        writeAxoObject(catName, createSKIFracOneOp("half", "divide by two", "", ">>1"));
        writeAxoObject(catName, createSKIFracOneOp("div 2", "divide by 2", "", " >>1"));
        writeAxoObject(catName, createSKIFracOneOp("div 4", "divide by 4", "", ">>2"));
        writeAxoObject(catName, createSKIFracOneOp("div 8", "divide by 8", "", ">>3"));
        writeAxoObject(catName, createSKIFracOneOp("div 16", "divide by 16", "", ">>4"));
        writeAxoObject(catName, createSKIFracOneOp("div 32", "divide by 32", "", ">>5"));
        writeAxoObject(catName, createSKIFracOneOp("div 64", "divide by 64", "", ">>6"));
        writeAxoObject(catName, createSKIFracOneOp("div 128", "divide by 128", "", ">>7"));
        writeAxoObject(catName, createSKIFracOneOp("div 256", "divide by 256", "", ">>8"));
        writeAxoObject(catName, createSKFracOneOp("muls 2", "saturated multiply by 2", "%out%= __SSAT(%in%,27)<<1;"));
        writeAxoObject(catName, createSKFracOneOp("muls 4", "saturated multiply by 4", "%out%= __SSAT(%in%,26)<<2;"));
        writeAxoObject(catName, createSKFracOneOp("muls 8", "saturated multiply by 8", "%out%= __SSAT(%in%,25)<<3;"));
        writeAxoObject(catName, createSKFracOneOp("muls 16", "saturated multiply by 16", "%out%= __SSAT(%in%,24)<<4;"));
        writeAxoObject(catName, createSKFracOneOp("muls 32", "saturated multiply by 32", "%out%= __SSAT(%in%,23)<<5;"));
        writeAxoObject(catName, createSKFracOneOp("muls 64", "saturated multiply by 64", "%out%= __SSAT(%in%,22)<<6;"));
        writeAxoObject(catName, createSKFracOneOp("muls 128", "saturated multiply by 128", "%out%= __SSAT(%in%,21)<<7;"));
        writeAxoObject(catName, createSKFracOneOp("muls 256", "saturated multiply by 256", "%out%= __SSAT(%in%,20)<<8;"));

        writeAxoObject(catName, createSKIFracOneOp("double", "multiply by two", "", "<<1"));
        writeAxoObject(catName, createSKIFracOneOp("abs", "absolute value", "%out%= %in%>0?%in%:-%in%;"));

        writeAxoObject(catName, createSKFracOneOp("sat", "saturate to normal range", "%out%= __SSAT(%in%,28);"));
        writeAxoObject(catName, createSKFracOneOp("satp", "saturate to normal positive range", "%out%= __USAT(%in%,27);"));

        writeAxoObject(catName, createSKIFracTwoOp("+", "add", "", "+", ""));
        writeAxoObject(catName, createSKIFracTwoOp("-", "subtract", "", "-", ""));
        writeAxoObject(catName, createKFracTwoOpLogicOut("<", "less than", "", "<", ""));
        writeAxoObject(catName, createKFracTwoOpLogicOut(">", "greater than", "", ">", ""));
        writeAxoObject(catName, createKFracTwoOpLogicOut("==", "equal", "", "==", ""));

        writeAxoObject(catName, createSKFracOneOpFracC("+c", "add constant", "", "+ %c%"));
        writeAxoObject(catName, createSKFracOneOpFracC("-c", "subtract constant", "", "- %c%"));
        writeAxoObject(catName, createKFracOneOpFracCLogicOut("<c", "less than constant", "", "< %c%"));
        writeAxoObject(catName, createKFracOneOpFracCLogicOut(">c", "greater than constant", "", "> %c%"));

        writeAxoObject(catName, createSKIFracTwoOp("min", "minimum", "%out%= (%in1%<%in2%)?%in1%:%in2%;"));
        writeAxoObject(catName, createSKIFracTwoOp("max", "maximum", "%out%= (%in1%>%in2%)?%in1%:%in2%;"));

        writeAxoObject(catName, createSKFracOneOp("round", "round to whole unit with positive bias: 0.49 becomes 0, 0.51 becomes 1, -0.49 becomes 0", "%out%= (%in% + 0x100000)&0xFFE00000;"));
        writeAxoObject(catName, createSKFracOneOp("sqrt", "square root (of absolute value), y = 8*sqrt(x)",
                "int32_t ai = %in%>0?%in%:-%in%;\n"
                + "float aif = ai;\n"
                + "aif *= (1<<27);\n"
                + "aif = _VSQRTF(aif);\n"
                + "%out%= (int)aif;\n"));

        writeAxoObject(catName, createSKFracOneOp("reciprocal", "reciprocal, y = 64/x",
                "if (%in%) {\n"
                + "      float inf = %in%;\n"
                + "      %out% = (int)(281474976710656.f/inf);\n"
                + "    } else\n"
                + "        %out% = 1<<27;\n"));

        writeAxoObject(catName, createC64());
        writeAxoObject(catName, createC32());
        writeAxoObject(catName, createC16());
        writeAxoObject(catName, createC8());
        writeAxoObject(catName, createC4());
        writeAxoObject(catName, createC2());
        writeAxoObject(catName, createC1());

        writeAxoObject(catName, createIFracTwoOp("bitand", "bitwise binary and operator", "%out% = %in1%&%in2%;"));
        writeAxoObject(catName, createIFracTwoOp("bitor", "bitwise binary and operator", "%out% = %in1%|%in2%;"));
        writeAxoObject(catName, createIFracTwoOp("bitxor", "bitwise binary and operator", "%out% = %in1%^%in2%;"));

        writeAxoObject(catName, createIDivideRemainder());

        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<>();
            c.add(createKAmp());
            c.add(createSAmp());
            writeAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<>();
            c.add(createKGain());
            c.add(createSGain());
            writeAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<>();
            c.add(createKRateLeftShift());
            c.add(createSRateLeftShift());
            writeAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<>();
            c.add(createKRateRightShift());
            c.add(createSRateRightShift());
            writeAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<>();
            c.add(create_Plus1());
            c.add(create_Plus1i());
            c.add(create_Plus1Tilde());
            writeAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<>();
            c.add(create_Minus1());
            c.add(create_Minus1i());
            c.add(create_Minus1Tilde());
            writeAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<>();
            c.add(createStarF());
            c.add(createStarFS());
            c.add(createStarSF());
            c.add(createStarFI());
            c.add(createStarIF());
            c.add(createStarI());
            c.add(createStarS());
            writeAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<>();
            c.add(createKLog());
            c.add(createSLog());
            writeAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<>();
            c.add(createKExp());
            c.add(createSExp());
            writeAxoObject(catName, c);
        }
    }

    static AxoObject createSRateLeftShift() {
        AxoObject o = new AxoObject("<<", "Left shift. Doubles the value \"shift\" times, with overflow. If overflow is not desireable, use \"muls\".");
        o.outlets.add(new OutletFrac32Buffer("result", "a amplified with i 6dB steps"));
        o.inlets.add(new InletFrac32Buffer("a", "a"));
        o.attributes.add(new AxoAttributeSpinner("shift", 0, 31, 1));
        o.sSRateCode = "%result%= %a% << %shift%;";
        return o;
    }

    static AxoObject createSRateRightShift() {
        AxoObject o = new AxoObject(">>", "attenuates a s-rate signal with \"shift\" times 6dB");
        o.outlets.add(new OutletFrac32Buffer("result", "a attenuated with i 6dB steps"));
        o.inlets.add(new InletFrac32Buffer("a", "a"));
        o.attributes.add(new AxoAttributeSpinner("shift", 0, 31, 1));
        o.sSRateCode = "%result%= %a% >> %shift%;";
        return o;
    }

    static AxoObject createKRateRightShift() {
        AxoObject o = new AxoObject(">>", "attenuates a k-rate signal with \"shift\" times 6dB");
        o.outlets.add(new OutletFrac32("result", "a attenuated in 6dB steps"));
        o.inlets.add(new InletFrac32("a", "a"));
        o.attributes.add(new AxoAttributeSpinner("shift", 0, 31, 1));
        o.sKRateCode = "%result%= %a% >> %shift%;";
        return o;
    }

    static AxoObject createKRateLeftShift() {
        AxoObject o = new AxoObject("<<", "Left shift. Doubles the value \"shift\" times, with overflow. If overflow is not desireable, use \"muls\".");
        o.outlets.add(new OutletFrac32("result", "a amplified in 6dB steps"));
        o.inlets.add(new InletFrac32("a", "a"));
        o.attributes.add(new AxoAttributeSpinner("shift", 0, 31, 1));
        o.sKRateCode = "%result%= %a% << %shift%;";
        return o;
    }

    static AxoObject create_Plus1() {
        AxoObject o = new AxoObject("+1", "adds one unit");
        o.inlets.add(new InletFrac32("a", "a"));
        o.outlets.add(new OutletFrac32("result", "a+1"));
        o.sKRateCode = "%result%= %a%+(1<<21);";
        return o;
    }

    static AxoObject create_Plus1i() {
        AxoObject o = new AxoObject("+1", "adds one unit");
        o.inlets.add(new InletInt32("a", "a"));
        o.outlets.add(new OutletInt32("result", "a+1"));
        o.sKRateCode = "%result%= %a%+1;";
        return o;
    }

    static AxoObject create_Plus1Tilde() {
        AxoObject o = new AxoObject("+1", "adds one unit");
        o.inlets.add(new InletFrac32Buffer("a", "a"));
        o.outlets.add(new OutletFrac32Buffer("result", "a+1"));
        o.sSRateCode = "%result%= %a%+(1<<21);";
        return o;
    }

    static AxoObject create_Minus1() {
        AxoObject o = new AxoObject("-1", "subtracts one unit");
        o.inlets.add(new InletFrac32("a", "a"));
        o.outlets.add(new OutletFrac32("result", "a-1"));
        o.sKRateCode = "%result%= %a%-(1<<21);";
        return o;
    }

    static AxoObject create_Minus1i() {
        AxoObject o = new AxoObject("-1", "subtracts one unit");
        o.inlets.add(new InletInt32("a", "a"));
        o.outlets.add(new OutletInt32("result", "a-1"));
        o.sKRateCode = "%result%= %a%-1;";
        return o;
    }

    static AxoObject create_Minus1Tilde() {
        AxoObject o = new AxoObject("-1", "subtracts one unit");
        o.inlets.add(new InletFrac32Buffer("a", "a"));
        o.outlets.add(new OutletFrac32Buffer("result", "a-1"));
        o.sSRateCode = "%result%= %a%-(1<<21);";
        return o;
    }

    static AxoObject createKAmp() {
        AxoObject o = new AxoObject("*c", "Multiply (attenuate) with a constant value");
        o.outlets.add(new OutletFrac32("out", "output"));
        o.inlets.add(new InletFrac32("in", "input"));
        o.params.add(new ParameterFrac32UMapGain("amp"));
        o.sKRateCode = "%out%= ___SMMUL(%amp%,%in%)<<1;\n";
        return o;
    }

    static AxoObject createSAmp() {
        AxoObject o = new AxoObject("*c", "Multiply (attenuate) with a constant value");
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.params.add(new ParameterFrac32UMapGain("amp"));
        o.sSRateCode = "%out%= ___SMMUL(%amp%,%in%)<<1;\n";
        return o;
    }

    static AxoObject createKGain() {
        AxoObject o = new AxoObject("gain", "amplify up to 16 times (saturated)");
        o.outlets.add(new OutletFrac32("out", "output"));
        o.inlets.add(new InletFrac32("in", "input"));
        o.params.add(new ParameterFrac32UMapGain16("amp"));
        o.sKRateCode = "%out%= __SSAT(___SMMUL(%amp%,__SSAT(%in%,28)<<4)<<1,28);\n";
        return o;
    }

    static AxoObject createSGain() {
        AxoObject o = new AxoObject("gain", "amplify up to 16 times (saturated)");
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.params.add(new ParameterFrac32UMapGain16("amp"));
        o.sSRateCode = "%out%= __SSAT(___SMMUL(%amp%,__SSAT(%in%,28)<<4)<<1,28);\n";
        return o;
    }

    static AxoObject createStarS() {
        AxoObject o = new AxoObject("*", "multiply");
        o.inlets.add(new InletFrac32Buffer("a", "input"));
        o.inlets.add(new InletFrac32Buffer("b", "input"));
        o.outlets.add(new OutletFrac32Buffer("result", "output"));
        o.sSRateCode = "%result%= ___SMMUL(%a%<<3,%b%<<2);\n";
        return o;
    }

    static AxoObject createStarSF() {
        AxoObject o = new AxoObject("*", "multiply");
        o.inlets.add(new InletFrac32Buffer("a", "input"));
        o.inlets.add(new InletFrac32("b", "input"));
        o.outlets.add(new OutletFrac32Buffer("result", "output"));
        o.sSRateCode = "%result%= ___SMMUL(%a%<<3,%b%<<2);\n";
        return o;
    }

    static AxoObject createStarFS() {
        AxoObject o = new AxoObject("*", "multiply");
        o.inlets.add(new InletFrac32("a", "input"));
        o.inlets.add(new InletFrac32Buffer("b", "input"));
        o.outlets.add(new OutletFrac32Buffer("result", "output"));
        o.sSRateCode = "%result%= ___SMMUL(%a%<<3,%b%<<2);\n";
        return o;
    }

    static AxoObject createStarF() {
        AxoObject o = new AxoObject("*", "multiply");
        o.inlets.add(new InletFrac32("a", "input"));
        o.inlets.add(new InletFrac32("b", "input"));
        o.outlets.add(new OutletFrac32("result", "output"));
        o.sKRateCode = "%result%= ___SMMUL(%a%<<3,%b%<<2);\n";
        return o;
    }

    static AxoObject createStarFI() {
        AxoObject o = new AxoObject("*", "multiply");
        o.inlets.add(new InletFrac32("a", "input"));
        o.inlets.add(new InletInt32("b", "input"));
        o.outlets.add(new OutletFrac32("result", "output"));
        o.sKRateCode = "%result%= %a%*%b%;\n";
        return o;
    }

    static AxoObject createStarIF() {
        AxoObject o = new AxoObject("*", "multiply");
        o.inlets.add(new InletInt32("a", "input"));
        o.inlets.add(new InletFrac32("b", "input"));
        o.outlets.add(new OutletFrac32("result", "output"));
        o.sKRateCode = "%result%= %a%*%b%;\n";
        return o;
    }

    static AxoObject createStarI() {
        AxoObject o = new AxoObject("*", "multiply");
        o.inlets.add(new InletInt32("a", "input"));
        o.inlets.add(new InletInt32("b", "input"));
        o.outlets.add(new OutletInt32("result", "output"));
        o.sKRateCode = "%result%= %a%*%b%;\n";
        return o;
    }

    static AxoObject createC64() {
        AxoObject o = new AxoObject("c 64", "constant value: 64");
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "%o%= 64;\n";
        return o;
    }

    static AxoObject createC32() {
        AxoObject o = new AxoObject("c 32", "constant value: 32");
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "%o%= 32;\n";
        return o;
    }

    static AxoObject createC16() {
        AxoObject o = new AxoObject("c 16", "constant value: 16");
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "%o%= 16;\n";
        return o;
    }

    static AxoObject createC8() {
        AxoObject o = new AxoObject("c 8", "constant value: 8");
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "%o%= 8;\n";
        return o;
    }

    static AxoObject createC4() {
        AxoObject o = new AxoObject("c 4", "constant value: 4");
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "%o%= 4;\n";
        return o;
    }

    static AxoObject createC2() {
        AxoObject o = new AxoObject("c 2", "constant value: 2");
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "%o%= 2;\n";
        return o;
    }

    static AxoObject createC1() {
        AxoObject o = new AxoObject("c 1", "constant value: 1");
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "%o%= 1;\n";
        return o;
    }

    static AxoObject createIDivideRemainder() {
        AxoObject o = new AxoObject("divremc", "divide integer with constant, also outputs remainder (modulo)");
        o.outlets.add(new OutletInt32("div", "a divided by denominator"));
        o.outlets.add(new OutletInt32("rem", "remainder of division by denominator"));
        o.inlets.add(new InletInt32("a", "nominator"));
        o.attributes.add(new AxoAttributeSpinner("denominator", 1, 128, 1));
        o.sKRateCode = "int r;\n"
                + "if (%a% >=0)\n"
                + "  r = ((unsigned int)%a%)/%denominator%;\n"
                + "else"
                + "  r = -(((unsigned int)(%denominator%-%a%))/%denominator%);\n"
                + "%div%= r;\n"
                + "%rem% = %a%-(r*%denominator%);\n";
        return o;
    }

    static AxoObject createKLog() {
        AxoObject o = new AxoObject("log", "logarithm, y=16+8*log2(x)");
        o.inlets.add(new InletFrac32Pos("a", "input"));
        o.outlets.add(new OutletFrac32("result", "output"));
//        o.outlets.add(new OutletFrac32("r1", "output1"));
//        o.outlets.add(new OutletInt32("r2", "output2"));
//        o.outlets.add(new OutletFrac32("r3", "output3"));
//        o.outlets.add(new OutletInt32("exponent", "output2"));
        o.sKRateCode = "Float_t f;\n"
                + "f.f = %a%;\n"
                //              + "%exponent% = f.parts.exponent;\n"
                + "int32_t r1 = ((f.parts.exponent&0x7F)-18) << 24;\n"
                //                + "int32_t r2 = f.parts.mantissa>>15;\n"
                + "int32_t r3 = logt[f.parts.mantissa>>15]<<10;\n"
                + "%result% = r1 + r3;\n";
        // 8 units/doubling
        // 64 = 8 doublings
        // 16 bit audio range = -64 to 64
        return o;
    }

    static AxoObject createSLog() {
        AxoObject o = new AxoObject("log", "logarithm, y=16+8*log2(x)");
        o.inlets.add(new InletFrac32BufferPos("a", "input"));
        o.outlets.add(new OutletFrac32Buffer("result", "output"));
        o.sSRateCode = "Float_t f;\n"
                + "f.f = %a%;\n"
                + "int32_t r1 = ((f.parts.exponent&0x7F)-18) << 24;\n"
                + "int32_t r3 = logt[f.parts.mantissa>>15]<<10;\n"
                + "%result% = r1 + r3;\n";
        return o;
    }

    static AxoObject createKExp() {
        AxoObject o = new AxoObject("exp", "exponent function, y=pow(2,(x/8)-2)");
        o.inlets.add(new InletFrac32Pos("a", "input"));
        o.outlets.add(new OutletFrac32("result", "output"));
        o.sKRateCode = "int8_t s = (%a%>>24)+4;\n"
                + "uint8_t ei = %a%>>16;\n"
                + "if (s>=0)\n"
                + "     %result% = expt[ei]<<s;\n"
                + "else %result% = expt[ei]>>(-s);\n";
        return o;
    }

    static AxoObject createSExp() {
        AxoObject o = new AxoObject("exp", "exponent function, y=pow(2,(x/8)-2)");
        o.inlets.add(new InletFrac32BufferPos("a", "input"));
        o.outlets.add(new OutletFrac32Buffer("result", "output"));
        o.sSRateCode = "int8_t s = (%a%>>24)+4;\n"
                + "uint8_t ei = %a%>>16;\n"
                + "if (s>=0)\n"
                + "     %result% = expt[ei]<<s;\n"
                + "else %result% = expt[ei]>>(-s);\n";
        return o;
    }
}
