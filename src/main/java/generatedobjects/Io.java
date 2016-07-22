/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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

import axoloti.attributedefinition.AxoAttributeComboBox;
import axoloti.displays.DisplayFrac32VU;
import axoloti.inlets.InletBool32;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.inlets.InletFrac32Pos;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author Johannes Taelman
 */
public class Io extends gentools {

    static void GenerateAll() {
        String catName = "gpio/in";
        WriteAxoObject(catName, CreateDigitalRead());
        WriteAxoObject(catName, CreateDigitalReadButton1());
        WriteAxoObject(catName, CreateDigitalReadButton2());
        WriteAxoObject(catName, CreateAnalogRead());

        catName = "gpio/out";
        WriteAxoObject(catName, CreateDigitalWrite());
        WriteAxoObject(catName, CreateLED1());
        WriteAxoObject(catName, CreateLED2());
//        WriteAxoObject(cat,CreateADC());
//        WriteAxoObject(cat,CreateADC2());
//        WriteAxoObject(cat,CreateButton());
        WriteAxoObject(catName, CreatePWMOut_t3());
        WriteAxoObject(catName, CreatePWMOut_t4());
        WriteAxoObject(catName, CreatePWMOut_t5());
        WriteAxoObject(catName, CreatePWMOut_t8());
        WriteAxoObject(catName, CreatePWMOut_v2_t4());
        WriteAxoObject(catName, AnalogOut());

        catName = "audio";
        WriteAxoObject(catName, CreateADCTilde1());
        WriteAxoObject(catName, CreateADCTilde2());
        WriteAxoObject(catName, CreateADCTilde());
        WriteAxoObject(catName, CreateADCConfig());
        WriteAxoObject(catName, CreateADCConfigL());
        WriteAxoObject(catName, CreateADCConfigR());
        WriteAxoObject(catName, CreateADCConfigMic());

        catName = "audio";
        WriteAxoObject(catName, CreateDACTilde1());
        WriteAxoObject(catName, CreateDACTilde2());
        WriteAxoObject(catName, CreateDACTilde());
        WriteAxoObject(catName, CreateDACConfig());

        catName = "gpio/serial";
        WriteAxoObject(catName, SerialBegin());
        catName = "gpio/spi";
        WriteAxoObject(catName, SPIBegin());
        catName = "gpio/i2c";
        WriteAxoObject(catName, I2CBegin());
    }

    static AxoObject CreateAnalogRead() {
        AxoObject o = new AxoObject("analog", "external analog control voltage input");
        o.outlets.add(new OutletFrac32("out", "external analog control voltage input"));
        o.sDescription = "Reads an external analog voltage. Voltage range 0 to 3.3V maps to 0..64, with 12-bit precision. "
                + "Apply no more than 3.3V!";
        String mentries[] = {
            "PA0 (ADC1_IN0)",
            "PA1 (ADC1_IN1)",
            "PA2 (ADC1_IN2)",
            "PA3 (ADC1_IN3)",
            "PA4 (ADC1_IN4)",
            "PA5 (ADC1_IN5)",
            "PA6 (ADC1_IN6)",
            "PA7 (ADC1_IN7)",
            "PB0 (ADC1_IN8)",
            "PB1 (ADC1_IN9)",
            "PC0 (ADC1_IN10)",
            "PC1 (ADC1_IN11)",
            "PC2 (ADC1_IN12)",
            "PC3 (ADC1_IN13)",
            "PC4 (ADC1_IN14)"
        };
        String centries[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14"};
        o.attributes.add(new AxoAttributeComboBox("channel", mentries, centries));
        o.sKRateCode = "%out%= adcvalues[%channel%]<<15;";
        return o;
    }

    static String GpioPinsM[] = {
        "PA0",
        "PA1",
        "PA2",
        "PA3",
        "PA4",
        "PA5",
        "PA6",
        "PA7",
        "PB0",
        "PB1",
        "PB6",
        "PB7",
        "PB8",
        "PB9",
        "PC0",
        "PC1",
        "PC2",
        "PC3",
        "PC4",
        "PC5"
    };

    static String GpioPinsC[] = {
        "GPIOA,0",
        "GPIOA,1",
        "GPIOA,2",
        "GPIOA,3",
        "GPIOA,4",
        "GPIOA,5",
        "GPIOA,6",
        "GPIOA,7",
        "GPIOB,0",
        "GPIOB,1",
        "GPIOB,6",
        "GPIOB,7",
        "GPIOB,8",
        "GPIOB,9",
        "GPIOC,0",
        "GPIOC,1",
        "GPIOC,2",
        "GPIOC,3",
        "GPIOC,4",
        "GPIOC,5",};

    static AxoObject CreateDigitalRead() {
        AxoObject o = new AxoObject("digital", "external digital input");
        o.outlets.add(new OutletBool32("out", "external digital input"));
        o.sDescription = "Reads a digital input pin. 3.3V logic maximum!";
        String GpioIModM[] = {
            "hi-z",
            "pullup",
            "pulldown"
        };
        String GpioIModC[] = {
            "PAL_MODE_INPUT",
            "PAL_MODE_INPUT_PULLUP",
            "PAL_MODE_INPUT_PULLDOWN"
        };
        o.attributes.add(new AxoAttributeComboBox("pad", GpioPinsM, GpioPinsC));
        o.attributes.add(new AxoAttributeComboBox("mode", GpioIModM, GpioIModC));
        o.sInitCode = "   palSetPadMode(%pad%,%mode%);";
        o.sKRateCode = "%out%= palReadPad(%pad%)<<27;";
        return o;
    }

    static AxoObject CreateDigitalReadButton1() {
        AxoObject o = new AxoObject("button1", "button S1");
        o.outlets.add(new OutletBool32("out", "button state"));
        o.sDescription = "button S1 on axoloti core board";
        o.sInitCode = "palSetPadMode(SW1_PORT,SW1_PIN,PAL_MODE_INPUT);";
        o.sKRateCode = "%out%= palReadPad(SW1_PORT,SW1_PIN);";
        return o;
    }
    static AxoObject CreateDigitalReadButton2() {
        AxoObject o = new AxoObject("button2", "button S2");
        o.outlets.add(new OutletBool32("out", "button state"));
        o.sDescription = "button S2 on axoloti core board";
        o.sKRateCode = "%out%= palReadPad(SW2_PORT,SW2_PIN);";
        return o;
    }

    static AxoObject CreateDigitalWrite() {
        AxoObject o = new AxoObject("digital", "external digital output pin control");
        o.inlets.add(new InletBool32("in", "positive = true"));
        String GpioOModM[] = {
            "Push pull",
            "Open drain"
        };
        String GpioOModC[] = {
            "PAL_MODE_OUTPUT_PUSHPULL",
            "PAL_MODE_OUTPUT_OPENDRAIN"
        };
        o.attributes.add(new AxoAttributeComboBox("pad", GpioPinsM, GpioPinsC));
        o.attributes.add(new AxoAttributeComboBox("mode", GpioOModM, GpioOModC));
        o.sInitCode = "   palSetPadMode(%pad%,%mode%);";
        o.sKRateCode = "   palWritePad(%pad%,(%in%>0));";
        return o;
    }

    static AxoObject CreateLED1() {
        AxoObject o = new AxoObject("led1", "controls LED1 (green) on the board");
        o.inlets.add(new InletBool32("in", "true = on"));
        o.sInitCode = "   sysmon_disable_blinker();\n"
                + "   palSetPadMode(LED1_PORT,LED1_PIN,PAL_MODE_OUTPUT_PUSHPULL);";
        o.sKRateCode = "   palWritePad(LED1_PORT,LED1_PIN,(%in%>0));";
        return o;
    }

    static AxoObject CreateLED2() {
        AxoObject o = new AxoObject("led2", "controls LED2 (red) on the board");
        o.inlets.add(new InletBool32("in", "true = on"));
        o.sInitCode = "   sysmon_disable_blinker();\n"
                + "   palSetPadMode(LED2_PORT,LED2_PIN,PAL_MODE_OUTPUT_PUSHPULL);";
        o.sKRateCode = "   palWritePad(LED2_PORT,LED2_PIN,(%in%>0));";
        return o;
    }

    static AxoObject CreatePWMOut_t3() {
        AxoObject o = new AxoObject("pwm t3", "pwm output timer 3");
        o.inlets.add(new InletFrac32Pos("pa6", "pwm ratio"));
        o.inlets.add(new InletFrac32Pos("pa7", "pwm ratio"));
        o.inlets.add(new InletFrac32Pos("pb0", "pwm ratio"));
        o.inlets.add(new InletFrac32Pos("pb1", "pwm ratio"));
        o.sInitCode = "  palSetPadMode(GPIOA, 6, PAL_MODE_ALTERNATE(2));\n"
                + "  palSetPadMode(GPIOA, 7, PAL_MODE_ALTERNATE(2));\n"
                + "  palSetPadMode(GPIOB, 0, PAL_MODE_ALTERNATE(2));\n"
                + "  palSetPadMode(GPIOB, 1, PAL_MODE_ALTERNATE(2));";
        o.sKRateCode = "   pwmEnableChannel(&PWMD3, 0, (pwmcnt_t)((%pa6%>=0)?(4096-(%pa6%>>15)):4096));\n"
                + "   pwmEnableChannel(&PWMD3, 1, (pwmcnt_t)((%pa7%>=0)?(4096-(%pa7%>>15)):4096));\n"
                + "   pwmEnableChannel(&PWMD3, 2, (pwmcnt_t)((%pb0%>=0)?(4096-(%pb0%>>15)):4096));\n"
                + "   pwmEnableChannel(&PWMD3, 3, (pwmcnt_t)((%pb1%>=0)?(4096-(%pb1%>>15)):4096));\n";
        return o;
    }

    static AxoObject CreatePWMOut_t4() {
        AxoObject o = new AxoObject("pwm t4", "pwm output timer 4");
        o.inlets.add(new InletFrac32Pos("pb8", "pwm ratio"));
        o.inlets.add(new InletFrac32Pos("pb9", "pwm ratio"));
        o.sInitCode = "  palSetPadMode(GPIOB, 8, PAL_MODE_ALTERNATE(2));\n"
                + "  palSetPadMode(GPIOB, 9, PAL_MODE_ALTERNATE(2));";
        o.sKRateCode = "   pwmEnableChannel(&PWMD4, 2, (pwmcnt_t)((%pb8%>=0)?(4096-(%pb8%>>15)):4096));\n"
                + "   pwmEnableChannel(&PWMD4, 3, (pwmcnt_t)((%pb9%>=0)?(4096-(%pb9%>>15)):4096));\n";
        return o;
    }

    static AxoObject CreatePWMOut_t5() {
        AxoObject o = new AxoObject("pwm t5", "pwm output timer 5");
        o.inlets.add(new InletFrac32Pos("pa0", "pwm ratio"));
        o.inlets.add(new InletFrac32Pos("pa1", "pwm ratio"));
        o.inlets.add(new InletFrac32Pos("pa2", "pwm ratio"));
        o.inlets.add(new InletFrac32Pos("pa3", "pwm ratio"));
        o.sInitCode = "  palSetPadMode(GPIOA, 0, PAL_MODE_ALTERNATE(2));\n"
                + "  palSetPadMode(GPIOA, 1, PAL_MODE_ALTERNATE(2));\n"
                + "  palSetPadMode(GPIOA, 2, PAL_MODE_ALTERNATE(2));\n"
                + "  palSetPadMode(GPIOA, 3, PAL_MODE_ALTERNATE(2));";
        o.sKRateCode = "   pwmEnableChannel(&PWMD5, 0, (pwmcnt_t)((%pa0%>=0)?(4096-(%pa0%>>15)):4096));\n"
                + "   pwmEnableChannel(&PWMD5, 1, (pwmcnt_t)((%pa1%>=0)?(4096-(%pa1%>>15)):4096));\n"
                + "   pwmEnableChannel(&PWMD5, 2, (pwmcnt_t)((%pa2%>=0)?(4096-(%pa2%>>15)):4096));\n"
                + "   pwmEnableChannel(&PWMD5, 3, (pwmcnt_t)((%pa3%>=0)?(4096-(%pa3%>>15)):4096));\n";
        return o;
    }

    static AxoObject CreatePWMOut_t8() {
        AxoObject o = new AxoObject("pwm t8", "pwm output timer 8");
        o.inlets.add(new InletFrac32Pos("pa5", "pwm ratio"));
        o.inlets.add(new InletFrac32Pos("pc7", "pwm ratio"));
        o.sInitCode = "  palSetPadMode(GPIOA, 5, PAL_MODE_ALTERNATE(3));\n"
                + "  palSetPadMode(GPIOC, 7, PAL_MODE_ALTERNATE(3));\n"
                + "  PWMD8.tim->CCER |= STM32_TIM_CCER_CC1NE | STM32_TIM_CCER_CC1NP;\n";
        o.sKRateCode = "   pwmEnableChannel(&PWMD8, 0, (pwmcnt_t)((%pa5%>=0)?(4096-(%pa5%>>15)):4096));\n"
                + "   pwmEnableChannel(&PWMD8, 1, (pwmcnt_t)((%pc7%>=0)?(4096-(%pc7%>>15)):4096));\n";
        return o;
    }

    static AxoObject CreatePWMOut_v2_t4() {
        AxoObject o = new AxoObject("pwm t4 servo", "pwm output timer 4, scaled for servo motors");
        o.inlets.add(new InletFrac32Pos("pb8", "pwm ratio"));
        o.inlets.add(new InletFrac32Pos("pb9", "pwm ratio"));
        o.sInitCode = "  palSetPadMode(GPIOB, 8, PAL_MODE_ALTERNATE(2));\n"
                + "  palSetPadMode(GPIOB, 9, PAL_MODE_ALTERNATE(2));";
        o.sKRateCode = "   pwmEnableChannel(&PWMD4, 2, (pwmcnt_t)((%pb8%>=0)?(150+(%pb8%>>17)-(%pb8%>>19)):4096));\n"
                + "   pwmEnableChannel(&PWMD4, 3, (pwmcnt_t)((%pb9%>=0)?(150+(%pb9%>>17)-(%pb9%>>19)):4096));";
        return o;
    }

    static AxoObject CreateADCTilde1() {
        AxoObject o = new AxoObject("in left", "Audio input, left channel (or mono)");
        o.displays.add(new DisplayFrac32VU("vu"));
        o.outlets.add(new OutletFrac32Buffer("wave", "Left channel"));
        o.sKRateCode = "int j;\n"
                + "for(j=0;j<BUFSIZE;j++){\n"
                + "   %wave%[j] = AudioInputLeft[j];\n"
                + "}\n"
                + "%vu%=%wave%[0];\n";
        return o;
    }

    static AxoObject CreateADCTilde2() {
        AxoObject o = new AxoObject("in right", "Audio input, right channel");
        o.displays.add(new DisplayFrac32VU("vu"));
        o.outlets.add(new OutletFrac32Buffer("wave", "Right channel"));
        o.sKRateCode = "int j;\n"
                + "for(j=0;j<BUFSIZE;j++){\n"
                + "   %wave%[j] = AudioInputRight[j];\n"
                + "}\n"
                + "%vu%=%wave%[0];\n";
        return o;
    }

    static AxoObject CreateADCTilde() {
        AxoObject o = new AxoObject("in stereo", "Audio input, stereo");
        o.displays.add(new DisplayFrac32VU("vuLeft"));
        o.displays.add(new DisplayFrac32VU("vuRight"));
        o.outlets.add(new OutletFrac32Buffer("left", "Left channel"));
        o.outlets.add(new OutletFrac32Buffer("right", "Right channel"));
        o.sKRateCode = "int j;\n"
                + "for(j=0;j<BUFSIZE;j++){\n"
                + "   %left%[j] = AudioInputLeft[j];\n"
                + "   %right%[j] = AudioInputRight[j];\n"
                + "}\n"
                + "%vuLeft%=%left%[0];\n"
                + "%vuRight%=%right%[0];\n";
        return o;
    }

    static AxoObject CreateDACTilde1() {
        AxoObject o = new AxoObject("out left", "Audio output, left channel (or mono)");
        o.inlets.add(new InletFrac32Buffer("wave", "Left channel"));
        o.displays.add(new DisplayFrac32VU("vu"));
        o.sKRateCode = "int j;\n"
                + "for(j=0;j<BUFSIZE;j++){\n"
                + "   AudioOutputLeft[j] += __SSAT(%wave%[j],28);\n"
                + "}\n"
                + "%vu%=%wave%[0];\n";
        return o;
    }

    static AxoObject CreateDACTilde2() {
        AxoObject o = new AxoObject("out right", "Audio output, right channel");
        o.inlets.add(new InletFrac32Buffer("wave", "Right channel"));
        o.displays.add(new DisplayFrac32VU("vu"));
        o.sKRateCode = "int j;\n"
                + "for(j=0;j<BUFSIZE;j++){\n"
                + "   AudioOutputRight[j] += __SSAT(%wave%[j],28);\n"
                + "}\n"
                + "%vu%=%wave%[0];\n";
        return o;
    }

    static AxoObject CreateDACTilde() {
        AxoObject o = new AxoObject("out stereo", "Audio output, stereo");
        o.inlets.add(new InletFrac32Buffer("left", "Left channel"));
        o.inlets.add(new InletFrac32Buffer("right", "Right channel"));
        o.displays.add(new DisplayFrac32VU("vuLeft"));
        o.displays.add(new DisplayFrac32VU("vuRight"));
        o.sKRateCode = "int j;\n"
                + "for(j=0;j<BUFSIZE;j++){\n"
                + "   AudioOutputLeft[j] += __SSAT(%left%[j],28);\n"
                + "   AudioOutputRight[j] += __SSAT(%right%[j],28);\n"
                + "}\n"
                + "%vuLeft%=%left%[0];\n"
                + "%vuRight%=%right%[0];\n";
        return o;
    }

    static AxoObject CreateADCConfig() {
        AxoObject o = new AxoObject("inconfig", "Audio input configuration");
        String ADCGainM[] = {
            "-12dB",
            "-9dB",
            "-6dB",
            "-3dB",
            "0dB",
            "3dB",
            "6dB",
            "9dB",
            "12dB",
            "15dB",
            "18dB",
            "21dB",
            "24dB",
            "27dB",
            "30dB",
            "33dB"
        };
        String ADCGainC[] = {
            "0",
            "4",
            "8",
            "12",
            "16",
            "20",
            "24",
            "28",
            "32",
            "36",
            "40",
            "44",
            "48",
            "52",
            "56",
            "60"
        };
        o.attributes.add(new AxoAttributeComboBox("gain", ADCGainM, ADCGainC));
        String ADCBoostM[] = {"Mute", "0dB", "20dB"};
        String ADCBoostC[] = {"0", "1", "2"};
        o.attributes.add(new AxoAttributeComboBox("boost", ADCBoostM, ADCBoostC));
        String InputModeM[] = {
            "Stereo",
            "Mono(L)",
            "Balanced(L)"
        };
        String InputModeC[] = {
            "A_STEREO",
            "A_MONO",
            "A_BALANCED"
        };
        o.attributes.add(new AxoAttributeComboBox("mode", InputModeM, InputModeC));
        o.sInitCode = "ADAU1961_WriteRegister(0x400E,(%gain%<<2)+3);\n"
                + "ADAU1961_WriteRegister(0x400F,(%gain%<<2)+3);\n"
                + "ADAU1961_WriteRegister(0x400B,(%boost%<<3)+0);\n"
                + "ADAU1961_WriteRegister(0x400D,(%boost%<<3)+0);\n"
                + "AudioInputMode = %mode%;\n";
        return o;
    }

    static AxoObject CreateADCConfigL() {
        AxoObject o = new AxoObject("inconfig l", "Audio input configuration, left channel only");
        String ADCGainM[] = {
            "-12dB",
            "-9dB",
            "-6dB",
            "-3dB",
            "0dB",
            "3dB",
            "6dB",
            "9dB",
            "12dB",
            "15dB",
            "18dB",
            "21dB",
            "24dB",
            "27dB",
            "30dB",
            "33dB"
        };
        String ADCGainC[] = {
            "0",
            "4",
            "8",
            "12",
            "16",
            "20",
            "24",
            "28",
            "32",
            "36",
            "40",
            "44",
            "48",
            "52",
            "56",
            "60"
        };
        o.attributes.add(new AxoAttributeComboBox("gain", ADCGainM, ADCGainC));
        String ADCBoostM[] = {"Mute", "0dB", "20dB"};
        String ADCBoostC[] = {"0", "1", "2"};
        o.attributes.add(new AxoAttributeComboBox("boost", ADCBoostM, ADCBoostC));

        o.sInitCode = "ADAU1961_WriteRegister(0x400E,(%gain%<<2)+3);\n"
                + "//ADAU1961_WriteRegister(0x400F,(%gain%<<2)+3);\n"
                + "ADAU1961_WriteRegister(0x400B,(%boost%<<3)+0);\n"
                + "//ADAU1961_WriteRegister(0x400D,(%boost%<<3)+0);\n";
        return o;
    }

    static AxoObject CreateADCConfigR() {
        AxoObject o = new AxoObject("inconfig r", "Audio input configuration, right channel only");
        String ADCGainM[] = {
            "-12dB",
            "-9dB",
            "-6dB",
            "-3dB",
            "0dB",
            "3dB",
            "6dB",
            "9dB",
            "12dB",
            "15dB",
            "18dB",
            "21dB",
            "24dB",
            "27dB",
            "30dB",
            "33dB"
        };
        String ADCGainC[] = {
            "0",
            "4",
            "8",
            "12",
            "16",
            "20",
            "24",
            "28",
            "32",
            "36",
            "40",
            "44",
            "48",
            "52",
            "56",
            "60"
        };
        o.attributes.add(new AxoAttributeComboBox("gain", ADCGainM, ADCGainC));
        String ADCBoostM[] = {"Mute", "0dB", "20dB"};
        String ADCBoostC[] = {"0", "1", "2"};
        o.attributes.add(new AxoAttributeComboBox("boost", ADCBoostM, ADCBoostC));

        o.sInitCode = "//ADAU1961_WriteRegister(0x400E,(%gain%<<2)+3);\n"
                + "ADAU1961_WriteRegister(0x400F,(%gain%<<2)+3);\n"
                + "//ADAU1961_WriteRegister(0x400B,(%boost%<<3)+0);\n"
                + "ADAU1961_WriteRegister(0x400D,(%boost%<<3)+0);\n";
        return o;
    }

    static AxoObject CreateADCConfigMic() {
        AxoObject o = new AxoObject("inconfig mic", "Audio input configuration for electret microphone)");
        String BiasM[] = {
            "AVDD x0.90",
            "AVDD x0.65"
        };
        String BiasC[] = {
            "0",
            "4"
        };
        o.attributes.add(new AxoAttributeComboBox("bias", BiasM, BiasC));
        String PerfM[] = {"High", "Normal"};
        String PerfC[] = {"0", "1"};
        o.attributes.add(new AxoAttributeComboBox("mperf", PerfM, PerfC));

        o.sInitCode = "ADAU1961_WriteRegister(0x4010,%bias%+%mperf%+1);\n";
        return o;
    }

    static AxoObject CreateDACConfig() {
        AxoObject o = new AxoObject("outconfig", "Audio output configuration)");
        String HPVolM[] = {
            "-54dB",
            "-48dB",
            "-42dB",
            "-36dB",
            "-30dB",
            "-24dB",
            "-18dB",
            "-12dB",
            "-6dB",
            "0dB",
            "6dB"
        };
        String HPVolC[] = {
            "3",
            "9",
            "15",
            "21",
            "27",
            "33",
            "39",
            "45",
            "51",
            "57",
            "63"
        };
        o.attributes.add(new AxoAttributeComboBox("headphones", HPVolM, HPVolC));
        String OutputModeM[] = {
            "Stereo",
            "Mono(L)",
            "Balanced(L)"
        };
        String OutputModeC[] = {
            "A_STEREO",
            "A_MONO",
            "A_BALANCED"
        };
        o.attributes.add(new AxoAttributeComboBox("mode", OutputModeM, OutputModeC));
        o.sInitCode = "ADAU1961_WriteRegister(0x4023,(%headphones%<<2)+3);\n"
                + "ADAU1961_WriteRegister(0x4024,(%headphones%<<2)+3);\n"
                + "AudioOutputMode = %mode%;\n"
                ;
        return o;
    }

    static AxoObject SerialBegin() {
        AxoObject o = new AxoObject("config", "Configures a serial (uart) interface on pins PA2 (TX) and PA3 (RX), using the SerialDriver API.");
        String baudrates[] = {"1200", "2400", "4800", "9600", "19200", "31250", "38400", "57600", "115200", "250000"};
        o.attributes.add(new AxoAttributeComboBox("baudrate", baudrates, baudrates));
        o.sInitCode = "// setup the pins\n"
                + "// PA2 : TX\n"
                + "// PA3 : RX\n"
                + "\n"
                + "  palSetPadMode(GPIOA, 3, PAL_MODE_ALTERNATE(7)|PAL_MODE_INPUT);// RX\n"
                + "  palSetPadMode(GPIOA, 2, PAL_MODE_OUTPUT_PUSHPULL);// TX\n"
                + "  palSetPadMode(GPIOA, 2, PAL_MODE_ALTERNATE(7));// TX\n"
                + "// 9600 baud\n"
                + "static const SerialConfig sd2Cfg = {%baudrate%, // baud\n"
                + "    0, 0, 0};\n"
                + "sdStart(&SD2, &sd2Cfg);\n";
        o.sDisposeCode = "sdStop(&SD2);\n"
                + "palSetPadMode(GPIOA, 2, PAL_MODE_INPUT_ANALOG);\n"
                + "palSetPadMode(GPIOA, 3, PAL_MODE_INPUT_ANALOG);\n";
        return o;
    }

    static AxoObject SPIBegin() {
        AxoObject o = new AxoObject("config", "Configures a SPI interface. Pin mapping: PA4=NSS PA5=SCK PA6=MISO PA7=MOSI");
        {
            String cpol[] = {"CPOL=0", "CPOL=1"};
            String cpolc[] = {"", "|SPI_CR1_CPOL"};
            o.attributes.add(new AxoAttributeComboBox("clock_polarity", cpol, cpolc));
        }
        {
            String cpha[] = {"CPHA=0", "CPHA=1"};
            String cphac[] = {"", "|SPI_CR1_CPHA"};
            o.attributes.add(new AxoAttributeComboBox("clock_phase", cpha, cphac));
        }
        {
            String cbaud[] = {"FPCLK/2", "FPCLK/4", "FPCLK/8", "FPCLK/16", "FPCLK/32", "FPCLK/64", "FPCLK/128", "FPCLK/256"};
            String cbaudc[] = {"|(0<<3)", "|(1<<3)", "|(2<<3)", "|(3<<3)", "|(4<<3)",
                "|(5<<3)", "|(6<<3)", "|(7<<3)"};
            o.attributes.add(new AxoAttributeComboBox("baudrate", cbaud, cbaudc));
        }
        {
            String cend[] = {"MSB first", "LSB first"};
            String cendc[] = {"", "|SPI_CR1_LSBFIRST"};
            o.attributes.add(new AxoAttributeComboBox("format", cend, cendc));
        }
        o.sInitCode = "// setup the pins\n"
                + "palSetPadMode(GPIOA, 4, PAL_MODE_OUTPUT_PUSHPULL);// NSS\n"
                + "palSetPadMode(GPIOA, 5, PAL_MODE_OUTPUT_PUSHPULL);// SCK\n"
                + "palSetPadMode(GPIOA, 7, PAL_MODE_OUTPUT_PUSHPULL);// MOSI\n"
                + "//palSetPadMode(GPIOA, 4, PAL_MODE_ALTERNATE(5));// NSS\n"
                + "palSetPadMode(GPIOA, 5, PAL_MODE_ALTERNATE(5));// SCK\n"
                + "palSetPadMode(GPIOA, 6, PAL_MODE_ALTERNATE(5));// MISO\n"
                + "palSetPadMode(GPIOA, 7, PAL_MODE_ALTERNATE(5));// MOSI\n"
                + "static const SPIConfig spicfg =\n"
                + "    {NULL, GPIOA, 4, 0 %clock_polarity% %clock_phase% %baudrate% %format%};\n"
                + "spiStart(&SPID1, &spicfg);\n";
        o.sDisposeCode = "spiStop(&SPID1);\n"
                + "palSetPadMode(GPIOA, 4, PAL_MODE_INPUT_ANALOG);\n"
                + "palSetPadMode(GPIOA, 5, PAL_MODE_INPUT_ANALOG);\n"
                + "palSetPadMode(GPIOA, 6, PAL_MODE_INPUT_ANALOG);\n"
                + "palSetPadMode(GPIOA, 7, PAL_MODE_INPUT_ANALOG);\n";
        return o;
    }

    static AxoObject I2CBegin() {
        AxoObject o = new AxoObject("config", "Configures a I2C interface. PB8=SCL PB9=SDA");
        o.sInitCode = "// setup the pins\n"
                + "palSetPadMode(GPIOB, 8, PAL_MODE_ALTERNATE(4)|PAL_STM32_PUDR_PULLUP|PAL_STM32_OTYPE_OPENDRAIN);// SCL\n"
                + "palSetPadMode(GPIOB, 9, PAL_MODE_ALTERNATE(4)|PAL_STM32_PUDR_PULLUP|PAL_STM32_OTYPE_OPENDRAIN);// SDA\n"
                + "static const I2CConfig i2cfg = {\n"
                + "    OPMODE_I2C,\n"
                + "    400000,\n"
                + "    FAST_DUTY_CYCLE_2,\n"
                + "};\n"
                + "i2cStart(&I2CD1, &i2cfg);\n";
        o.sDisposeCode = "i2cStop(&I2CD1);\n"
                + "palSetPadMode(GPIOA, 8, PAL_MODE_INPUT_ANALOG);\n"
                + "palSetPadMode(GPIOA, 9, PAL_MODE_INPUT_ANALOG);\n";
        return o;
    }

    static AxoObject AnalogOut() {
        AxoObject o = new AxoObject("analog", "low-speed 12 bit digital to analog conversion, not suitable for audio signals, but for control voltages...");
        o.inlets.add(new InletFrac32("PA4", "voltage ratio (64u = 3.3V)"));
        o.inlets.add(new InletFrac32("PA5", "voltage ratio (64u = 3.3V)"));
        o.sInitCode = "palSetPadMode(GPIOA, 4, PAL_MODE_INPUT_ANALOG);\n"
                + "palSetPadMode(GPIOA, 5, PAL_MODE_INPUT_ANALOG);\n"
                + "RCC->APB1ENR |= 0x20000000;\n"
                + "DAC->CR |= 0x00030003;\n";
        o.sKRateCode = "DAC->DHR12R1 = %PA4%>>15;\n"
                + "DAC->DHR12R2 = %PA5%>>15;\n";
        o.sDisposeCode = "DAC->CR = 0x0;\n"
                + "RCC->APB1ENR &= ~0x20000000;\n";
        return o;
    }

}
