package axoloti.swingui.target;

import axoloti.mvc.IView;
import axoloti.target.TargetModel;
import java.beans.PropertyChangeEvent;
import javax.swing.JFrame;

/**
 *
 * @author jtaelman
 */
public class TargetViews implements IView<TargetModel> {

    private static TargetViews targetViews;

    public static TargetViews getTargetViews() {
        if (targetViews == null) {
            targetViews = new TargetViews(TargetModel.getTargetModel());
        }
        return targetViews;
    }

    final TargetModel targetModel;

    private KeyboardFrame keyboard;
    private FileManagerFrame fileManager;
    private AxolotiRemoteControl remote;
    private MidiRouting midiRouting;
    private MidiMonitor midiMonitor;
    private Memory memory;

    public TargetViews(TargetModel targetModel) {
        this.targetModel = targetModel;
    }

    @Override
    public TargetModel getDModel() {
        return targetModel;
    }

    private static void popWindow(JFrame frame) {
        frame.setVisible(true);
        frame.toFront();
    }

    public void showRemote() {
        if (remote == null) {
            remote = new AxolotiRemoteControl(targetModel);
            targetModel.getController().addView(remote);
            remote.setTitle("Remote");
            remote.setVisible(true);
        } else {
            popWindow(remote);
        }
    }

    public void showMidiRouting() {
        if (midiRouting == null) {
            midiRouting = new MidiRouting(targetModel);
            targetModel.getController().addView(midiRouting);
            midiRouting.setTitle("MIDI Routing");
            midiRouting.setVisible(true);
        } else {
            popWindow(midiRouting);
        }
    }

    public void showKeyboard() {
        if (keyboard == null) {
            keyboard = new KeyboardFrame(targetModel);
            targetModel.getController().addView(keyboard);
            //piano.setAlwaysOnTop(true);
            keyboard.setTitle("Keyboard");
            keyboard.setVisible(true);
        } else {
            popWindow(keyboard);
        }
    }

    public void showFilemanager() {
        if (fileManager == null) {
            fileManager = new FileManagerFrame(targetModel);
            targetModel.getController().addView(fileManager);
            fileManager.setTitle("File Manager");
            fileManager.setVisible(true);
        } else {
            popWindow(fileManager);
        }
    }

    public void showMemoryViewer() {
        if (memory == null) {
            memory = new axoloti.swingui.target.Memory(targetModel);
            targetModel.getController().addView(memory);
            memory.setVisible(true);
        } else {
            popWindow(memory);
        }
    }

    public void showMidiMonitor() {
        if (midiMonitor == null) {
            midiMonitor = new axoloti.swingui.target.MidiMonitor(targetModel);
            targetModel.getController().addView(midiMonitor);
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
