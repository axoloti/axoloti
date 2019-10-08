package axoloti.shell;

import axoloti.target.fs.SDFileReference;

/**
 *
 * @author jtaelman
 */
public class CompilePatchResult {

    final byte elf[];
    final SDFileReference filedeps[];
    final String stdout;
    final String stderror;

    public CompilePatchResult(byte[] elf, SDFileReference filedeps[], String stdout, String stderror) {
        this.elf = elf;
        this.filedeps = filedeps;
        this.stdout = stdout;
        this.stderror = stderror;
    }

    public byte[] getElf() {
        return elf;
    }

    public SDFileReference[] getFiledeps() {
        return filedeps;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderror() {
        return stderror;
    }

}
