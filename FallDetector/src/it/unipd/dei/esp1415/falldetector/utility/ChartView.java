package it.unipd.dei.esp1415.falldetector.utility;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * ChartView class extends View for displaying a simple chart
 * from accelerometer data */
public class ChartView extends View{

	/**
	 * Provides how to draw the graph
	 */
	private Paint paint;
	
	
	/**
	 * Device's screen width
	 */
	private int widht;
	
	
	/**
	 * Device's screen height
	 */
	private int height;
	
	
	/**
	 * An array list which saves the data from the acceleromoter
	 */
	private ArrayList<Float> chartHeightData;
	
	
	/**
	 * Number of data stored from accelerometer
	 */
	private int size;


	/**
	 * Constructor of the ChartView. Set the screen size
	 * and configures how to draw.
	 * 
	 * @param context Which content to work for
	 * @param attrs Customized attributes
	 */
	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// Configures paint options
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLACK);
		paint.setAlpha(255);
		
		// Get the screen size
		widht = this.getWidth();
		height = this.getHeight();
		
		// And istantiate the array list
		chartHeightData = new ArrayList<Float>();
		size = 0;
	}
	
	
	/**
	 * Returns array list containing the accelerometer data
	 * @return ArrayList
	 */
	public ArrayList<Float> getChartHeightData(){
		return chartHeightData;
	};
	
	/**
	 * Returns the number of elements
	 * @return int
	 */
	public int getChartDataSize(){
		return size;
	}
	
	/**
	 * Returns a float array containing the accelerometer data
	 * @return float[]
	 */
	public float[] getArrayFloat(){
		
		float[] arrayFloat = new float[size];
		int i = 0;
		
		for(Float data : chartHeightData)
			arrayFloat[i++] = (data != null) ? data:0 ;
		
		return arrayFloat;
	}
	/**
	 * Set the data to be display
	 * 
	 * @param arrayList Contains data to displays
	 */
	public void setChartHeightData(float[] array){
		
		this.size = array.length;
		
		for (float data : array) {
			chartHeightData.add(Float.valueOf(data));
		}
	};
	
	/**
	 * Add new element to display in the chart
	 * @param data New data to display
	 */
	public void setChartHeightData(float data){
		chartHeightData.add(size, (Float) (((float)height / 2) - data));
		size++;
	}
	
	public int getWidht(){
		return this.widht;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if(size < widht){
			for(int j = 0; j < ((size % widht) - 1); j++){
				canvas.drawLine((float) j, (float) chartHeightData.get(size - (j + 2)),
						(float) (j + 1), (float) chartHeightData.get(size - (j + 1)), paint);
			}
		}
		else{
			for(int j = 0; j < (widht - 1); j++){
				canvas.drawLine((float) j, (float) chartHeightData.get(size - (j + 2)),
						(float) (j + 1), (float) chartHeightData.get(size - (j + 1)), paint);
			}
		}
	}
	
	@Override
    public void onSizeChanged (int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        this.widht = w;
        this.height= h;
    }
}
