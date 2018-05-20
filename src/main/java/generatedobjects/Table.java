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
import axoloti.object.attribute.AxoAttributeComboBox;
import axoloti.object.attribute.AxoAttributeObjRef;
import axoloti.object.attribute.AxoAttributeSDFile;
import axoloti.object.attribute.AxoAttributeTextEditor;
import axoloti.object.inlet.InletBool32Rising;
import axoloti.object.inlet.InletCharPtr32;
import axoloti.object.inlet.InletFrac32;
import axoloti.object.inlet.InletFrac32Bipolar;
import axoloti.object.inlet.InletFrac32Buffer;
import axoloti.object.inlet.InletFrac32BufferPos;
import axoloti.object.inlet.InletFrac32Pos;
import axoloti.object.inlet.InletInt32Pos;
import axoloti.object.outlet.OutletFrac32;
import axoloti.object.outlet.OutletFrac32Buffer;
import axoloti.object.parameter.ParameterFrac32SMapPitch;
import axoloti.object.parameter.ParameterFrac32SMapVSlider;
import axoloti.object.parameter.ParameterInt32Box;
import static generatedobjects.GenTools.writeAxoObject;

/**
 *
 * @author Johannes Taelman
 */
class Table extends GenTools {

    static void generateAll() {
        String catName = "table";
        writeAxoObject(catName, createRamTable8());
        writeAxoObject(catName, createRamTable16());
        writeAxoObject(catName, createRamTable32());

        writeAxoObject(catName, createSdRamTable8());
        writeAxoObject(catName, createSdRamTable16());
        writeAxoObject(catName, createSdRamTable32());

        writeAxoObject(catName, createSdRamTable16Load());

        writeAxoObject(catName, createRamTable32Slider16());
        writeAxoObject(catName, new AxoObject[]{createTableReadI(), createTableRead(), createTableReadTilde()});
        writeAxoObject(catName, new AxoObject[]{createTableRead2(), createTableRead2T()});
        writeAxoObject(catName, new AxoObject[]{createTableWrite(), createTableWriteI()});
        writeAxoObject(catName, createTableRecord());
        writeAxoObject(catName, createTablePlay());
        writeAxoObject(catName, createTablePlayPitch());
        writeAxoObject(catName, createTablePlayPitchLoop());
        writeAxoObject(catName, createSaveTable());
        writeAxoObject(catName, createLoadTable());
    }

    static AxoObject createRamTable8() {
        AxoObject o = new AxoObject("alloc 8b", "allocate table in RAM memory, -128..127");
        String mentries[] = {"2", "4", "8", "16", "32", "64", "128", "256", "512",
            "1024", "2048", "4096", "8192", "16384", "32768"};
        String centries[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
        o.attributes.add(new AxoAttributeComboBox("size", mentries, centries));
        o.attributes.add(new AxoAttributeTextEditor("init"));
        o.sLocalData = "static const uint32_t LENGTHPOW = (%size%);\n"
                + "static const uint32_t LENGTH = (1<<%size%);\n"
                + "static const uint32_t LENGTHMASK = ((1<<%size%)-1);\n"
                + "static const uint32_t BITS = 8;\n"
                + "static const uint32_t GAIN = 20;\n"
                + "int8_t array[LENGTH];\n";
        o.sInitCode = "{ \n"
                + "  int i;\n"
                + "  for(i=0;i<LENGTH;i++) array[i]=0;\n"
                + "}\n"
                + "%init%";
        return o;
    }

    static AxoObject createRamTable16() {
        AxoObject o = new AxoObject("alloc 16b", "allocate 16bit table in RAM memory, -128.00 .. 127.99");
        String mentries[] = {"2", "4", "8", "16", "32", "64", "128", "256", "512",
            "1024", "2048", "4096", "8192", "16384", "32768"};
        String centries[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
        o.attributes.add(new AxoAttributeComboBox("size", mentries, centries));
        o.attributes.add(new AxoAttributeTextEditor("init"));
        o.sLocalData = "static const uint32_t LENGTHPOW = (%size%);\n"
                + "static const uint32_t LENGTH = (1<<%size%);\n"
                + "static const uint32_t LENGTHMASK = ((1<<%size%)-1);\n"
                + "static const uint32_t BITS = 16;\n"
                + "static const uint32_t GAIN = 12;\n"
                + "int16_t array[LENGTH];\n";
        o.sInitCode = "{ \n"
                + "  int i;\n"
                + "  for(i=0;i<LENGTH;i++) array[i]=0;\n"
                + "}\n"
                + "%init%";
        return o;
    }

    static AxoObject createRamTable32() {
        AxoObject o = new AxoObject("alloc 32b", "allocate 32bit table in RAM memory");
        String mentries[] = {"2", "4", "8", "16", "32", "64", "128", "256", "512",
            "1024", "2048", "4096", "8192", "16384"};
        String centries[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14"};
        o.attributes.add(new AxoAttributeComboBox("size", mentries, centries));
        o.attributes.add(new AxoAttributeTextEditor("init"));
        o.sLocalData = "static const uint32_t LENGTHPOW = %size%;\n"
                + "static const uint32_t LENGTH = 1<<%size%;\n"
                + "static const uint32_t LENGTHMASK = (1<<%size%)-1;\n"
                + "static const uint32_t BITS = 32;\n"
                + "static const uint32_t GAIN = 0;\n"
                + "int32_t array[LENGTH];\n";
        o.sInitCode = "{ \n"
                + "  int i;\n"
                + "  for(i=0;i<LENGTH;i++) array[i]=0;\n"
                + "}\n"
                + "%init%";
        return o;
    }

    static AxoObject createSdRamTable8() {
        AxoObject o = new AxoObject("alloc 8b sdram", "allocate table in SDRAM memory, -128..127");
        String mentries[] = {"2", "4", "8", "16", "32", "64", "128", "256", "512",
            "1024", "2048", "4096", "8192", "16384", "32768",
            "65536", "131072", "262144", "524288", "1048576", "2097152","4194304"};
        String centries[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22"};
        o.attributes.add(new AxoAttributeComboBox("size", mentries, centries));
        o.attributes.add(new AxoAttributeTextEditor("init"));
        o.sLocalData = "static const uint32_t LENGTHPOW = (%size%);\n"
                + "static const uint32_t LENGTH = (1<<%size%);\n"
                + "static const uint32_t LENGTHMASK = ((1<<%size%)-1);\n"
                + "static const uint32_t BITS = 8;\n"
                + "static const uint32_t GAIN = 20;\n"
                + "int8_t *array;\n";
        o.sInitCode = "static int8_t _array[attr_poly][LENGTH] __attribute__ ((section (\".sdram\")));\n"
                + "array = &_array[parent->polyIndex][0];\n"
                + "{ \n"
                + "  int i;\n"
                + "  for(i=0;i<LENGTH;i++) array[i]=0;\n"
                + "}\n"
                + "%init%";
        return o;
    }

    static AxoObject createSdRamTable16() {
        AxoObject o = new AxoObject("alloc 16b sdram", "allocate 16bit table in SDRAM memory, -128.00 .. 127.99");
        String mentries[] = {"2", "4", "8", "16", "32", "64", "128", "256", "512",
            "1024", "2048", "4096", "8192", "16384", "32768",
            "65536", "131072", "262144", "524288", "1048576", "2097152", "4194304"};
        String centries[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22"};
        o.attributes.add(new AxoAttributeComboBox("size", mentries, centries));
        o.attributes.add(new AxoAttributeTextEditor("init"));
        o.sLocalData = "static const uint32_t LENGTHPOW = (%size%);\n"
                + "static const uint32_t LENGTH = (1<<%size%);\n"
                + "static const uint32_t LENGTHMASK = ((1<<%size%)-1);\n"
                + "static const uint32_t BITS = 16;\n"
                + "static const uint32_t GAIN = 12;\n"
                + "int16_t *array;\n";
        o.sInitCode = "static int16_t _array[attr_poly][LENGTH] __attribute__ ((section (\".sdram\")));\n"
                + "array = &_array[parent->polyIndex][0];\n"
                + "{ \n"
                + "  int i;\n"
                + "  for(i=0;i<LENGTH;i++) array[i]=0;\n"
                + "}\n"
                + "%init%";
        return o;
    }

    static AxoObject createSdRamTable16Load() {
        AxoObject o = new AxoObject("alloc 16b sdram load", "allocate 16bit table in SDRAM memory, -128.00 .. 127.99");
        String mentries[] = {"2", "4", "8", "16", "32", "64", "128", "256", "512",
            "1024", "2048", "4096", "8192", "16384", "32768",
            "65536", "131072", "262144", "524288", "1048576", "2097152", "4194304"};
        String centries[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22"};
        o.attributes.add(new AxoAttributeComboBox("size", mentries, centries));
        o.attributes.add(new AxoAttributeSDFile("filename"));
        o.sLocalData = "static const uint32_t LENGTHPOW = (%size%);\n"
                + "static const uint32_t LENGTH = (1<<%size%);\n"
                + "static const uint32_t LENGTHMASK = ((1<<%size%)-1);\n"
                + "static const uint32_t BITS = 16;\n"
                + "static const uint32_t GAIN = 12;\n"
                + "int16_t *array;\n";
        o.sInitCode = "static int16_t _array[attr_poly][LENGTH] __attribute__ ((section (\".sdram\")));\n"
                + "array = &_array[parent->polyIndex][0];\n"
                + "int i;\n"
                + "for(i=0;i<LENGTH;i++) array[i]=0;\n"
                + "FIL FileObject;\n"
                + "FRESULT err;\n"
                + "UINT bytes_read;\n"
                + "err = f_open(&FileObject, \"%filename%\", FA_READ | FA_OPEN_EXISTING);\n"
                + "if (err != FR_OK) {report_fatfs_error(err,\"%filename%\"); return;}\n"
                + "int rem_sz = sizeof(_array[0]);\n"
                + "int offset = 0;\n"
                + "while (rem_sz>0) {\n"
                + "  if (rem_sz>sizeof(fbuff)) {\n"
                + "    err = f_read(&FileObject, fbuff, sizeof(fbuff),&bytes_read);\n"
                + "    if (bytes_read == 0) break;\n"
                + "    memcpy((char *)(&_array[0]) + offset,(char *)fbuff,bytes_read);\n"
                + "    rem_sz -= bytes_read;\n"
                + "    offset += bytes_read;\n"
                + "  } else {\n"
                + "    err = f_read(&FileObject, fbuff, rem_sz, &bytes_read);\n"
                + "    memcpy((char *)(&_array[0]) + offset,(char *)fbuff,bytes_read);\n"
                + "    rem_sz = 0;\n"
                + "  }\n"
                + "}"
                + "if (err != FR_OK) {LogTextMessage(\"Read failed\\n\"); return;}\n"
                + "err = f_close(&FileObject);\n"
                + "if (err != FR_OK) {LogTextMessage(\"Close failed\\n\"); return;}\n";
        return o;
    }

    static AxoObject createSdRamTable32() {
        AxoObject o = new AxoObject("alloc 32b sdram", "allocate 32bit table in SDRAM memory");
        String mentries[] = {"2", "4", "8", "16", "32", "64", "128", "256", "512",
            "1024", "2048", "4096", "8192", "16384", "32768",
            "65536", "131072", "262144", "524288", "1048576", "2097152"};
        String centries[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21"};
        o.attributes.add(new AxoAttributeComboBox("size", mentries, centries));
        o.attributes.add(new AxoAttributeTextEditor("init"));
        o.sLocalData = "static const uint32_t LENGTHPOW = %size%;\n"
                + "static const uint32_t LENGTH = 1<<%size%;\n"
                + "static const uint32_t LENGTHMASK = (1<<%size%)-1;\n"
                + "static const uint32_t BITS = 32;\n"
                + "static const uint32_t GAIN = 0;\n"
                + "int32_t *array;\n";
        o.sInitCode = "static int32_t _array[attr_poly][LENGTH] __attribute__ ((section (\".sdram\")));\n"
                + "array = &_array[parent->polyIndex][0];\n"
                + "{ \n"
                + "  int i;\n"
                + "  for(i=0;i<LENGTH;i++) array[i]=0;\n"
                + "}\n"
                + "%init%";
        return o;
    }

    static AxoObject createRamTable32Slider16() {
        AxoObject o = new AxoObject("allocate 32b 16sliders", "table in RAM memory, direct from sliders");
        for (int i = 0; i < 16; i++) {
            ParameterFrac32SMapVSlider p = new ParameterFrac32SMapVSlider("b" + i);
            p.noLabel = true;
            o.params.add(p);
        }
        o.setRotatedParams(true);
        o.sLocalData = "static const uint32_t LENGTHPOW = 4;\n"
                + "static const uint32_t LENGTH = 1<<4;\n"
                + "static const uint32_t LENGTHMASK = (1<<4)-1;\n"
                + "static const uint32_t BITS = 32;\n"
                + "static const uint32_t GAIN = 0;\n"
                + "int32_t array[LENGTH];\n";
        o.sInitCode = "{ \n"
                + "  int i;\n"
                + "  for(i=0;i<LENGTH;i++) array[i]=0;"
                + "}";
        o.sKRateCode = "";
        for (int i = 0; i < 16; i++) {
            o.sKRateCode += "array[" + i + "] = %b" + i + "%;\n";
        }
        return o;
    }

    static AxoObject createTableReadI() {
        AxoObject o = new AxoObject("read", "read from table, nearest neighbour");
        o.outlets.add(new OutletFrac32("o", "table[a]"));
        o.inlets.add(new InletInt32Pos("a", "index"));
        o.attributes.add(new AxoAttributeObjRef("table"));
        o.sKRateCode = "   %o%= %table%.array[__USAT(%a%,%table%.LENGTHPOW)]<<%table%.GAIN;\n";
        return o;
    }

    static AxoObject createTableRead() {
        AxoObject o = new AxoObject("read", "read from table, nearest neighbour");
        o.outlets.add(new OutletFrac32("o", "table[a]"));
        o.inlets.add(new InletFrac32Pos("a", "index in fraction of table size"));
        o.attributes.add(new AxoAttributeObjRef("table"));
        o.sKRateCode = "   %o%= %table%.array[__USAT(%a%,27)>>(27-%table%.LENGTHPOW)]<<%table%.GAIN;\n";
        return o;
    }

    static AxoObject createTableReadTilde() {
        AxoObject o = new AxoObject("read", "read from table, nearest neighbour");
        o.outlets.add(new OutletFrac32Buffer("o", "table[a]"));
        o.inlets.add(new InletFrac32BufferPos("a", "index in fraction of table size"));
        o.attributes.add(new AxoAttributeObjRef("table"));
        o.sSRateCode = "   %o%= %table%.array[__USAT(%a%,27)>>(27-%table%.LENGTHPOW)]<<%table%.GAIN;\n";
        return o;
    }

    static AxoObject createTableRead2() {
        AxoObject o = new AxoObject("read interp", "read from table, linear interpolated");
        o.outlets.add(new OutletFrac32("o", "table[a]"));
        o.inlets.add(new InletFrac32Pos("a", "index in fraction of table size"));
        o.attributes.add(new AxoAttributeObjRef("table"));
        o.sKRateCode = "   uint32_t asat = __USAT(%a%,27);\n"
                + "    int index = asat>>(27-%table%.LENGTHPOW);\n"
                + "   int32_t y1 = %table%.array[index]<<%table%.GAIN;\n"
                + "   int32_t y2 = %table%.array[(index+1)&%table%.LENGTHMASK]<<%table%.GAIN;\n"
                + "   int frac = (asat - (index<<(27-%table%.LENGTHPOW)))<<(%table%.LENGTHPOW+3);\n"
                + "  int32_t rr;\n"
                + "  rr = ___SMMUL(y1,(1<<30)-frac);\n"
                + "  rr = ___SMMLA(y2,frac,rr);\n"
                + "%o%= rr<<2;\n";
        return o;
    }

    static AxoObject createTableRead2T() {
        AxoObject o = new AxoObject("read interp", "read from table, linear interpolated");
        o.outlets.add(new OutletFrac32Buffer("o", "table[a]"));
        o.inlets.add(new InletFrac32BufferPos("a", "index in fraction of table size"));
        o.attributes.add(new AxoAttributeObjRef("table"));
        o.sSRateCode = "   uint32_t asat = __USAT(%a%,27);\n"
                + "    int index = asat>>(27-%table%.LENGTHPOW);\n"
                + "   int32_t y1 = %table%.array[index]<<%table%.GAIN;\n"
                + "   int32_t y2 = %table%.array[(index+1)&%table%.LENGTHMASK]<<%table%.GAIN;\n"
                + "   int frac = (asat - (index<<(27-%table%.LENGTHPOW)))<<(%table%.LENGTHPOW+3);\n"
                + "  int32_t rr;\n"
                + "  rr = ___SMMUL(y1,(1<<30)-frac);\n"
                + "  rr = ___SMMLA(y2,frac,rr);\n"
                + "%o%= rr<<2;\n";
        return o;
    }

    static AxoObject createTableWrite() {
        AxoObject o = new AxoObject("write", "write to table");
        o.inlets.add(new InletFrac32Pos("a", "index in fraction of table size"));
        o.inlets.add(new InletFrac32("v", "value"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.attributes.add(new AxoAttributeObjRef("table"));
        o.sLocalData = "   int ntrig;\n";
        o.sInitCode =  "   int ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) {\n"
                + "      ntrig=1;\n"
                + "      %table%.array[__USAT(%a%,27)>>(27-%table%.LENGTHPOW)]=__SSAT(%v%,28)>>%table%.GAIN;\n"
                + "   }\n"
                + "   if (!(%trig%>0)) ntrig=0;\n";
        return o;
    }

    static AxoObject createTableWriteI() {
        AxoObject o = new AxoObject("write", "write to table");
        o.inlets.add(new InletInt32Pos("a", "index (integer, not fraction)"));
        o.inlets.add(new InletFrac32("v", "value"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.attributes.add(new AxoAttributeObjRef("table"));
        o.sLocalData = "   int ntrig;\n";
        o.sInitCode =  "   int ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) {\n"
                + "      ntrig=1;\n"
                + "      if (%a%<%table%.LENGTH)\n"
                + "         %table%.array[%a%]=__SSAT(%v%,28)>>%table%.GAIN;\n"
                + "   }\n"
                + "   if (!(%trig%>0)) ntrig=0;\n";
        return o;
    }

    static AxoObject createTableRecord() {
        AxoObject o = new AxoObject("record", "record audio into table, starting from position");
        o.inlets.add(new InletFrac32Buffer("wave", "wave"));
        o.inlets.add(new InletFrac32Pos("pos", "start position in table"));
        o.inlets.add(new InletBool32Rising("start", "start playback"));
        o.inlets.add(new InletBool32Rising("stop", "stop playback"));
        o.attributes.add(new AxoAttributeObjRef("table"));
        o.sLocalData = "   int pstart;\n"
                + "   int pstop;\n"
                + "   int pos;\n";
        o.sInitCode = "pos = 0;\n"
                + "pstart = 0;\n"
                + "pstop = 1;\n";
        o.sKRateCode = "   if ((%start%>0) && !pstart) {\n"
                + "      pstart = 1;\n"
                + "      pstop = 0;\n"
                + "      uint32_t asat = __USAT(%pos%,27);\n"
                + "      pos = asat>>(27-%table%.LENGTHPOW);\n"
                + "  } else if (!(%start%>0)) {\n"
                + "      pstart = 0;\n"
                + "  }\n"
                + "  if ((%stop%>0) && !pstop) {\n"
                + "      pstop = 1;\n"
                + "      pstart = 0;\n"
                + "  } \n";
        o.sSRateCode = "   if (!pstop)  {\n"
                + "       if (pos< %table%.LENGTH)\n"
                + "              %table%.array[pos++] = __SSAT(%wave%,28)>>%table%.GAIN;\n"
                + "   }";
        return o;
    }

    static AxoObject createTablePlay() {
        AxoObject o = new AxoObject("play", "play audio from table (non-transposed), starting from position");
        o.outlets.add(new OutletFrac32Buffer("wave", "wave"));
        o.inlets.add(new InletFrac32Pos("pos", "start position in table"));
        o.inlets.add(new InletBool32Rising("start", "start playback"));
        o.inlets.add(new InletBool32Rising("stop", "stop playback"));
        o.attributes.add(new AxoAttributeObjRef("table"));
        o.sLocalData = "   int pstart;\n"
                + "   int pstop;\n"
                + "   int pos;\n";
        o.sInitCode = "pos = 0;\n"
                + "pstart = 0;\n"
                + "pstop = 1;\n";
        o.sKRateCode = "   if ((%start%>0) && !pstart) {\n"
                + "      pstart = 1;\n"
                + "      pstop = 0;\n"
                + "      uint32_t asat = __USAT(%pos%,27);\n"
                + "      pos = asat>>(27-%table%.LENGTHPOW);\n"
                + "   } else if (!(%start%>0)) {\n"
                + "      pstart = 0;\n"
                + "   }\n"
                + "  if ((%stop%>0) && !pstop) {\n"
                + "      pstop = 1;\n"
                + "      pstart = 0;\n"
                + "   } \n"
                + "\n";
        o.sSRateCode = "   if (!pstop) {\n"
                + "       if (pos< %table%.LENGTH)\n"
                + "              %wave% = %table%.array[pos++]<<%table%.GAIN;\n"
                + "	else %wave% = 0;\n"
                + "   } else %wave% = 0;\n";
        return o;
    }

    static AxoObject createTablePlayPitch() {
        AxoObject o = new AxoObject("play pitch", "play audio sample from table with pitch control, starting from position");
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.outlets.add(new OutletFrac32Buffer("wave", "wave"));
        o.inlets.add(new InletBool32Rising("start", "start playback"));
        o.inlets.add(new InletBool32Rising("stop", "stop playback"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch modulation"));
        o.inlets.add(new InletFrac32Pos("pos", "start position in table"));
        o.attributes.add(new AxoAttributeObjRef("table"));
        o.sLocalData = "   int pstart;\n"
                + "   int pstop;\n"
                + "   uint64_t pos;\n";
        o.sInitCode = "pos = 0;\n"
                + "pstart = 0;\n"
                + "pstop = 1;\n";
        o.sKRateCode = "   if ((inlet_start>0) && !pstart) {\n"
                + "      pstart = 1;\n"
                + "      pstop = 0;\n"
                + "      uint32_t asat = __USAT(inlet_pos,27);\n"
                + "      pos = ((uint64_t)(asat>>(27-attr_table.LENGTHPOW)))<<32;\n"
                + "   } else if (!(inlet_start>0)) {\n"
                + "      pstart = 0;\n"
                + "   }\n"
                + "   if ((inlet_stop>0) && !pstop) {\n"
                + "      pstop = 1;\n"
                + "      pstart = 0;\n"
                + "   }\n"
                + "   uint32_t f0;\n"
                + "   MTOFEXTENDED(inlet_pitch + 80179668 - param_pitch,f0);\n";
        o.sSRateCode = "   if (!pstop) {\n"
                + "      if ((pos>>32)<attr_table.LENGTH) {\n"
                + "         uint32_t r = ___SMMUL(attr_table.array[pos>>32]<<attr_table.GAIN,INT32_MAX-(((uint32_t)pos)>>1));\n"
                + "         r = ___SMMLA(attr_table.array[(pos>>32)+1]<<attr_table.GAIN,(((uint32_t)pos)>>1),r);\n"
                + "         outlet_wave = r;\n"
                + "         pos += ((uint64_t)f0)<<4;\n"
                + "      }\n"
                + "      else outlet_wave = 0;\n"
                + "   } else outlet_wave = 0;\n";
        return o;
    }

    static AxoObject createTablePlayPitchLoop() {
        AxoObject o = new AxoObject("play pitch loop", "play audio sample from table with pitch control, starting from position");
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterInt32Box("loopstart", 0, 1 << 30));
        o.params.add(new ParameterInt32Box("loopend", 0, 1 << 30));
        o.outlets.add(new OutletFrac32Buffer("wave", "wave"));
        o.inlets.add(new InletBool32Rising("start", "start playback"));
        o.inlets.add(new InletBool32Rising("stop", "stop playback"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch modulation"));
        o.inlets.add(new InletFrac32Pos("pos", "start position in table"));
        o.attributes.add(new AxoAttributeObjRef("table"));
        o.sLocalData = "   int pstart;\n"
                + "   int pstop;\n"
                + "   uint64_t pos;\n";
        o.sInitCode = "pos = 0;\n"
                + "pstart = 0;\n"
                + "pstop = 1;\n";
        o.sKRateCode = "   if ((inlet_start>0) && !pstart) {\n"
                + "      pstart = 1;\n"
                + "      pstop = 0;\n"
                + "      uint32_t asat = __USAT(inlet_pos,27);\n"
                + "      pos = ((uint64_t)(asat>>(27-attr_table.LENGTHPOW)))<<32;\n"
                + "   } else if (!(inlet_start>0)) {\n"
                + "      pstart = 0;\n"
                + "   }\n"
                + "   if ((inlet_stop>0) && !pstop) {\n"
                + "      pstop = 1;\n"
                + "      pstart = 0;\n"
                + "   }\n"
                + "   uint32_t f0;\n"
                + "   MTOFEXTENDED(param_pitch + inlet_pitch,f0);\n";
        o.sSRateCode = "   if (!pstop) {\n"
                + "      if ((pos>>32)<attr_table.LENGTH) {\n"
                + "         uint32_t r = ___SMMUL(attr_table.array[pos>>32]<<attr_table.GAIN,INT32_MAX-(((uint32_t)pos)>>1));\n"
                + "         r = ___SMMLA(attr_table.array[(pos>>32)+1]<<attr_table.GAIN,(((uint32_t)pos)>>1),r);\n"
                + "         outlet_wave = r;\n"
                + "         pos += ((uint64_t)f0)<<4;\n"
                + "      }\n"
                + "      else outlet_wave = 0;\n"
                + "   } else outlet_wave = 0;\n";
        return o;
    }

    static AxoObject createSaveTable() {
        AxoObject o = new AxoObject("save", "save table to sdcard");
        o.attributes.add(new AxoAttributeObjRef("table"));
        o.inlets.add(new InletCharPtr32("filename", "file name"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.includes.add("chibios/ext/fatfs/src/ff.h");
        o.sLocalData = "  int ntrig;\n";
        o.sInitCode = "  ntrig = 0;\n";
        o.sKRateCode = "  if ((%trig%>0) && !ntrig) {\n"
                + "    ntrig=1;\n"
                + "    FIL FileObject;\n"
                + "    FRESULT err;\n"
                + "    UINT bytes_written;\n"
                + "    err = f_open(&FileObject, %filename%, FA_WRITE | FA_CREATE_ALWAYS);\n"
                + "    if (err != FR_OK) {report_fatfs_error(err,\"%filename%\"); return;}\n"
                + "    int rem_sz = sizeof(*%table%.array)*%table%.LENGTH;\n"
                + "    int offset = 0;\n"
                + "    while (rem_sz>0) {\n"
                + "      if (rem_sz>sizeof(fbuff)) {\n"
                + "        memcpy((char *)fbuff,(char *)(&%table%.array[0]) + offset, sizeof(fbuff));\n"
                + "        err = f_write(&FileObject, fbuff, sizeof(fbuff),&bytes_written);\n"
                + "        rem_sz -= sizeof(fbuff);\n"
                + "        offset += sizeof(fbuff);\n"
                + "      } else {\n"
                + "        memcpy((char *)fbuff,(char *)(&%table%.array[0]) + offset, rem_sz);\n"
                + "        err = f_write(&FileObject, fbuff, rem_sz, &bytes_written);\n"
                + "        rem_sz = 0;\n"
                + "      }\n"
                + "    }"
                + "    if (err != FR_OK) report_fatfs_error(err,\"%filename%\");\n"
                + "    err = f_close(&FileObject);\n"
                + "    if (err != FR_OK) report_fatfs_error(err,\"%filename%\");\n"
                + "  }\n"
                + "  else if (!(%trig%>0)) ntrig=0;\n";
        return o;
    }

    static AxoObject createLoadTable() {
        AxoObject o = new AxoObject("load", "load table from sdcard");
        o.attributes.add(new AxoAttributeObjRef("table"));
        o.inlets.add(new InletCharPtr32("filename", "file name"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.includes.add("chibios/ext/fatfs/src/ff.h");
        o.sLocalData = "  int ntrig;\n";
        o.sInitCode = "  ntrig = 0;\n";
        o.sKRateCode = "  if ((%trig%>0) && !ntrig) {\n"
                + "    ntrig=1;\n"
                + "    FIL FileObject;\n"
                + "    FRESULT err;\n"
                + "    UINT bytes_read;\n"
                + "    err = f_open(&FileObject, %filename%, FA_READ | FA_OPEN_EXISTING);\n"
                + "    if (err != FR_OK) { report_fatfs_error(err,\"%filename%\"); return;}\n"
                + "    int rem_sz = sizeof(*%table%.array)*%table%.LENGTH;\n"
                + "    int offset = 0;\n"
                + "    while (rem_sz>0) {\n"
                + "      if (rem_sz>sizeof(fbuff)) {\n"
                + "        err = f_read(&FileObject, fbuff, sizeof(fbuff),&bytes_read);\n"
                + "        if (bytes_read == 0) break;\n"
                + "        memcpy((char *)(&%table%.array[0]) + offset,(char *)fbuff,bytes_read);\n"
                + "        rem_sz -= bytes_read;\n"
                + "        offset += bytes_read;\n"
                + "      } else {\n"
                + "        err = f_read(&FileObject, fbuff, rem_sz,&bytes_read);\n"
                + "        memcpy((char *)(&%table%.array[0]) + offset,(char *)fbuff,bytes_read);\n"
                + "        rem_sz = 0;\n"
                + "      }\n"
                + "    }"
                + "    if (err != FR_OK) { report_fatfs_error(err,\"%filename%\"); return;};\n"
                + "    err = f_close(&FileObject);\n"
                + "    if (err != FR_OK) { report_fatfs_error(err,\"%filename%\"); return;};\n"
                + "  }\n"
                + "  else if (!(%trig%>0)) ntrig=0;\n";
        return o;
    }

}
