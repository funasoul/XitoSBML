package jp.ac.keio.bio.fun.xitosbml.xitosbml;

import javax.swing.JOptionPane;

import jp.ac.keio.bio.fun.xitosbml.util.ModelSaver;
import jp.ac.keio.bio.fun.xitosbml.util.ModelValidator;


/**
 * Spatial SBML Plugin for ImageJ.
 *
 * @author Kaito Ii <ii@fun.bio.keio.ac.jp>
 * @author Akira Funahashi <funa@bio.keio.ac.jp>
 * Date Created: May 12, 2015
 *
 * The Class MainImgSpatial, which implements "run Spatial Image SBML plugin" function.
 */
public class MainImgSpatial extends MainSpatial {

	/**
	 * Overrides ij.plugin.PlugIn#run(java.lang.String)
     * A dialog for specifying the correspondence between the image and the region in the cell will be displayed.
	 * The specified image is read and the following process is performed.
	 *
     * 1. Interpolate an image if it is a Z-stack image (3D image)
	 * 2. Fill holes (blank pixels) in the image by morphology operation if exists
	 * 3. Export generated image to spatial SBML
	 *
	 * @param arg name of the method defined in plugins.config
	 */
	@Override
	public void run(String arg) {

		gui();
		// if close button is pressed, then exit this plugin
		if (imgexp.getDomFile() == null) {
		  return;
		}
		computeImg();
		SpatialSBMLExporter sbmlexp = new SpatialSBMLExporter(spImg);
		model = sbmlexp.getModel();
		sbmlexp.createGeometryElements();
		//visualize(spImg);
		
		//add species and parameter here
		int reply = JOptionPane.showConfirmDialog(null, "Do you want to add Parameters or Species to the model?", "Adding Parameters and species", JOptionPane.YES_NO_CANCEL_OPTION);
		if(reply == JOptionPane.YES_OPTION)
			addSBases();
		
		sbmlexp.addCoordParameter();
		document = sbmlexp.getDocument();
		ModelSaver saver = new ModelSaver(document);
		saver.save();
		spImg.saveAsImage(saver.getPath(), saver.getName());
		showDomainStructure();
		

		print();
		ModelValidator validator = new ModelValidator(document);
		validator.validate();
	}
}