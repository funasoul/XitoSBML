package jp.ac.keio.bio.fun.xitosbml.cui;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.TidySBMLWriter;
import org.sbml.jsbml.ext.spatial.Geometry;
import org.sbml.jsbml.ext.spatial.SpatialModelPlugin;

import ij.ImagePlus;
import ij.plugin.PlugIn;
import jp.ac.keio.bio.fun.xitosbml.image.CreateImage;
import jp.ac.keio.bio.fun.xitosbml.image.Filler;
import jp.ac.keio.bio.fun.xitosbml.image.ImageBorder;
import jp.ac.keio.bio.fun.xitosbml.image.ImageEdit;
import jp.ac.keio.bio.fun.xitosbml.image.Interpolator;
import jp.ac.keio.bio.fun.xitosbml.image.SpatialImage;
import jp.ac.keio.bio.fun.xitosbml.visual.DomainStruct;
import jp.ac.keio.bio.fun.xitosbml.visual.Viewer;

public abstract class CuiMainSpatial implements PlugIn {

	/** The SBML document. */
	protected SBMLDocument document;

	/** The SBML model. */
	protected Model model;

	/** The SBML spatialplugin. */
	protected SpatialModelPlugin spatialplugin;

	/** The hashmap of domain types. */
	private HashMap<String, Integer> hashDomainTypes;

	/** The hashmap of sampled value of spatial image. */
	protected HashMap<String, Integer> hashSampledValue;

	/** The viewer. */
	protected Viewer viewer;

	/**
	 * The SpatialImage, which is a class for handling spatial image in XitoSBML.
	 */
	protected SpatialImage spImg;

	/** TrialForImage **/
	protected TrialForImg trial;

	protected void cui(ImagePlus imager) {
		hashDomainTypes = new HashMap<String, Integer>();
		hashSampledValue = new HashMap<String, Integer>();
		// imager.show();
		trial = new TrialForImg(hashDomainTypes, hashSampledValue, imager);
		// HashMap<String, ImagePlus> hashDomFile = trial.getDomFile();
		// System.out.println(hashDomFile.values());
	}

	/**
	 * Following process is performed to an image:
	 * <ol>
	 * <li>Interpolate an image if it is a Z-stack image (3D image) and the voxel
	 * size of each axis (x, y and z) is not equal</li>
	 * <li>Fill holes (blank pixels) in the image by morphology operation if
	 * exists</li>
	 * <li>Add a membrane between two different domains if exists</li>
	 * </ol>
	 *
	 * The converted image will be generated as
	 * {@link jp.ac.keio.bio.fun.xitosbml.image.SpatialImage}, which is a base class
	 * for representing spatial image in XitoSBML.
	 */

	protected void computeImgTrial() {
		Interpolator interpolator = new Interpolator();
		HashMap<String, ImagePlus> hashDomFile = trial.getDomFile();
		interpolator.interpolate(hashDomFile);
		Filler fill = new Filler();

		for (Entry<String, ImagePlus> e : hashDomFile.entrySet())
			hashDomFile.put(e.getKey(), fill.fill(e.getValue()));

		CreateImage creIm = new CreateImage(trial.getDomFile(), hashSampledValue);
		spImg = new SpatialImage(hashSampledValue, hashDomainTypes, creIm.getCompoImg());
		ImagePlus img = fill.fill(spImg);
		spImg.setImage(img);
		ImageBorder imgBorder = new ImageBorder(spImg);
		spImg.updateImage(imgBorder.getStackImage());

		new ImageEdit(spImg);

	}

	/**
	 * Show inclusion relationship of domains as a graph.
	 */
	protected void showDomainStructure() {
		spatialplugin = (SpatialModelPlugin) model.getPlugin("spatial");
		Geometry g = spatialplugin.getGeometry();
		new DomainStruct().show(g);
	}

	protected void print() {
		String docStr;
		try {
			docStr = new TidySBMLWriter().writeSBMLToString(document);
			System.out.println(docStr);
		} catch (SBMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
