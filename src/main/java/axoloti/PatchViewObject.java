
package axoloti;

import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public class PatchViewObject extends PatchAbstractView {

    public PatchViewObject(PatchController controller) {
        super(controller);
    }
    
    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }
    
}
