package eu.dzim.tests.fx.math;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TeXUtil {
	
	private static final Logger LOG = Logger.getLogger(TeXUtil.class.getName());
	
	public static ImageView parseFormula(String formula) {
		return parseFormula(formula, 12.0f, java.awt.Color.BLACK, (java.awt.Color) null);
	}
	
	public static ImageView parseFormula(String formula, float size) {
		return parseFormula(formula, size, java.awt.Color.BLACK, (java.awt.Color) null);
	}
	
	public static ImageView parseFormula(String formula, float size, java.awt.Color fg, java.awt.Color bg) {
		ImageView iv = null;
		try {
			TeXFormula tex = new TeXFormula(formula);
			java.awt.Image awtImage = tex.createBufferedImage(TeXConstants.STYLE_DISPLAY, size, fg, bg);
			Image fxImage = SwingFXUtils.toFXImage((BufferedImage) awtImage, null);
			iv = new ImageView(fxImage);
			iv.setPreserveRatio(true);
			iv.setFitHeight(fxImage.getHeight());
			// As we create a new object which doesn't exist before, we need to
			// add it directly to call it afterwards.
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Could not create LaTeXFormula: " + e.getMessage());
		}
		return iv;
	}
}
