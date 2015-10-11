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

import axoloti.attributedefinition.AxoAttributeSpinner;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.inlets.InletFrac32BufferPos;
import axoloti.inlets.InletFrac32Pos;
import axoloti.inlets.InletInt32;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectAbstract;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.outlets.OutletInt32;
import axoloti.parameters.ParameterFrac32UMapGain;
import axoloti.parameters.ParameterFrac32UMapGain16;
import static generatedobjects.gentools.WriteAxoObject;
import java.util.ArrayList;

/**
 *
 * @author Johannes Taelman
 */
public class Arithmetic extends gentools {

    static void GenerateAll() {
        String catName = "math";
        WriteAxoObject(catName, CreateSKIFracOneOp("inv", "negate, negative, y = -x", "-", ""));
        WriteAxoObject(catName, CreateSKIFracOneOp("half", "divide by two", "", ">>1"));
        WriteAxoObject(catName, CreateSKIFracOneOp("div 2", "divide by 2", "", " >>1"));
        WriteAxoObject(catName, CreateSKIFracOneOp("div 4", "divide by 4", "", ">>2"));
        WriteAxoObject(catName, CreateSKIFracOneOp("div 8", "divide by 8", "", ">>3"));
        WriteAxoObject(catName, CreateSKIFracOneOp("div 16", "divide by 16", "", ">>4"));
        WriteAxoObject(catName, CreateSKIFracOneOp("div 32", "divide by 32", "", ">>5"));
        WriteAxoObject(catName, CreateSKIFracOneOp("div 64", "divide by 64", "", ">>6"));
        WriteAxoObject(catName, CreateSKIFracOneOp("div 128", "divide by 128", "", ">>7"));
        WriteAxoObject(catName, CreateSKIFracOneOp("div 256", "divide by 256", "", ">>8"));
        WriteAxoObject(catName, CreateSKFracOneOp("muls 2", "saturated multiply by 2", "%out%= __SSAT(%in%,27)<<1;"));
        WriteAxoObject(catName, CreateSKFracOneOp("muls 4", "saturated multiply by 4", "%out%= __SSAT(%in%,26)<<2;"));
        WriteAxoObject(catName, CreateSKFracOneOp("muls 8", "saturated multiply by 8", "%out%= __SSAT(%in%,25)<<3;"));
        WriteAxoObject(catName, CreateSKFracOneOp("muls 16", "saturated multiply by 16", "%out%= __SSAT(%in%,24)<<4;"));
        WriteAxoObject(catName, CreateSKFracOneOp("muls 32", "saturated multiply by 32", "%out%= __SSAT(%in%,23)<<5;"));
        WriteAxoObject(catName, CreateSKFracOneOp("muls 64", "saturated multiply by 64", "%out%= __SSAT(%in%,22)<<6;"));
        WriteAxoObject(catName, CreateSKFracOneOp("muls 128", "saturated multiply by 128", "%out%= __SSAT(%in%,21)<<7;"));
        WriteAxoObject(catName, CreateSKFracOneOp("muls 256", "saturated multiply by 256", "%out%= __SSAT(%in%,20)<<8;"));

        WriteAxoObject(catName, CreateSKIFracOneOp("double", "multiply by two", "", "<<1"));
        WriteAxoObject(catName, CreateSKIFracOneOp("abs", "absolute value", "%out%= %in%>0?%in%:-%in%;"));

        WriteAxoObject(catName, CreateSKFracOneOp("sat", "saturate to normal range", "%out%= __SSAT(%in%,28);"));
        WriteAxoObject(catName, CreateSKFracOneOp("satp", "saturate to normal positive range", "%out%= __USAT(%in%,27);"));

        WriteAxoObject(catName, CreateSKIFracTwoOp("+", "add", "", "+", ""));
        WriteAxoObject(catName, CreateSKIFracTwoOp("-", "subtract", "", "-", ""));
        WriteAxoObject(catName, CreateKFracTwoOpLogicOut("<", "less than", "", "<", ""));
        WriteAxoObject(catName, CreateKFracTwoOpLogicOut(">", "greater than", "", ">", ""));
        WriteAxoObject(catName, CreateKFracTwoOpLogicOut("==", "equal", "", "==", ""));

        WriteAxoObject(catName, CreateSKFracOneOpFracC("+c", "add constant", "", "+ %c%"));
        WriteAxoObject(catName, CreateSKFracOneOpFracC("-c", "subtract constant", "", "- %c%"));
        WriteAxoObject(catName, CreateKFracOneOpFracCLogicOut("<c", "less than constant", "", "< %c%"));
        WriteAxoObject(catName, CreateKFracOneOpFracCLogicOut(">c", "greater than constant", "", "> %c%"));

        WriteAxoObject(catName, CreateSKIFracTwoOp("min", "minimum", "%out%= (%in1%<%in2%)?%in1%:%in2%;"));
        WriteAxoObject(catName, CreateSKIFracTwoOp("max", "maximum", "%out%= (%in1%>%in2%)?%in1%:%in2%;"));

        WriteAxoObject(catName, CreateSKFracOneOp("round", "round to whole unit with positive bias: 0.49 becomes 0, 0.51 becomes 1, -0.49 becomes 0", "%out%= (%in% + 0x100000)&0xFFE00000;"));
        WriteAxoObject(catName, CreateSKFracOneOp("sqrt", "square root (of absolute value), y = 8*sqrt(x)",
                "int32_t ai = %in%>0?%in%:-%in%;\n"
                + "float aif = ai;\n"
                + "aif *= (1<<27);\n"
                + "aif = _VSQRTF(aif);\n"
                + "%out%= (int)aif;\n"));

        WriteAxoObject(catName, CreateSKFracOneOp("reciprocal", "reciprocal, y = 64/x",
                "if (%in%) {\n"
                + "      float inf = %in%;\n"
                + "      %out% = (int)(281474976710656.f/inf);\n"
                + "    } else\n"
                + "        %out% = 1<<27;\n"));

        WriteAxoObject(catName, CreateC64());
        WriteAxoObject(catName, CreateC32());
        WriteAxoObject(catName, CreateC16());
        WriteAxoObject(catName, CreateC8());
        WriteAxoObject(catName, CreateC4());
        WriteAxoObject(catName, CreateC2());
        WriteAxoObject(catName, CreateC1());

        WriteAxoObject(catName, CreateIFracTwoOp("bitand", "bitwise binary and operator", "%out% = %in1%&%in2%;"));
        WriteAxoObject(catName, CreateIFracTwoOp("bitor", "bitwise binary and operator", "%out% = %in1%|%in2%;"));
        WriteAxoObject(catName, CreateIFracTwoOp("bitxor", "bitwise binary and operator", "%out% = %in1%^%in2%;"));

        WriteAxoObject(catName, CreateIDivideRemainder());

        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(CreateKAmp());
            c.add(CreateSAmp());
            WriteAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(CreateKGain());
            c.add(CreateSGain());
            WriteAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(CreateKRateLeftShift());
            c.add(CreateSRateLeftShift());
            WriteAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(CreateKRateRightShift());
            c.add(CreateSRateRightShift());
            WriteAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(Create_Plus1());
            c.add(Create_Plus1i());
            c.add(Create_Plus1Tilde());
            WriteAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(Create_Minus1());
            c.add(Create_Minus1i());
            c.add(Create_Minus1Tilde());
            WriteAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(CreateStarF());
            c.add(CreateStarFS());
            c.add(CreateStarSF());
            c.add(CreateStarFI());
            c.add(CreateStarIF());
            c.add(CreateStarI());
            c.add(CreateStarS());
            WriteAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(CreateKLog());
            c.add(CreateSLog());
            WriteAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(CreateKExp());
            c.add(CreateSExp());
            WriteAxoObject(catName, c);
        }
    }

    static AxoObject CreateSRateLeftShift() {
        AxoObject o = new AxoObject("<<", "Left shift. Doubles the value \"shift\" times, with overflow. If overflow is not desireable, use \"muls\".");
        o.outlets.add(new OutletFrac32Buffer("result", "a amplified with i 6dB steps"));
        o.inlets.add(new InletFrac32Buffer("a", "a"));
        o.attributes.add(new AxoAttributeSpinner("shift", 0, 31, 1));
        o.sSRateCode = "%result%= %a% << %shift%;";
        return o;
    }

    static AxoObject CreateSRateRightShift() {
        AxoObject o = new AxoObject(">>", "attenuates a s-rate signal with \"shift\" times 6dB");
        o.outlets.add(new OutletFrac32Buffer("result", "a attenuated with i 6dB steps"));
        o.inlets.add(new InletFrac32Buffer("a", "a"));
        o.attributes.add(new AxoAttributeSpinner("shift", 0, 31, 1));
        o.sSRateCode = "%result%= %a% >> %shift%;";
        return o;
    }

    static AxoObject CreateKRateRightShift() {
        AxoObject o = new AxoObject(">>", "attenuates a k-rate signal with \"shift\" times 6dB");
        o.outlets.add(new OutletFrac32("result", "a attenuated in 6dB steps"));
        o.inlets.add(new InletFrac32("a", "a"));
        o.attributes.add(new AxoAttributeSpinner("shift", 0, 31, 1));
        o.sKRateCode = "%result%= %a% >> %shift%;";
        return o;
    }

    static AxoObject CreateKRateLeftShift() {
        AxoObject o = new AxoObject("<<", "Left shift. Doubles the value \"shift\" times, with overflow. If overflow is not desireable, use \"muls\".");
        o.outlets.add(new OutletFrac32("result", "a amplified in 6dB steps"));
        o.inlets.add(new InletFrac32("a", "a"));
        o.attributes.add(new AxoAttributeSpinner("shift", 0, 31, 1));
        o.sKRateCode = "%result%= %a% << %shift%;";
        return o;
    }

    static AxoObject Create_Plus1() {
        AxoObject o = new AxoObject("+1", "adds one unit");
        o.inlets.add(new InletFrac32("a", "a"));
        o.outlets.add(new OutletFrac32("result", "a+1"));
        o.sKRateCode = "%result%= %a%+(1<<21);";
        return o;
    }

    static AxoObject Create_Plus1i() {
        AxoObject o = new AxoObject("+1", "adds one unit");
        o.inlets.add(new InletInt32("a", "a"));
        o.outlets.add(new OutletInt32("result", "a+1"));
        o.sKRateCode = "%result%= %a%+1;";
        return o;
    }

    static AxoObject Create_Plus1Tilde() {
        AxoObject o = new AxoObject("+1", "adds one unit");
        o.inlets.add(new InletFrac32Buffer("a", "a"));
        o.outlets.add(new OutletFrac32Buffer("result", "a+1"));
        o.sSRateCode = "%result%= %a%+(1<<21);";
        return o;
    }

    static AxoObject Create_Minus1() {
        AxoObject o = new AxoObject("-1", "subtracts one unit");
        o.inlets.add(new InletFrac32("a", "a"));
        o.outlets.add(new OutletFrac32("result", "a-1"));
        o.sKRateCode = "%result%= %a%-(1<<21);";
        return o;
    }

    static AxoObject Create_Minus1i() {
        AxoObject o = new AxoObject("-1", "subtracts one unit");
        o.inlets.add(new InletInt32("a", "a"));
        o.outlets.add(new OutletInt32("result", "a-1"));
        o.sKRateCode = "%result%= %a%-1;";
        return o;
    }

    static AxoObject Create_Minus1Tilde() {
        AxoObject o = new AxoObject("-1", "subtracts one unit");
        o.inlets.add(new InletFrac32Buffer("a", "a"));
        o.outlets.add(new OutletFrac32Buffer("result", "a-1"));
        o.sSRateCode = "%result%= %a%-(1<<21);";
        return o;
    }

    static AxoObject CreateKAmp() {
        AxoObject o = new AxoObject("*c", "Multiply (attenuate) with a constant value");
        o.outlets.add(new OutletFrac32("out", "output"));
        o.inlets.add(new InletFrac32("in", "input"));
        o.params.add(new ParameterFrac32UMapGain("amp"));
        o.sKRateCode = "%out%= ___SMMUL(%amp%,%in%)<<1;\n";
        return o;
    }

    static AxoObject CreateSAmp() {
        AxoObject o = new AxoObject("*c", "Multiply (attenuate) with a constant value");
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.params.add(new ParameterFrac32UMapGain("amp"));
        o.sSRateCode = "%out%= ___SMMUL(%amp%,%in%)<<1;\n";
        return o;
    }

    static AxoObject CreateKGain() {
        AxoObject o = new AxoObject("gain", "amplify up to 16 times (saturated)");
        o.outlets.add(new OutletFrac32("out", "output"));
        o.inlets.add(new InletFrac32("in", "input"));
        o.params.add(new ParameterFrac32UMapGain16("amp"));
        o.sKRateCode = "%out%= __SSAT(___SMMUL(%amp%,__SSAT(%in%,28)<<4)<<1,28);\n";
        return o;
    }

    static AxoObject CreateSGain() {
        AxoObject o = new AxoObject("gain", "amplify up to 16 times (saturated)");
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.params.add(new ParameterFrac32UMapGain16("amp"));
        o.sSRateCode = "%out%= __SSAT(___SMMUL(%amp%,__SSAT(%in%,28)<<4)<<1,28);\n";
        return o;
    }

    static AxoObject CreateStarS() {
        AxoObject o = new AxoObject("*", "multiply");
        o.inlets.add(new InletFrac32Buffer("a", "input"));
        o.inlets.add(new InletFrac32Buffer("b", "input"));
        o.outlets.add(new OutletFrac32Buffer("result", "output"));
        o.sSRateCode = "%result%= ___SMMUL(%a%<<3,%b%<<2);\n";
        return o;
    }

    static AxoObject CreateStarSF() {
        AxoObject o = new AxoObject("*", "multiply");
        o.inlets.add(new InletFrac32Buffer("a", "input"));
        o.inlets.add(new InletFrac32("b", "input"));
        o.outlets.add(new OutletFrac32Buffer("result", "output"));
        o.sSRateCode = "%result%= ___SMMUL(%a%<<3,%b%<<2);\n";
        return o;
    }

    static AxoObject CreateStarFS() {
        AxoObject o = new AxoObject("*", "multiply");
        o.inlets.add(new InletFrac32("a", "input"));
        o.inlets.add(new InletFrac32Buffer("b", "input"));
        o.outlets.add(new OutletFrac32Buffer("result", "output"));
        o.sSRateCode = "%result%= ___SMMUL(%a%<<3,%b%<<2);\n";
        return o;
    }

    static AxoObject CreateStarF() {
        AxoObject o = new AxoObject("*", "multiply");
        o.inlets.add(new InletFrac32("a", "input"));
        o.inlets.add(new InletFrac32("b", "input"));
        o.outlets.add(new OutletFrac32("result", "output"));
        o.sKRateCode = "%result%= ___SMMUL(%a%<<3,%b%<<2);\n";
        return o;
    }

    static AxoObject CreateStarFI() {
        AxoObject o = new AxoObject("*", "multiply");
        o.inlets.add(new InletFrac32("a", "input"));
        o.inlets.add(new InletInt32("b", "input"));
        o.outlets.add(new OutletFrac32("result", "output"));
        o.sKRateCode = "%result%= %a%*%b%;\n";
        return o;
    }

    static AxoObject CreateStarIF() {
        AxoObject o = new AxoObject("*", "multiply");
        o.inlets.add(new InletInt32("a", "input"));
        o.inlets.add(new InletFrac32("b", "input"));
        o.outlets.add(new OutletFrac32("result", "output"));
        o.sKRateCode = "%result%= %a%*%b%;\n";
        return o;
    }

    static AxoObject CreateStarI() {
        AxoObject o = new AxoObject("*", "multiply");
        o.inlets.add(new InletInt32("a", "input"));
        o.inlets.add(new InletInt32("b", "input"));
        o.outlets.add(new OutletInt32("result", "output"));
        o.sKRateCode = "%result%= %a%*%b%;\n";
        return o;
    }

    static AxoObject CreateC64() {
        AxoObject o = new AxoObject("c 64", "constant value: 64");
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "%o%= 64;\n";
        return o;
    }

    static AxoObject CreateC32() {
        AxoObject o = new AxoObject("c 32", "constant value: 32");
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "%o%= 32;\n";
        return o;
    }

    static AxoObject CreateC16() {
        AxoObject o = new AxoObject("c 16", "constant value: 16");
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "%o%= 16;\n";
        return o;
    }

    static AxoObject CreateC8() {
        AxoObject o = new AxoObject("c 8", "constant value: 8");
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "%o%= 8;\n";
        return o;
    }

    static AxoObject CreateC4() {
        AxoObject o = new AxoObject("c 4", "constant value: 4");
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "%o%= 4;\n";
        return o;
    }

    static AxoObject CreateC2() {
        AxoObject o = new AxoObject("c 2", "constant value: 2");
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "%o%= 2;\n";
        return o;
    }

    static AxoObject CreateC1() {
        AxoObject o = new AxoObject("c 1", "constant value: 1");
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "%o%= 1;\n";
        return o;
    }

    static AxoObject CreateIDivideRemainder() {
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

    static AxoObject CreateKLog() {
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

    static AxoObject CreateSLog() {
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

    static AxoObject CreateKExp() {
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

    static AxoObject CreateSExp() {
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
