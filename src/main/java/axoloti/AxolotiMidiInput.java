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

package axoloti;

import axoloti.dialogs.KeyboardFrame;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.swing.SpinnerNumberModel;
import jssc.SerialPortException;


public class AxolotiMidiInput implements Receiver {
    public AxolotiMidiInput() {
        device=null;
    }
    
    public void start(String midiDevice) {
        close();
        
        if(midiDevice.isEmpty()) return;
        
        MidiDevice.Info[] devices;
        devices = MidiSystem.getMidiDeviceInfo();

        for (MidiDevice.Info info: devices) {
            try {
                if(info.getName().equals(midiDevice))
                {
                    MidiDevice dev = MidiSystem.getMidiDevice(info);
                    dev.open();
                    if(dev.isOpen()) {
                        dev.getTransmitter().setReceiver(this);
                    }
                }
            }
            catch (MidiUnavailableException e)
            {
            }
        }
    }
    public void send(MidiMessage msg,long timesStamp) {
        SerialConnection connection = MainFrame.mainframe.getQcmdprocessor().serialconnection;
        if ((connection != null) && connection.isConnected()) {
            try {
                byte[] b = msg.getMessage();
                byte b0 = b[0];
                byte b1 = (msg.getLength()>1 ? b[1] : 0); 
                byte b2 = (msg.getLength()>2 ? b[2] : 0); 
                if (b0 != (byte)254) { 
                    connection.SendMidi(b0,b1,b2);
                }
            } catch (SerialPortException ex) {
                Logger.getLogger(AxolotiMidiInput.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void close() {
        if (device!=null) {
            device.close();
        }
    }
    private MidiDevice device;
}
