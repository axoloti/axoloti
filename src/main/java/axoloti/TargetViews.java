package axoloti;

import axoloti.dialogs.AxolotiRemoteControl;
import axoloti.dialogs.FileManagerFrame;
import axoloti.dialogs.KeyboardFrame;
import axoloti.dialogs.Memory;
import axoloti.dialogs.MidiMonitor;
import axoloti.dialogs.MidiRouting;
import axoloti.mvc.IView;
import java.beans.PropertyChangeEvent;
import javax.swing.JFrame;

/**
 *
 * @author jtaelman
 */
public class TargetViews implements IView<TargetController> {

    private static TargetViews targetViews;

    public static TargetViews getTargetViews() {
        if (targetViews == null) {
            targetViews = new TargetViews(TargetController.getTargetController());
        }
        return targetViews;
    }

    final TargetController controller;

    KeyboardFrame keyboard;
    FileManagerFrame fileManager;
    AxolotiRemoteControl remote;
    MidiRouting midiRouting;
    MidiMonitor midiMonitor;
    Memory memory;


    public TargetViews(TargetController controller) {
        this.controller = controller;
    }

    @Override
    public TargetController getController() {
        return controller;
    }

    private static void popWindow(JFrame frame) {
        frame.setVisible(true);
        frame.setState(java.awt.Frame.NORMAL);
        frame.toFront();
    }

    public void showRemote() {
        if (remote == null) {
            remote = new AxolotiRemoteControl(getController());
            getController().addView(remote);
            remote.setTitle("Remote");
            remote.setVisible(true);
        } else {
            popWindow(remote);
        }
    }

    public void showMidiRouting() {
        if (midiRouting == null) {
            midiRouting = new MidiRouting(getController());
            getController().addView(midiRouting);
            midiRouting.setTitle("MIDI Routing");
            midiRouting.setVisible(true);
        } else {
            popWindow(midiRouting);
        }
    }

    public void showKeyboard() {
        if (keyboard == null) {
            keyboard = new KeyboardFrame(getController());
            getController().addView(keyboard);
            //piano.setAlwaysOnTop(true);
            keyboard.setTitle("Keyboard");
            keyboard.setVisible(true);
        } else {
            popWindow(keyboard);
        }
    }

    public void showFilemanager() {
        if (fileManager == null) {
            fileManager = new FileManagerFrame(getController());
            getController().addView(fileManager);
            fileManager.setTitle("File Manager");
            fileManager.setVisible(true);
        } else {
            popWindow(fileManager);
        }
    }

    public void showMemoryViewer() {
        if (memory == null) {
            memory = new axoloti.dialogs.Memory(getController());
            getController().addView(memory);
            memory.setVisible(true);
        } else {
            popWindow(memory);
        }
    }

    public void showMidiMonitor() {
        if (midiMonitor == null) {
            midiMonitor = new axoloti.dialogs.MidiMonitor(getController());
            getController().addView(midiMonitor);
            midiMonitor.setVisible(true);
        } else {
            popWindow(midiMonitor);
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void dispose() {
    }

    public AxolotiRemoteControl getRemote() {
        return remote;
    }

}
