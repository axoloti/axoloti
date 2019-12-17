package axoloti.job;

import java.util.function.Consumer;

/**
 *
 * @author jtaelman
 */
public interface IJob extends Consumer<IJobContext> {
}
