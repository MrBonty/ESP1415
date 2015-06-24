package it.unipd.dei.esp1415.falldetector.utility;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * ChartView class extends View for displaying a simple chart
 * of data */
public class ChartView extends View{

	private static final int MAX_DATA = 1000000;		// 1 million data per chart, about a total of 10MB
	
	/**
	 * Provides how to draw the graph
	 */
	private Paint paint;
	
	
	/**
	 *	The widht of the view
	 */
	private int viewWidht;
	
	
	/**
	 * The height of the view
	 */
	private int viewHeight;
	
	
	/**
	 * Chart data, the chart view read data from here and display it.
	 */
	private float[] chartData;
	
	/**
	 * Number of data stored
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
		viewWidht = this.getWidth();
		viewHeight = this.getHeight();
		
		// And istantiate the array
		chartData = new float[MAX_DATA];
		
		size = 0;
	}
	
	
	/**
	 * Returns array containing data
	 * @return float[]
	 */
	public float[] getChartData(){
		return chartData;
	};
	
	
	/**
	 * Returns the number of element
	 * @return int
	 */
	public int getSize(){
		return size;
	}
	
	/**
	 * Set the data to be display
	 * 
	 * @param array
	 */
	public void setChartData(float[] array, int size){
		chartData = array;
		this.size = size;
	};
	
	/**
	 * Add new data to displays in chart
	 * @param data New data to display
	 */
	public void addNewData(float data){
		chartData[size] = data;
		size++;
	}
	
	/**
	 * Get the view widht
	 * @return	int
	 */
	public int getViewWidht(){
		return this.viewWidht;
	}
	
	/**
	 * Get the view height
	 * @return	int
	 */
	public int getViewHeight(){
		return this.viewHeight;
	}
	
	public void onDraw(Canvas canvas) {
		if(size < viewWidht){
			for(int j = 0; j < ((size % viewWidht) - 1); j++){
				canvas.drawLine((float) j, chartData[size - (j + 2)] + (viewHeight / 2),
						(float) (j + 1), chartData[size - (j + 1)] + (viewHeight / 2), paint);
			}
		}
		else{
			for(int j = 0; j < (viewWidht - 1); j++){
				canvas.drawLine((float) j, chartData[size - (j + 2)] + (viewHeight / 2),
						(float) (j + 1), chartData[size - (j + 1)] + (viewHeight / 2), paint);
			}
		}
	}
	
	@Override
    public void onSizeChanged (int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewWidht = w;
        this.viewHeight= h;
    }
}
