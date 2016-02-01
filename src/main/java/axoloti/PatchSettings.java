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
package axoloti;

import axoloti.dialogs.PatchSettingsFrame;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public class PatchSettings {

    @Element
    public SubPatchMode subpatchmode = SubPatchMode.no;
    @Element(required = false)
    private Integer MidiChannel;
    @Element(required = false)
    private Boolean HasMidiChannelSelector;
    @Element(required = false)
    private Integer NPresets;
    @Element(required = false)
    private Integer NPresetEntries;
    @Element(required = false)
    private Integer NModulationSources;
    @Element(required = false)
    private Integer NModulationTargetsPerSource;
    @Element(required = false)
    private String Author;
    @Element(required = false)
    private String License;
    @Element(required = false)
    private String Attributions;
    @Element(required = false)
    private Boolean Saturate;
    PatchSettingsFrame editor;
    
    public int GetMidiChannel() {
        if (MidiChannel != null) {
            SetMidiChannel(MidiChannel);
            return MidiChannel;
        } else {
            return 1;
        }
    }

    public void SetMidiChannel(int i) {
        if (i > 16) {
            i = 16;
        }
        if (i < 1) {
            i = 1;
        }
        MidiChannel = i;
    }

    public boolean GetMidiSelector() {
        if (HasMidiChannelSelector == null) {
            return false;
        }
        return HasMidiChannelSelector;
    }

    public void SetMidiSelector(boolean b) {
        if (b) {
            HasMidiChannelSelector = b;
        } else {
            HasMidiChannelSelector = null;
        }
    }

    public int GetNPresets() {
        if (NPresets != null) {
            return NPresets;
        } else {
            return 8;
        }
    }

    public void SetNPresets(int i) {
        NPresets = i;
    }

    public void SetNPresetEntries(int i) {
        NPresetEntries = i;
    }

    public int GetNPresetEntries() {
        if (NPresetEntries != null) {
            return NPresetEntries;
        } else {
            return 32;
        }
    }

    public int GetNModulationSources() {
        if (NModulationSources != null) {
            return NModulationSources;
        } else {
            return 8;
        }
    }

    public void SetNModulationSources(int i) {
        NModulationSources = i;
    }

    public int GetNModulationTargetsPerSource() {
        if (NModulationTargetsPerSource != null) {
            return NModulationTargetsPerSource;
        } else {
            return 8;
        }
    }

    public void SetNModulationTargetsPerSource(int i) {
        NModulationTargetsPerSource = i;
    }

    void showEditor(Patch patch) {
        if (editor == null) {
            editor = new PatchSettingsFrame(this, patch);
        }
        editor.setVisible(true);
        editor.setState(java.awt.Frame.NORMAL);
        editor.toFront();
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String Author) {
        this.Author = Author;
    }

    public String getLicense() {
        return License;
    }

    public void setLicense(String License) {
        this.License = License;
    }

    public String getAttributions() {
        return Attributions;
    }

    public void setAttributions(String Attributions) {
        this.Attributions = Attributions;
    }

    public Boolean getSaturate() {
        if (Saturate == null) {
            return true;
        } else {
            return Saturate;
        }
    }

    public void setSaturate(Boolean Saturate) {
        this.Saturate = Saturate;
    }
}
