package org.hkprog.m365gui.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.ImageIcon;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

/**
 * Utility class for working with SVG files
 * @author Peter <peter@quantr.hk>
 */
public class SVGUtil {
    
    /**
     * Converts an SVG resource to an ImageIcon
     * @param resourcePath the path to the SVG resource (e.g., "/m365gui.svg")
     * @param width desired width in pixels
     * @param height desired height in pixels
     * @return ImageIcon containing the rendered SVG
     */
    public static ImageIcon svgToImageIcon(String resourcePath, int width, int height) {
        try {
            InputStream svgStream = SVGUtil.class.getResourceAsStream(resourcePath);
            if (svgStream == null) {
                throw new IOException("SVG resource not found: " + resourcePath);
            }
            
            BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
            transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, (float) width);
            transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, (float) height);
            
            TranscoderInput input = new TranscoderInput(svgStream);
            transcoder.transcode(input, null);
            
            BufferedImage image = transcoder.getBufferedImage();
            return new ImageIcon(image);
            
        } catch (Exception e) {
            System.err.println("Error loading SVG: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Custom transcoder that outputs to BufferedImage
     */
    private static class BufferedImageTranscoder extends ImageTranscoder {
        private BufferedImage bufferedImage;
        
        @Override
        public BufferedImage createImage(int width, int height) {
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            return bufferedImage;
        }
        
        @Override
        public void writeImage(BufferedImage img, TranscoderOutput output) throws TranscoderException {
            // Nothing to do here
        }
        
        public BufferedImage getBufferedImage() {
            return bufferedImage;
        }
    }
}
