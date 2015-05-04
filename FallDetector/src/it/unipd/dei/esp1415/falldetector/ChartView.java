package it.unipd.dei.esp1415.falldetector;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ChartView extends View{

	private Paint paint;
	private int widht;
	private int height;

	private ArrayList<Float> chartHeightData;
	
	private int size;


	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		chartHeightData = new ArrayList<Float>();
		
		// Enable anti aliasing when drawing
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);	
		paint.setColor(Color.BLACK);
		paint.setAlpha(255);
		
		widht = this.getWidth();
		height = this.getHeight();
		
		size = 0;
	}
	
	public void setHeight(float data){
		
		chartHeightData.add(size, (Float) (((float)height / 2) - data));
		size++;
	}
	
	public int getWidht(){
		return this.widht;
	}
	
	@Override
	public void onDraw(Canvas canvas) {

		//		for(int i = 0; i < widht; i++)
		//		canvas.drawPoint(i, (int)(height/2), paint);
		for(int j = 0 ; j < size; j++){
			canvas.drawPoint((float)j,(float) chartHeightData.get(j), paint);
		}
	}
	
	@Override
    public void onSizeChanged (int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        this.widht = w;
        this.height= h;
    }
}
