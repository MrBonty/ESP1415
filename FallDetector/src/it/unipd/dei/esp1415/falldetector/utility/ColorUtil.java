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
	//private final static int MIN_VAL = 0x00;

	private final static int LAYER_TH = 6; 
	private final static int SQUARE_NUM_FOR_ROOT = 3; //total size of thumbnail is 90 (acepted value 1, 2, 3, 5, 6, 9, 15,..., 90)
	private final static int SQUARE_NUM = SQUARE_NUM_FOR_ROOT *SQUARE_NUM_FOR_ROOT;
	/**
	 * [m]
	 * This method calculate the color of the image for a new session
	 * 
	 * @return the value of the color in ARGB
	 */
	public static int imageColorSelector(){
		int color = 0;
		
		Random random = new Random();

		int aa = MAX_VAL << 24;
		int rr = ((int)(( random.nextDouble()*256)))<< 16;
		int gg = ((int)(( random.nextDouble()*256)))<< 8;
		int bb = ((int)(( random.nextDouble()*256)));
		
		color = (((aa | rr) | gg) | bb);
		
		return color;
	}
	

	/**
	 * [m]
	 * Method to recolor the thumbnail image
	 * 
	 * @param color first color as aarrggbb, second color is get from ((0xff)-rr) | ((0xff)-gg) | ((0xff)-bb)
	 * @param bitmap thumbnail image
	 * @return recolored image
	 */
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
		
		int invColor = (aa<<24) | (invRR<<16) | (invGG<<8) | (invBB);
		
		Bitmap image =bitmap.copy(Bitmap.Config.ARGB_8888, true);;
		int heigth = image.getHeight();
		int width = image.getWidth();

		int line = 0;
		int columns = 0;
		int xVal = LAYER_TH;
		int yVal = LAYER_TH;
		int heigthSquare = (heigth - 2*LAYER_TH)/SQUARE_NUM_FOR_ROOT; 
		int widthSquare = (width - 2*LAYER_TH)/SQUARE_NUM_FOR_ROOT;
		
		
		for(int i= 0; i<SQUARE_NUM; i++){
			
			for(int y = yVal; y < (yVal+heigthSquare); y++){
				for (int x = xVal; x < (xVal + widthSquare) ; x++){
					if(line%2 != 0){
						if(columns%2 != 0){
							image.setPixel(x, y, color);
						}else{
							image.setPixel(x, y, invColor);
						}
					}else{
						if(columns%2 != 0){
							image.setPixel(x, y, invColor);
						}else{
							image.setPixel(x, y, color);
						}
					}
				}
				
			}
			columns++;
			
			if(columns == SQUARE_NUM_FOR_ROOT){
				columns = 0;
				line++;
				yVal = LAYER_TH + line*heigthSquare;
				xVal = LAYER_TH + columns*widthSquare;
			}else{
				xVal = LAYER_TH + columns*widthSquare;
			}
		}
		
		return image;
	}
	
}
