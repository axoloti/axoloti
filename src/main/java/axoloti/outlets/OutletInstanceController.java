/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axoloti.outlets;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;

/**
 *
 * @author jtaelman
 */
public class OutletInstanceController extends AbstractController<OutletInstance, IOutletInstanceView> {

    public OutletInstanceController(OutletInstance model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
    }

}
