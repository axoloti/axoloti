package axoloti.chunks;

/**
 *
 * @author jtaelman
 */
public class FourCCs {

    private FourCCs() {
    }

    static public final FourCC PATCH_META = new FourCC('P', 'T', 'C', 'H');
    static public final FourCC PATCH_PRESET = new FourCC('P', 'R', 'S', 'T');
    static public final FourCC PATCH_DISPLAY_META = new FourCC('D', 'I', 'S', 'M');
    static public final FourCC PATCH_PARAMETER = new FourCC('P', 'A', 'R', 'M');
    static public final FourCC PATCH_UI_OBJECT = new FourCC('U', 'I', 'O', 'B');
    static public final FourCC PATCH_INITPRESET = new FourCC('P', 'R', 'I', 'N');
    static public final FourCC PATCH_DISPLAY = new FourCC('D', 'I', 'S', 'P');
    static public final FourCC PATCH_FUNCTION = new FourCC('P', 'F', 'U', 'N');

    static public final FourCC FW_PATCH_NAME = new FourCC('P', 'C', 'H', 'N');
    static public final FourCC FW_GPIO_ADC = new FourCC('G', 'A', 'D', 'C');
    static public final FourCC FW_LCD_FRAMEBUFFER = new FourCC('L', 'C', 'D', 'F');
    static public final FourCC FW_MIDI_INPUT_ROUTING = new FourCC('M', 'I', 'R', '2');
    static public final FourCC FW_MIDI_OUTPUT_ROUTING = new FourCC('M', 'O', 'R', 'O');

    static public final FourCC FW_MIDI_INPUT_BUFFER = new FourCC('M', 'I', 'B', '1');
}
