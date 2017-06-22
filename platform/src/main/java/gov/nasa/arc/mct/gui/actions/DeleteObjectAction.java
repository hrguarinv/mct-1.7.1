/*******************************************************************************
 * Mission Control Technologies, Copyright (c) 2009-2012, United States Government
 * as represented by the Administrator of the National Aeronautics and Space 
 * Administration. All rights reserved.
 *
 * The MCT platform is licensed under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations under 
 * the License.
 *
 * MCT includes source code licensed under additional open source licenses. See 
 * the MCT Open Source Licenses file included with this distribution or the About 
 * MCT Licenses dialog available at runtime from the MCT Help menu for additional 
 * information. 
 *******************************************************************************/
package gov.nasa.arc.mct.gui.actions;

import gov.nasa.arc.mct.components.AbstractComponent;
import gov.nasa.arc.mct.gui.ActionContext;
import gov.nasa.arc.mct.gui.ActionContextImpl;
import gov.nasa.arc.mct.gui.ContextAwareAction;
import gov.nasa.arc.mct.gui.MCTMutableTreeNode;
import gov.nasa.arc.mct.gui.OptionBox;
import gov.nasa.arc.mct.gui.View;
import gov.nasa.arc.mct.gui.housing.MCTDirectoryArea;
import gov.nasa.arc.mct.gui.housing.MCTHousing;
import gov.nasa.arc.mct.platform.spi.PlatformAccess;
import gov.nasa.arc.mct.policy.PolicyContext;
import gov.nasa.arc.mct.policy.PolicyInfo;
import gov.nasa.arc.mct.policymgr.PolicyManagerImpl;
import gov.nasa.arc.mct.services.component.ViewInfo;
import gov.nasa.arc.mct.services.component.ViewType;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

public class DeleteObjectAction extends ContextAwareAction {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("gov/nasa/arc/mct/gui/actions/Bundle"); 
    private static final long serialVersionUID = 3047419887471823851L;
    private static String WARNING = bundle.getString("DeleteWarningTitle");
    private static String TEXT = "Delete";
    
    private TreePath[] selectedTreePaths;
    private ActionContextImpl actionContext;
    
    public DeleteObjectAction() {
        super(TEXT);
    }
    
    @Override
    public boolean canHandle(ActionContext context) {
        actionContext = (ActionContextImpl) context;
        MCTHousing activeHousing = actionContext.getTargetHousing();
        if (activeHousing == null)
            return false;


        Collection<View> selection = 
            activeHousing.getSelectionProvider().getSelectedManifestations();
        
        if (selection.isEmpty()) {
            return false;
        }
        
        ViewInfo vi = selection.iterator().next().getInfo();
        
        if (!(vi != null && vi.getViewType() == ViewType.NODE)){
            return false;
        }

        if (!(activeHousing.getDirectoryArea() instanceof MCTDirectoryArea)) {
            return false;
        }
        
        MCTDirectoryArea directory = MCTDirectoryArea.class.cast(activeHousing.getDirectoryArea());
        MCTMutableTreeNode firstSelectedNode = directory.getSelectedDirectoryNode();
        
        if (firstSelectedNode == null)
            return false;
        
        JTree tree = firstSelectedNode.getParentTree();
        selectedTreePaths = tree.getSelectionPaths();
        return selectedTreePaths != null && selectedTreePaths.length > 0;
    }
    
    @Override
    public boolean isEnabled() {
        for (TreePath path : selectedTreePaths) {
            if (!isRemovable(path))
                return false;
        }
        return true;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        for (TreePath path : selectedTreePaths) {
            MCTMutableTreeNode selectedNode = (MCTMutableTreeNode) path.getLastPathComponent();            
            AbstractComponent selectedComponent = ((View) selectedNode.getUserObject()).getManifestedComponent();
            
            // If has children,  
            if (selectedComponent.getComponents().size() > 0) {
                OptionBox.showMessageDialog(actionContext.getWindowManifestation(), 
                        bundle.getString("DeleteErrorHasDescendantsText"), 
                        "ERROR: "+ WARNING+ " " + selectedComponent.getDisplayName(), 
                        OptionBox.ERROR_MESSAGE);
            } else {
                Object[] options = { "Delete Core" , "Cancel" };
                int choice = OptionBox.showOptionDialog(actionContext.getWindowManifestation(), 
                        bundle.getString("DeleteWarningText") + " " + selectedComponent.getDisplayName() +
                        " " + bundle.getString("DeleteWarningText2"),
                        WARNING,
                        OptionBox.YES_NO_OPTION,
                        OptionBox.WARNING_MESSAGE,
                        null,
                        options,
                        null);
                if (choice == 0) {
                    PlatformAccess.getPlatform().getPersistenceProvider().delete(Collections.singleton(selectedComponent));
                    PlatformAccess.getPlatform().getWindowManager().closeWindows(selectedComponent.getComponentId());
                }
            }

        }   
    }
    
    private boolean isRemovable(TreePath path) {
        MCTMutableTreeNode lastPathComponent = (MCTMutableTreeNode) path.getLastPathComponent();
        AbstractComponent selectedComponent = View.class.cast(lastPathComponent.getUserObject()).getManifestedComponent();

        MCTMutableTreeNode parentNode = (MCTMutableTreeNode) lastPathComponent.getParent();
        if (parentNode == null)
            return false;

        AbstractComponent parentComponent = ((View) parentNode.getUserObject()).getManifestedComponent();        
        if (!selectedComponent.canBeDeleted()) {
            return false;
        }
        
        
        PolicyContext context = new PolicyContext();
        context.setProperty(PolicyContext.PropertyName.TARGET_COMPONENT.getName(), parentComponent);
        context.setProperty(PolicyContext.PropertyName.ACTION.getName(), 'w');
        String compositionKey = PolicyInfo.CategoryType.COMPOSITION_POLICY_CATEGORY.getKey();
        return PolicyManagerImpl.getInstance().execute(compositionKey, context).getStatus();
    }
    
}
