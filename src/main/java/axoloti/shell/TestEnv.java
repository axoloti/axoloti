package axoloti.shell;

import axoloti.job.GlobalJobProcessor;

/**
 *
 * @author jtaelman
 */
public class TestEnv {

    public static void test_env() {
        String makeCmd = ShellTask.getMake();
        String cmd[] = new String[]{
            makeCmd,
            "-I", ShellTask.getEnvDir(),
            "-f", ShellTask.getEnvDir() + "/test-env.mk"
        };
        String env[] = ShellTask.getEnvironment();
        ShellTask shellTask = new ShellTask(
                ShellTask.getBuildDir(),
                cmd,
                env);
        GlobalJobProcessor.getJobProcessor().exec(shellTask.getJob());
    }
}
