package it.unipd.dei.esp1415.falldetector.utility;

import java.util.Random;

import android.graphics.Bitmap;

public class ColorUtil {
	
	public final static int START_COLOR = 0x00000000;
	public final static int FINAL_COLOR = 0xFFFFFFFF;
	/*private final static int[] COLOR_STEP = {0x020B060B, 0x00B0060C, 0x030003CC, 
		  									 0x050F8001, 0x0C0601E0, 0x01010101, 
		  									 0x02020602, 0x0B0C0500, 0x00C0A002,
		  									 0x07080808};*/
	
	private final static int MAX_VAL = 0xFF;
	private final static int MIN_VAL = 0x00;
	/**
	 * [m]
	 * This method calculate the color of the image for a new session
	 * 
	 * @param lastSession precedent session or null
	 * @return the value of the color in ARGB
	 */
	public static int imageColorSelector(Session lastSession){
		long color = 0L;
		/*
		if(lastSession != null){
			color = lastSession.getColorThumbnail();
		}
		
		for(int i=((int) Math.random()*20+10); i >= 0 ;i-- ){
			color = color + COLOR_STEP[(int) Math.random()*10];		
			color = color & 0x00000000FFFFFFFF;
		}
		*/
		Random random = new Random();

		int aa = /*((int)(( random.nextDouble()*201)+55))*/ MAX_VAL << 24;
		int rr = ((int)(( random.nextDouble()*256)))<< 16;
		int gg = ((int)(( random.nextDouble()*256)))<< 8;
		int bb = ((int)(( random.nextDouble()*256)));
		
		color = (((aa | rr) | gg) | bb);
		color = color & 0x00000000FFFFFFFF;
		System.out.printf("aa=%h rr=%h gg=%h bb=%h t=%h\n", aa, rr, gg, bb, color);
		
		return (int) color;
	}
	

	public static Bitmap recolorIconBicolor(int color, Bitmap bitmap){
		int aa = color >> 24;
		int rr = (color >> 16) & MAX_VAL;
		int gg = (color >> 8) & MAX_VAL;
		int bb = color & 0xFF;
		
		int invRR = MAX_VAL - rr;
		int invGG = MAX_VAL - gg;
		int invBB = MAX_VAL - bb;
		
		if((invRR-rr)<15 && (invGG - gg)<15 && (invBB - bb)<15){
			invRR = invRR + 100;
			invRR = invRR & MAX_VAL;

			invGG = invGG + 100;
			invGG = invGG & MAX_VAL;

			invBB = invBB + 100;
			invBB = invBB & MAX_VAL;
		}
		
		int invColor = aa | (invRR<<16) | (invGG<<8) | (invBB);
		
		Bitmap image =bitmap.copy(Bitmap.Config.ARGB_8888, true);;
		int heigth = image.getHeight();
		int width = image.getWidth();
		
		for(int y = 0; y<heigth; y++){
			int min;
			for(min= 0; min< width; min++){
				if(((image.getPixel(min, y))>>24)>MIN_VAL){
					break;
				}
			}
			
			int max;
			for(max= width-1; max <= 0 && min < width ; max--){
				if(((image.getPixel(max, y))>>24)>MIN_VAL){
					break;
				}
			}
			
			if(min < width && min != max){
				for (int x = min+1; x < max; x++){
					int c = image.getPixel(x, y);
					
					if((c>>24)!=MIN_VAL){
						image.setPixel(x, y, color);
					}else{
						image.setPixel(x, y, invColor);
					}
				}
			}
		}
		
		return image;
	}
	
}
