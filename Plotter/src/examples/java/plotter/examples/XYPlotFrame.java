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
package plotter.examples;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SpringLayout;

import plotter.xy.DefaultXYLayoutGenerator;
import plotter.xy.LinearXYAxis;
import plotter.xy.SlopeLine;
import plotter.xy.SlopeLineDisplay;
import plotter.xy.XYAxis;
import plotter.xy.XYDimension;
import plotter.xy.XYGrid;
import plotter.xy.XYLocationDisplay;
import plotter.xy.XYPlot;
import plotter.xy.XYPlotContents;

public class XYPlotFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private XYPlot plot;

	private XYAxis xAxis;

	private XYAxis yAxis;

	private XYGrid grid;

	private XYPlotContents contents;

	private XYLocationDisplay locationDisplay;

	private SlopeLineDisplay slopeLineDisplay;


	public void setup() {
		Container contentPane = getContentPane();

		plot = new XYPlot();
		xAxis = createXAxis();
		yAxis = createYAxis();
		xAxis.setPreferredSize(new Dimension(1, 40));
		yAxis.setPreferredSize(new Dimension(40, 1));
		xAxis.setForeground(Color.white);
		yAxis.setForeground(Color.white);
		xAxis.setTextMargin(10);
		yAxis.setTextMargin(10);
		plot.add(xAxis);
		plot.add(yAxis);
		plot.setXAxis(xAxis);
		plot.setYAxis(yAxis);
		plot.setBackground(Color.darkGray);
		contents = new XYPlotContents();
		contents.setBackground(Color.black);
		plot.setBackground(Color.darkGray);
		grid = new XYGrid(xAxis, yAxis);
		grid.setForeground(Color.lightGray);
		contents.add(grid);
		plot.add(contents);
		plot.setPreferredSize(new Dimension(150, 100));

		contentPane.setBackground(Color.darkGray);

		locationDisplay = new XYLocationDisplay();
		// This is a hack to set the preferred height to the normal height so the component doesn't collapse to height 0 when the text is empty.
		// Note that mimimumSize does not work for some reason.
		locationDisplay.setText("Ag");
		Dimension size = locationDisplay.getPreferredSize();
		size.width = 100;
		locationDisplay.setText("");
		locationDisplay.setPreferredSize(size);
		// End hack
		locationDisplay.setForeground(Color.white);
		locationDisplay.setFont(new Font("Arial", 0, 12));
		locationDisplay.setFormat(new MessageFormat("<html><b>X:</b> {0} &nbsp; <b>Y:</b> {1}</html>"));
		locationDisplay.attach(plot);
		plot.add(locationDisplay);

		SlopeLine slopeLine = new SlopeLine();
		slopeLine.setForeground(Color.white);
		slopeLine.attach(plot);

		slopeLineDisplay = new SlopeLineDisplay();
		slopeLine.addListenerForPlot(plot, slopeLineDisplay);
		slopeLineDisplay.setFont(new Font("Arial", 0, 12));
		slopeLineDisplay.setForeground(Color.white);
		slopeLineDisplay.setFormat(new MessageFormat("<html><b>&Delta;x:</b> {0}  <b>&Delta;y:</b> {1}</html>"));
		plot.add(slopeLineDisplay);

		new DefaultXYLayoutGenerator().generateLayout(plot);

		SpringLayout layout2 = (SpringLayout) plot.getLayout();
		layout2.putConstraint(SpringLayout.NORTH, locationDisplay, 0, SpringLayout.NORTH, plot);
		layout2.putConstraint(SpringLayout.WEST, locationDisplay, 0, SpringLayout.WEST, contents);
		layout2.putConstraint(SpringLayout.EAST, locationDisplay, 0, SpringLayout.HORIZONTAL_CENTER, contents);
		layout2.putConstraint(SpringLayout.NORTH, contents, 0, SpringLayout.SOUTH, locationDisplay);
		layout2.putConstraint(SpringLayout.NORTH, slopeLineDisplay, 0, SpringLayout.NORTH, plot);
		layout2.putConstraint(SpringLayout.WEST, slopeLineDisplay, 0, SpringLayout.HORIZONTAL_CENTER, contents);
		layout2.putConstraint(SpringLayout.EAST, slopeLineDisplay, 0, SpringLayout.EAST, contents);
		yAxis.setEndMargin((int) locationDisplay.getPreferredSize().getHeight());

		SpringLayout layout = new SpringLayout();
		contentPane.setLayout(layout);
		layout.putConstraint(SpringLayout.NORTH, plot, 0, SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.SOUTH, contentPane, 0, SpringLayout.SOUTH, plot);
		layout.putConstraint(SpringLayout.EAST, contentPane, 0, SpringLayout.EAST, plot);
		contentPane.add(plot);
	}


	protected XYAxis createYAxis() {
		return new LinearXYAxis(XYDimension.Y);
	}


	protected XYAxis createXAxis() {
		return new LinearXYAxis(XYDimension.X);
	}


	public void addPlotLine(JComponent plotLine) {
		contents.add(plotLine);
		contents.setComponentZOrder(grid, contents.getComponentCount() - 1);
	}


	public XYAxis getXAxis() {
		return xAxis;
	}


	public XYAxis getYAxis() {
		return yAxis;
	}


	public XYPlotContents getContents() {
		return contents;
	}


	public XYLocationDisplay getLocationDisplay() {
		return locationDisplay;
	}


	public SlopeLineDisplay getSlopeLineDisplay() {
		return slopeLineDisplay;
	}


	public XYPlot getPlot() {
		return plot;
	}
}
