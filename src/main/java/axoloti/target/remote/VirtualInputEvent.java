package axoloti.target.remote;

/**
 *
 * @author jtaelman
 */
public class VirtualInputEvent {
    public static final byte BTN_UP = 1;
    public static final byte BTN_DOWN = 2;
    public static final byte BTN_ENCODER = 3;
    public static final byte BTN_F = 4;
    public static final byte BTN_S = 5;
    public static final byte BTN_X = 6;
    public static final byte BTN_E = 7;
    public static final byte BTN_1 = 8;
    public static final byte BTN_2 = 9;
    public static final byte BTN_3 = 10;
    public static final byte BTN_4 = 11;
    public static final byte BTN_5 = 12;
    public static final byte BTN_6 = 13;
    public static final byte BTN_7 = 14;
    public static final byte BTN_8 = 15;
    public static final byte BTN_9 = 16;
    public static final byte BTN_10 = 17;
    public static final byte BTN_11 = 18;
    public static final byte BTN_12 = 19;
    public static final byte BTN_13 = 20;
    public static final byte BTN_14 = 21;
    public static final byte BTN_15 = 22;
    public static final byte BTN_16 = 23;

    public static final byte MODIFIER_SHIFT = 1;

    public static final byte QUADRANT_MAIN = 0;
    public static final byte QUADRANT_TOPLEFT = 1;
    public static final byte QUADRANT_TOPRIGHT = 2;
    public static final byte QUADRANT_BOTTOMLEFT = 3;
    public static final byte QUADRANT_BOTTOMRIGHT = 4;
    public static final byte QUADRANT_BOTTOM = 5;

    final byte quadrant;
    final byte modifiers;
    final byte button;
    final byte value;

    public VirtualInputEvent(byte button, byte modifiers, byte value, byte quadrant) {
        this.button = button;
        this.modifiers = modifiers;
        this.value = value;
        this.quadrant = quadrant;
    }

    public byte getQuadrant() {
        return quadrant;
    }

    public byte getModifiers() {
        return modifiers;
    }

    public byte getButton() {
        return button;
    }

    public byte getValue() {
        return value;
    }
}
