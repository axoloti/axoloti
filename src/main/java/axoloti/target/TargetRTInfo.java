package axoloti.target;

/**
 *
 * @author jtaelman
 */
public class TargetRTInfo {
    public float inLevel1;
    public float inLevel2;
    public float outLevel1;
    public float outLevel2;

    public float dsp;
    public int sram1_free;
    public int sram3_free;
    public int ccmsram_free;
    public int sdram_free;

    public int underruns;

    public float vdd;
    public float v50;
    public boolean voltageAlert;
}
