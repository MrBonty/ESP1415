package it.unipd.dei.esp1415.falldetector.utility;

import it.unipd.dei.esp1415.falldetector.service.FallDetectorService;

public class SimpleFallAlgorithm {

	public static double FALL_UPPER_BOUND = 18.5;
	public static double FALL_LOWER_BOUND = 2.5;
	public static int ACC_DATA_SIZE = 1000;
	public static int NUM_BLOCK = 3;
	
	private AccelData[] accDataBuffer;
	private int accDataSize;
	
	//what to do?
	private AccelData[] fallData;
	
	private AccelData[]	accDataAnalyzer;

	private int accDataAnalyzerBlockIndex;
	
	private int SEC_DATA_NUMBER = 1000 / FallDetectorService.MIN_SAMPLE_RATE;
	
	
	public SimpleFallAlgorithm(){
		accDataBuffer = new AccelData[ACC_DATA_SIZE];
		accDataSize = 0;
		
		accDataAnalyzer = new AccelData[NUM_BLOCK * ACC_DATA_SIZE];
		accDataAnalyzerBlockIndex = 0;
		
		fallData = new AccelData[SEC_DATA_NUMBER];
	}
	
	public void setElement(AccelData element){
		accDataBuffer[accDataSize] = element;
		if((accDataSize + 1) >= ACC_DATA_SIZE){
			System.arraycopy(accDataBuffer, 0, accDataAnalyzer, accDataAnalyzerBlockIndex * ACC_DATA_SIZE, accDataSize);
			accDataSize = 0;
			accDataAnalyzerBlockIndex = (accDataAnalyzerBlockIndex + 1) % NUM_BLOCK;
			fallDetector(accDataAnalyzerBlockIndex).run();
		}
		
		accDataSize++;	
	}
	
	private double module(AccelData element){
		return Math.sqrt(Math.pow(element.getX(), 2) 
				+ Math.pow(element.getY() , 2) 
				+ Math.pow(element.getZ() , 2));
	}
	
	private Thread fallDetector(final int blockIndex){
		return new Thread(){
			@Override
			public void run(){
				
				boolean isPotentialFall = false;
				int lowerBoundCounter = SEC_DATA_NUMBER / 5;
				
				for(int i = 0; i < ACC_DATA_SIZE; i++){
					if(isPotentialFall){
						if(module(accDataAnalyzer[(blockIndex * ACC_DATA_SIZE) + i]) < FALL_LOWER_BOUND){
							fallDetected((blockIndex * ACC_DATA_SIZE) + i);
							interrupt();
						}
						lowerBoundCounter--;
						if(lowerBoundCounter <= 0){
							lowerBoundCounter = SEC_DATA_NUMBER / 5;
							isPotentialFall = false;
						}
					}// isPotentialFall
					
					if(!isPotentialFall){
						if(module(accDataAnalyzer[blockIndex * ACC_DATA_SIZE + i]) > FALL_UPPER_BOUND)
							isPotentialFall = true;
					}// !isPotentialFAll
					
				}// for
				
				if(isPotentialFall){
					for(int i = 0 ; i < lowerBoundCounter; i++ ){
						if(module(accDataAnalyzer[( ( (blockIndex + 1) % 3) * ACC_DATA_SIZE) + i]) < FALL_LOWER_BOUND){
							fallDetected((( ( (blockIndex + 1) % 3) * ACC_DATA_SIZE) + i));
							interrupt();
						}
							
					}
				}
						
			}
		};
	}
	
	
	//what to do when the fall is detected
	private void fallDetected(int fallStartIndex){
		
		AccelData[] fallData = new AccelData[SEC_DATA_NUMBER];
		
		if((fallStartIndex - (SEC_DATA_NUMBER / 2) >= 0) && (fallStartIndex + (SEC_DATA_NUMBER / 2) < (NUM_BLOCK * ACC_DATA_SIZE))){
			for(int i = 0; i < SEC_DATA_NUMBER; i++)
				fallData[i] = accDataAnalyzer[fallStartIndex - (SEC_DATA_NUMBER / 2) + i];
		}
		else{
			if(fallStartIndex - (SEC_DATA_NUMBER / 2) < 0){
				
				int negativeIndex = (SEC_DATA_NUMBER / 2) - fallStartIndex;
				for(int i = 0; i < negativeIndex; i++)
					fallData[i] = accDataAnalyzer[NUM_BLOCK - negativeIndex + i];
				for(int i = 0; i < (SEC_DATA_NUMBER - negativeIndex); i ++)
					fallData[i]	 = accDataAnalyzer[i + negativeIndex];
			}
			if(fallStartIndex + (SEC_DATA_NUMBER / 2) < (NUM_BLOCK * ACC_DATA_SIZE)){
				int positiveIndex = (NUM_BLOCK * ACC_DATA_SIZE) - fallStartIndex;
				for(int i = 0; i < positiveIndex; i++)
					fallData[i] = accDataAnalyzer[(NUM_BLOCK * ACC_DATA_SIZE) - positiveIndex + i];
				for(int i = 0; i < SEC_DATA_NUMBER - positiveIndex; i++)
					fallData[i + positiveIndex] = accDataAnalyzer[i];
			}
		}
	}

}
