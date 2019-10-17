package axoloti.shell;

import axoloti.target.fs.SDFileReference;

/**
 *
 * @author jtaelman
 */
public class CompilePatchResult {

    final byte elf[];
    final SDFileReference filedeps[];
    final String output;

    public CompilePatchResult(byte[] elf, SDFileReference filedeps[], String output) {
        this.elf = elf;
        this.filedeps = filedeps;
        this.output = output;
    }

    public byte[] getElf() {
        return elf;
    }

    public SDFileReference[] getFiledeps() {
        return filedeps;
    }

    public String getOutput() {
        return output;
    }

}
