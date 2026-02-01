package com.invoice.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ImageUtil
{
	private static final int MAX_SIZE = 256 * 1024; // 256 KB
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 800;
    
    public static byte[] compressImage(byte[] originalImage) throws IOException
    {
    	BufferedImage bf = ImageIO.read(new java.io.ByteArrayInputStream(originalImage));
    	if(bf == null)
    	{
    		throw new IOException("Image not Found");
    	}
    	
    	BufferedImage resize = resizeImage(bf,MAX_WIDTH,MAX_HEIGHT);
    	
    	float quality = 0.9f;
    	
    	byte[] compressedImage;
    	
    	do {
    		compressedImage = compress(resize,quality);
    		quality -= 0.05f;
    	}while(compressedImage.length>MAX_SIZE && quality >0.15f);
    		if(compressedImage.length>MAX_SIZE)
    		{
    			throw new IOException("Image size to big as per rules");
    		}
    	return compressedImage;
    }
    
    
    private static BufferedImage resizeImage(BufferedImage image,int maxWidth,int maxHeight)
    {
    	int width = image.getWidth();
    	int height = image.getHeight();
    	
    	double scale = Math.min((double) maxWidth / width,(double) maxHeight/ height);
    	
    	if(scale>=1)
    	{
    		return image;
    	}
    	

    	int newWidth = (int) (width * scale);
    	int newHeight = (int) (height * scale);
    	
    	
    	 Image scaled = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
    	 BufferedImage resizedImage = new BufferedImage(newWidth, height, BufferedImage.TYPE_INT_RGB);
    	
    	Graphics2D  gr = resizedImage.createGraphics();
    	gr.drawImage(scaled,0,0,null);
    	gr.dispose();
    	return resizedImage;
    }
    
    private static byte[] compress(BufferedImage image,float quality)throws IOException
    {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
    	ImageWriter writer = writers.next();
    	ImageWriteParam param  = writer.getDefaultWriteParam();
    	param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    	param.setCompressionQuality(quality);
    	
    	ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
    	writer.setOutput(ios);
        writer.write(null, new IIOImage(image, null, null), param);
    	writer.dispose();
    	ios.close();
    	return baos.toByteArray();
    }
    

}
