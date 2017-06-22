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
package gov.nasa.arc.mct.fastplot.utils;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFormattedTextField;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;

/**
 * A JSpinner that can be used as a menu item. (Includes key listeners to allow tabbing in, etc)
 */
public class MenuItemSpinner extends JSpinner {
	private static final long serialVersionUID = 1571587288826156261L;

	public MenuItemSpinner(SpinnerModel model, final JMenu menu) {
		super(model);
		final JSpinner spinner = this;
		spinner.setBorder(new EmptyBorder(2,2,2,2));
		
		 final JFormattedTextField myTextField = ((NumberEditor) spinner
			        .getEditor()).getTextField();
		
		spinner.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if ( ! (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) && 
						(e.getKeyCode() == KeyEvent.VK_UNDEFINED) &&
						// Apparently, backspace has a key char (although it should not)
						(e.getKeyChar() == '0' ||
						 e.getKeyChar() == '1' ||
						 e.getKeyChar() == '2' ||
						 e.getKeyChar() == '3' ||
						 e.getKeyChar() == '4' ||
						 e.getKeyChar() == '5' ||
						 e.getKeyChar() == '6' ||
						 e.getKeyChar() == '7' ||
						 e.getKeyChar() == '8' ||
						 e.getKeyChar() == '9'
								) &&
						Integer.valueOf(myTextField.getValue() + String.valueOf(e.getKeyChar())).compareTo((Integer) 
								((SpinnerNumberModel) spinner.getModel()).getMinimum()) > 0 && 
						Integer.valueOf(myTextField.getValue() + String.valueOf(e.getKeyChar())).compareTo((Integer) 
								((SpinnerNumberModel) spinner.getModel()).getMaximum()) < 0 ) {
					myTextField.setText(myTextField.getValue() + String.valueOf(e.getKeyChar()));
					
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE ) {
					((NumberEditor) spinner.getEditor()).getTextField().setText("");
				} 
				myTextField.grabFocus();
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
			
		});  
			 
		 myTextField.addFocusListener(new FocusListener()
			    {
			 @Override
			 public void focusGained(FocusEvent e) {
				 SwingUtilities.invokeLater(new Runnable() {
			            public void run() {
			            	myTextField.selectAll();
			            }
			     });
			 }

			@Override
			public void focusLost(java.awt.event.FocusEvent e) {
			}
		});
		 
		 final NumberEditor numberEditor = (NumberEditor) spinner.getEditor();
		 
		 numberEditor.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT && 
						numberEditor.getTextField().getCaretPosition() == 0) {
					menu.setSelected(true);
				} 
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		 
		 myTextField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT && 
						numberEditor.getTextField().getCaretPosition() == 0) {
					menu.setSelected(true);
					menu.grabFocus();
					((JPopupMenu) spinner.getParent()).setSelected(menu);
				} 
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
			
		});
		 
		menu.addMenuKeyListener(new MenuKeyListener() {

			@Override
			public void menuKeyTyped(MenuKeyEvent e) {
			}

			@Override
			public void menuKeyPressed(MenuKeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_RIGHT ) {
					spinner.setVisible(true);
					spinner.requestFocus();
					((NumberEditor) spinner.getEditor()).grabFocus();
				} 
			}

			@Override
			public void menuKeyReleased(MenuKeyEvent e) {
			}
			
		});
		
	}
}
