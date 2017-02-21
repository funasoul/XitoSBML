/*******************************************************************************
 * Copyright 2015 Kaito Ii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ij.gui;
import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.plugin.filter.Analyzer;
import ij.plugin.frame.Recorder;
import ij.process.ByteProcessor;
import ij.process.FloatPolygon;
import ij.process.ImageProcessor;
import ij.util.Java2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

// TODO: Auto-generated Javadoc
/** This class represents a collection of points. */
public class PointRoi extends PolygonRoi {
	
	/** The Constant sizes. */
	public static final String[] sizes = {"Tiny", "Small", "Medium", "Large", "Extra Large"};
	
	/** The Constant types. */
	public static final String[] types = {"Hybrid", "Crosshair", "Dot", "Circle"};
	
	/** The Constant TYPE_KEY. */
	private static final String TYPE_KEY = "point.type";
	
	/** The Constant SIZE_KEY. */
	private static final String SIZE_KEY = "point.size";
	
	/** The Constant CROSS_COLOR_KEY. */
	private static final String CROSS_COLOR_KEY = "point.cross.color";
	
	/** The Constant EXTRA_LARGE. */
	private static final int TINY=1, SMALL=3, MEDIUM=5, LARGE=7, EXTRA_LARGE=11;
	
	/** The Constant CIRCLE. */
	private static final int HYBRID=0, CROSSHAIR=1, DOT=2, CIRCLE=3;
	
	/** The Constant twoPixelsWide. */
	private static final BasicStroke twoPixelsWide = new BasicStroke(2);
	
	/** The Constant threePixelsWide. */
	private static final BasicStroke threePixelsWide = new BasicStroke(3);
	
	/** The default type. */
	private static int defaultType = HYBRID;
	
	/** The default size. */
	private static int defaultSize = SMALL;
	
	/** The font. */
	private static Font font;
	
	/** The default cross color. */
	private static Color defaultCrossColor = Color.white;
	
	/** The font size. */
	private static int fontSize = 9;
	
	/** The save mag. */
	private double saveMag;
	
	/** The show labels. */
	private boolean showLabels;
	
	/** The type. */
	private int type = HYBRID;
	
	/** The size. */
	private int size = SMALL;
	
	static {
		setDefaultType((int)Prefs.get(TYPE_KEY, HYBRID));
		setDefaultSize((int)Prefs.get(SIZE_KEY, 1));
	}
	
	/**
	 *  Creates a new PointRoi using the specified int arrays of offscreen coordinates.
	 *
	 * @param ox the ox
	 * @param oy the oy
	 * @param points the points
	 */
	public PointRoi(int[] ox, int[] oy, int points) {
		super(itof(ox), itof(oy), points, POINT);
		width+=1; height+=1;
	}

	/**
	 *  Creates a new PointRoi using the specified float arrays of offscreen coordinates.
	 *
	 * @param ox the ox
	 * @param oy the oy
	 * @param points the points
	 */
	public PointRoi(float[] ox, float[] oy, int points) {
		super(ox, oy, points, POINT);
		width+=1; height+=1;
	}

	/**
	 *  Creates a new PointRoi using the specified float arrays of offscreen coordinates.
	 *
	 * @param ox the ox
	 * @param oy the oy
	 */
	public PointRoi(float[] ox, float[] oy) {
		this(ox, oy, ox.length);
	}

	/**
	 *  Creates a new PointRoi from a FloatPolygon.
	 *
	 * @param poly the poly
	 */
	public PointRoi(FloatPolygon poly) {
		this(poly.xpoints, poly.ypoints, poly.npoints);
	}

	/**
	 *  Creates a new PointRoi from a Polygon.
	 *
	 * @param poly the poly
	 */
	public PointRoi(Polygon poly) {
		this(itof(poly.xpoints), itof(poly.ypoints), poly.npoints);
	}

	/**
	 *  Creates a new PointRoi using the specified offscreen int coordinates.
	 *
	 * @param ox the ox
	 * @param oy the oy
	 */
	public PointRoi(int ox, int oy) {
		super(makeXArray(ox, null), makeYArray(oy, null), 1, POINT);
		width=1; height=1;
	}

	/**
	 *  Creates a new PointRoi using the specified offscreen double coordinates.
	 *
	 * @param ox the ox
	 * @param oy the oy
	 */
	public PointRoi(double ox, double oy) {
		super(makeXArray(ox, null), makeYArray(oy, null), 1, POINT);
		width=1; height=1;
	}

	/**
	 *  Creates a new PointRoi using the specified screen coordinates.
	 *
	 * @param sx the sx
	 * @param sy the sy
	 * @param imp the imp
	 */
	public PointRoi(int sx, int sy, ImagePlus imp) {
		super(makeXArray(sx, imp), makeYArray(sy, imp), 1, POINT);
		setImage(imp);
		width=1; height=1;
		type = defaultType;
		size = defaultSize;
		showLabels = !Prefs.noPointLabels;
		if (imp!=null)
			imp.draw(x-10, y-10, 20, 20);
		if (Recorder.record && !Recorder.scriptMode()) 
			Recorder.record("makePoint", x, y);
	}
	
	/**
	 * Itof.
	 *
	 * @param arr the arr
	 * @return the float[]
	 */
	static float[] itof(int[] arr) {
		if (arr==null)
			return null;
		int n = arr.length;
		float[] temp = new float[n];
		for (int i=0; i<n; i++)
			temp[i] = arr[i];
		return temp;
	}

	/**
	 * Make X array.
	 *
	 * @param value the value
	 * @param imp the imp
	 * @return the float[]
	 */
	static float[] makeXArray(double value, ImagePlus imp) {
		float[] array = new float[1];
		array[0] = (float)(imp!=null?imp.getCanvas().offScreenXD((int)value):value);
		return array;
	}
				
	/**
	 * Make Y array.
	 *
	 * @param value the value
	 * @param imp the imp
	 * @return the float[]
	 */
	static float[] makeYArray(double value, ImagePlus imp) {
		float[] array = new float[1];
		array[0] = (float)(imp!=null?imp.getCanvas().offScreenYD((int)value):value);
		return array;
	}
				
	/**
	 * Handle mouse move.
	 *
	 * @param ox the ox
	 * @param oy the oy
	 */
	void handleMouseMove(int ox, int oy) {
		//IJ.log("handleMouseMove");
	}
	
	/* (non-Javadoc)
	 * @see ij.gui.PolygonRoi#handleMouseUp(int, int)
	 */
	protected void handleMouseUp(int sx, int sy) {
		super.handleMouseUp(sx, sy);
		modifyRoi(); //adds this point to previous points if shift key down
	}
	
	/**
	 *  Draws the points on the image.
	 *
	 * @param g the g
	 */
	public void draw(Graphics g) {
		updatePolygon();
		if (ic!=null) mag = ic.getMagnification();
		if (showLabels && nPoints>1) {
			fontSize = 9;
			if (mag>1.0)
				fontSize = (int)(((mag-1.0)/3.0+1.0)*9.0);
			if (fontSize>18) fontSize = 18;
			if (font==null || mag!=saveMag)
				font = new Font("SansSerif", Font.PLAIN, fontSize);
			g.setFont(font);
			if (fontSize>9)
				Java2.setAntialiasedText(g, true);
			saveMag = mag;
		}
		for (int i=0; i<nPoints; i++)
			drawPoint(g, xp2[i], yp2[i], i+1);
		if (updateFullWindow)
			{updateFullWindow = false; imp.draw();}
	}

	/**
	 * Draw point.
	 *
	 * @param g the g
	 * @param x the x
	 * @param y the y
	 * @param n the n
	 */
	void drawPoint(Graphics g, int x, int y, int n) {
		int size2=size/2;
		boolean colorSet = false;
		Graphics2D g2d = (Graphics2D)g;
		Color color = strokeColor!=null?strokeColor:ROIColor;
		if (!overlay && isActiveOverlayRoi()) {
			if (color==Color.cyan)
				color = Color.magenta;
			else
				color = Color.cyan;
		}
		if (type==HYBRID || type==CROSSHAIR) {
			if (type==HYBRID)
				g.setColor(Color.white);
			else {
				g.setColor(color);
				colorSet = true;
			}
			if (size>LARGE)
				g2d.setStroke(threePixelsWide);
			g.drawLine(x-(size+2), y, x+size+2, y);
			g.drawLine(x, y-(size+2), x, y+size+2);
		}
		if (type!=CROSSHAIR && size>SMALL)
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (type==HYBRID || type==DOT) { 
			if (!colorSet) {
				g.setColor(color);
				colorSet = true;
			}
			if (size>LARGE)
				g2d.setStroke(onePixelWide);
			if (size>LARGE && type==DOT)
				g.fillOval(x-size2, y-size2, size, size);
			else if (size>LARGE && type==HYBRID)
				g.fillRect(x-(size2-2), y-(size2-2), size-4, size-4);
			else if (size>SMALL && type==HYBRID)
				g.fillRect(x-(size2-1), y-(size2-1), size-2, size-2);
			else
				g.fillRect(x-size2, y-size2, size, size);
		}
		if (showLabels && nPoints>1) {
			if (!colorSet)
				g.setColor(color);
			g.drawString(""+n, x+4, y+fontSize+2);
		}
		if ((size>TINY||type==DOT) && (type==HYBRID||type==DOT)) {
			g.setColor(Color.black);
			if (size>LARGE && type==HYBRID)
				g.drawOval(x-(size2-1), y-(size2-1), size-3, size-3);
			else if (size>SMALL && type==HYBRID)
				g.drawOval(x-size2, y-size2, size-1, size-1);
			else
				g.drawOval(x-(size2+1), y-(size2+1), size+1, size+1);
		}
		if (type==CIRCLE) {
			int csize = size + 2;
			int csize2 = csize/2;
			g.setColor(color);
			if (size>LARGE)
				g2d.setStroke(twoPixelsWide);
			g.drawOval(x-(csize2+1), y-(csize2+1), csize+1, csize+1);
		}
	}
	
	/* (non-Javadoc)
	 * @see ij.gui.PolygonRoi#drawPixels(ij.process.ImageProcessor)
	 */
	public void drawPixels(ImageProcessor ip) {
		ip.setLineWidth(Analyzer.markWidth);
		for (int i=0; i<nPoints; i++) {
			ip.moveTo(x+(int)xpf[i], y+(int)ypf[i]);
			ip.lineTo(x+(int)xpf[i], y+(int)ypf[i]);
		}
	}
	
	/**
	 *  Returns a copy of this PointRoi with a point at (x,y) added.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the point roi
	 */
	public PointRoi addPoint(double x, double y) {
		FloatPolygon poly = getFloatPolygon();
		poly.addPoint(x, y);
		PointRoi p = new PointRoi(poly.xpoints, poly.ypoints, poly.npoints);
		p.setShowLabels(showLabels);
		IJ.showStatus("count="+poly.npoints);
		p.setStrokeColor(getStrokeColor());
		p.setFillColor(getFillColor());
		p.setPointType(getPointType());
		p.setSize(getSize());
		return p;
	}
	
	/**
	 * Adds the point.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the point roi
	 */
	public PointRoi addPoint(int x, int y) {
		return addPoint((double)x, (double)y);
	}
	
	/**
	 *  Subtract the points that intersect the specified ROI and return 
	 * 		the result. Returns null if there are no resulting points.
	 *
	 * @param roi the roi
	 * @return the point roi
	 */
	public PointRoi subtractPoints(Roi roi) {
		Polygon points = getPolygon();
		Polygon poly = roi.getPolygon();
		Polygon points2 = new Polygon();
		for (int i=0; i<points.npoints; i++) {
			if (!poly.contains(points.xpoints[i], points.ypoints[i]))
				points2.addPoint(points.xpoints[i], points.ypoints[i]);
		}
		if (points2.npoints==0)
			return null;
		else
			return new PointRoi(points2.xpoints, points2.ypoints, points2.npoints);
	}

	/* (non-Javadoc)
	 * @see ij.gui.PolygonRoi#getMask()
	 */
	public ImageProcessor getMask() {
		if (cachedMask!=null && cachedMask.getPixels()!=null)
			return cachedMask;
		ImageProcessor mask = new ByteProcessor(width, height);
		for (int i=0; i<nPoints; i++) {
			mask.putPixel((int)xpf[i], (int)ypf[i], 255);
		}
		cachedMask = mask;
		return mask;
	}

	/**
	 *  Returns true if (x,y) is one of the points in this collection.
	 *
	 * @param x the x
	 * @param y the y
	 * @return true, if successful
	 */
	public boolean contains(int x, int y) {
		for (int i=0; i<nPoints; i++) {
			if (x==this.x+xpf[i] && y==this.y+ypf[i]) return true;
		}
		return false;
	}
	
	/**
	 * Sets the show labels.
	 *
	 * @param showLabels the new show labels
	 */
	public void setShowLabels(boolean showLabels) {
		this.showLabels = showLabels;
	}

	/**
	 * Gets the show labels.
	 *
	 * @return the show labels
	 */
	public boolean getShowLabels() {
		return showLabels;
	}

	/**
	 * Sets the default type.
	 *
	 * @param type the new default type
	 */
	public static void setDefaultType(int type) {
		if (type>=0 && type<types.length) {
			defaultType = type;
			PointRoi instance = getPointRoiInstance();
			if (instance!=null)
				instance.setPointType(defaultType);
			Prefs.set(TYPE_KEY, type);
		}
	}
	
	/**
	 * Gets the default type.
	 *
	 * @return the default type
	 */
	public static int getDefaultType() {
		return defaultType;
	}
	
	/**
	 * Sets the point type.
	 *
	 * @param type the new point type
	 */
	public void setPointType(int type) {
		if (type>=0 && type<types.length)
			this.type = type;
	}

	/**
	 * Gets the point type.
	 *
	 * @return the point type
	 */
	public int getPointType() {
		return type;
	}


	/**
	 * Sets the default size.
	 *
	 * @param index the new default size
	 */
	public static void setDefaultSize(int index) {
		if (index>=0 && index<sizes.length) {
			defaultSize = convertIndexToSize(index);
			PointRoi instance = getPointRoiInstance();
			if (instance!=null)
				instance.setSize(index);
			Prefs.set(SIZE_KEY, index);
		}
	}
	
	/**
	 * Gets the default size.
	 *
	 * @return the default size
	 */
	public static int getDefaultSize() {
		return convertSizeToIndex(defaultSize);
	}

	/**
	 * Sets the size.
	 *
	 * @param index the new size
	 */
	public void setSize(int index) {
		if (index>=0 && index<sizes.length)
			this.size = convertIndexToSize(index);
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		return convertSizeToIndex(size);
	}
	
	/**
	 * Convert size to index.
	 *
	 * @param size the size
	 * @return the int
	 */
	private static int convertSizeToIndex(int size) {
		switch (size) {
			case TINY: return 0;
			case SMALL: return 1;
			case MEDIUM: return 2;
			case LARGE: return 3;
			case EXTRA_LARGE: return 4;
		}
		return 1;
	}

	/**
	 * Convert index to size.
	 *
	 * @param index the index
	 * @return the int
	 */
	private static int convertIndexToSize(int index) {
		switch (index) {
			case 0: return TINY;
			case 1: return SMALL;
			case 2: return MEDIUM;
			case 3: return LARGE;
			case 4: return EXTRA_LARGE;
		}
		return SMALL;
	}

	/**
	 *  Deprecated.
	 *
	 * @param color the new default cross color
	 */
	public static void setDefaultCrossColor(Color color) {
	}
	
	/**
	 *  Deprecated.
	 *
	 * @return the default cross color
	 */
	public static Color getDefaultCrossColor() {
		return null;
	}

	/**
	 *  Always returns true.
	 *
	 * @return true, if successful
	 */
	public boolean subPixelResolution() {
		return true;
	}
	
	/**
	 * Gets the point roi instance.
	 *
	 * @return the point roi instance
	 */
	private static PointRoi getPointRoiInstance() {
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp!=null) {
			Roi roi  = imp.getRoi();
			if (roi!=null) {
				if (roi instanceof PointRoi)
					return (PointRoi)roi;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see ij.gui.Roi#toString()
	 */
	public String toString() {
		if (nPoints>1)
			return ("Roi[Points, count="+nPoints+"]");
		else
			return ("Roi[Point, x="+x+", y="+y+"]");
	}

	/**
	 * Sets the hide labels.
	 *
	 * @param hideLabels the new hide labels
	 * @deprecated 
	 */
	public void setHideLabels(boolean hideLabels) {
		this.showLabels = !hideLabels;
	}
	
	/**
	 * Sets the default marker size.
	 *
	 * @param size the new default marker size
	 * @deprecated 
	 */
	public static void setDefaultMarkerSize(String size) {
	}
	
	/**
	 * Gets the default marker size.
	 *
	 * @return the default marker size
	 * @deprecated 
	 */
	public static String getDefaultMarkerSize() {
		return sizes[defaultSize];
	}

}