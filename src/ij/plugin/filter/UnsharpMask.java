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
package ij.plugin.filter;
import ij.IJ;
import ij.ImagePlus;
import ij.Macro;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.awt.AWTEvent;
import java.awt.Rectangle;

// TODO: Auto-generated Javadoc
/** This plugin-filter implements ImageJ's Unsharp Mask command.
 * Unsharp masking subtracts a blurred copy of the image and rescales the image
 * to obtain the same contrast of large (low-frequency) structures as in the
 * input image. This is equivalent to adding a high-pass filtered image and
 * thus sharpens the image.
 * "Radius (Sigma)" is the standard deviation (blur radius) of the Gaussian blur that
 * is subtracted. "Mask Weight" determines the strength of filtering, where "Mask Weight"=1
 * would be an infinite weight of the high-pass filtered image that is added.
 */
public class UnsharpMask implements ExtendedPlugInFilter, DialogListener {
    
    /** The sigma. */
    private static double sigma = 1.0; // standard deviation of the Gaussian
    
    /** The weight. */
    private static double weight = 0.6; // weight of the mask
    
    /** The flags. */
    private final int flags = DOES_ALL|SUPPORTS_MASKING|CONVERT_TO_FLOAT|SNAPSHOT|KEEP_PREVIEW;
    
    /** The gb. */
    private GaussianBlur gb;

    /**
     *  Method to return types supported.
     *
     * @param arg Not used by this plugin
     * @param imp The image to be filtered
     * @return Code describing supported formats etc.
     * (see ij.plugin.filter.PlugInFilter & ExtendedPlugInFilter)
     */
    public int setup(String arg, ImagePlus imp) {
        return flags;
    }
    
    /** This method is invoked for each slice or color channel. It filters
     * an image by enhancing high-frequency components. Since this
     * PlugInFilter specifies the CONVERT_TO_FLOAT and SNAPHOT
     * flags, 'ip' is always a FloatProcessor with a valid snapshot.
     * @param ip The image, slice or channel to filter
     */
    public void run(ImageProcessor ip) {
        sharpenFloat((FloatProcessor)ip, sigma, (float)weight);
    }
    
    /**
     *  Unsharp Mask filtering of a float image. 'fp' must have a valid snapshot.
     *
     * @param fp the fp
     * @param sigma the sigma
     * @param weight the weight
     */
    public void sharpenFloat(FloatProcessor fp, double sigma, float weight) {
        if (gb == null) gb = new GaussianBlur();
        gb.blurGaussian(fp, sigma, sigma, 0.01);
        if (Thread.currentThread().isInterrupted()) return;
        float[] pixels = (float[])fp.getPixels();
        float[] snapshotPixels = (float[])fp.getSnapshotPixels();
        int width = fp.getWidth();
        Rectangle roi = fp.getRoi();
        for (int y=roi.y; y<roi.y+roi.height; y++)
            for (int x=roi.x, p=width*y+x; x<roi.x+roi.width; x++,p++)
                pixels[p] = (snapshotPixels[p] - weight*pixels[p])/(1f - weight);
    }

    /**
     *  Ask the user for the parameters.
     *
     * @param imp the imp
     * @param command the command
     * @param pfr the pfr
     * @return the int
     */
    public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
        String options = Macro.getOptions();
        boolean oldMacro = false;    //for old macros, "gaussian radius" was 2.5 sigma
        if  (options!=null) {
            if (options.indexOf("gaussian=") >= 0) {
                oldMacro = true;
                Macro.setOptions(options.replaceAll("gaussian=", "radius="));
            }
        }
        GenericDialog gd = new GenericDialog(command);
        sigma = Math.abs(sigma);
        if (weight<0) weight = 0;
        if (weight>0.99) weight = 0.99; 
        gd.addNumericField("Radius (Sigma)", sigma, 1, 6, "pixels");
        gd.addNumericField("Mask Weight (0.1-0.9)", weight,2);
        gd.addPreviewCheckbox(pfr);
        gd.addDialogListener(this);
        gd.showDialog();                        //input by the user (or macro) happens here
        if (gd.wasCanceled()) return DONE;
        if (oldMacro) sigma /= 2.5;
        IJ.register(this.getClass());           //protect static class variables (parameters) from garbage collection
        return IJ.setupDialog(imp, flags);      //ask whether to process all slices of stack (if a stack)
    }

    /* (non-Javadoc)
     * @see ij.gui.DialogListener#dialogItemChanged(ij.gui.GenericDialog, java.awt.AWTEvent)
     */
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        sigma = gd.getNextNumber();
        weight = gd.getNextNumber();
        if (sigma < 0 || weight < 0 || weight > 0.99 || gd.invalidNumber())
            return false;
        else return true;
    }

    /**
     *  Since most computing time is spent in GaussianBlur, forward the
     * information about the number of passes to Gaussian Blur. The
     * ProgressBar will be handled by GaussianBlur.
     *
     * @param nPasses the new n passes
     */
    public void setNPasses(int nPasses) {
        if (gb == null) gb = new GaussianBlur();
        gb.setNPasses(nPasses); 
    }
}