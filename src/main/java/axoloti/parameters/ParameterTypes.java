/**
 * Copyright (C) 2013 - 2016 Johannes Taelman
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
package axoloti.parameters;

/**
 *
 * @author jtaelman
 */
public class ParameterTypes {
    
    final static Parameter types[] = {
      new ParameterFrac32SMap(),
      new Parameter4LevelX16(),
      new ParameterBin1(),
      new ParameterBin12(),
      new ParameterBin16(),
      new ParameterBin1Momentary(),
      new ParameterBin32(),
      new ParameterFrac32SMapKDTimeExp(),
      new ParameterFrac32SMapKLineTimeExp(),
      new ParameterFrac32SMapKLineTimeExp2(),
      new ParameterFrac32SMapKPitch(),
      new ParameterFrac32SMapLFOPitch(),
      new ParameterFrac32SMapPitch(),
      new ParameterFrac32SMapRatio(),
      new ParameterFrac32SMapVSlider(),
      new ParameterFrac32UMap(),
      new ParameterFrac32UMapFilterQ(),
      new ParameterFrac32UMapFreq(),
      new ParameterFrac32UMapGain(),
      new ParameterFrac32UMapGain16(),
      new ParameterFrac32UMapGainSquare(),
      new ParameterFrac32UMapKDecayTime(),
      new ParameterFrac32UMapKDecayTimeReverse(),
      new ParameterFrac32UMapKLineTimeReverse(),
      new ParameterFrac32UMapRatio(),
      new ParameterFrac32UMapVSlider(),
      new ParameterInt32Box(),
      new ParameterInt32BoxSmall(),
      new ParameterInt32HRadio(),
      new ParameterInt32VRadio()
    };
    
    public static Parameter[] getTypes(){
        return types;
    }
}
