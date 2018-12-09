import org.apache.poi.ss.usermodel.Workbook;



import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;


public class StatsCalculator {
	
	
	
	private static final long serialVersionUID = 1L;
	private static Data rawDataObjct  = new Data(timeStamp());
	private static DecimalFormat number_format = new DecimalFormat(".##");   //The hash tags represent the number of decimal places that the final output number will have
	private static int colNumDesird = 2;//Only column numbers 1 and greater because the zero column doesn't contain numerical data
	private static int waitTime = 7;//specified time until old data is cleared, most likely needs to be higher since data will be cleared if not fast enough
	private static String newDataFileName;//The file name of the new excel Data!
	
	// The data CAN be queried for a specific range!
	private static int RangeMin = 0;//Set minimum value here
	private static int RangeMax = 500;//Set maximum value here
	private static double Range = 0;
	
	JPanel jp = new JPanel();
	JTextArea inputArea = new JTextArea(10,40);
	JTextArea textArea = new JTextArea ("Test");

	
	public StatsCalculator(){
		
		 String s = "Paste in raw data here over this comment (raw data e.g = 34,...,456 )";       
	            
	        JScrollPane scrollPane = new JScrollPane(new JLabel(s));
	        scrollPane.setPreferredSize(new Dimension(400,400));
	        
	        Object message = scrollPane;

	        JTextArea textArea = new JTextArea(s);
	        textArea.setLineWrap(true);
	        textArea.setWrapStyleWord(true);
	        textArea.setMargin(new Insets(5,5,5,5));
	        scrollPane.getViewport().setView(textArea);
	        message = scrollPane;
	        int retVal = JOptionPane.showConfirmDialog(null,message,"Raw Data Cruncher",JOptionPane.YES_NO_OPTION,1);
	        
	      if(retVal == 0){
				String rawDataStrng =textArea.getText();
				String[] rawDataStringArr = rawDataStrng.split(",");
				

                //Check for invalid inputs
				for(int i = 0; i < rawDataStringArr.length; i++){
					
					if(!rawDataStringArr[i].matches("[0-9]+")){
						System.out.println("Invalid Raw data...");
						
						//startScrn();
						System.exit(0);
					}
				}

				for(int i = 0; i < rawDataStringArr.length; i++){
					
					if(Double.parseDouble(rawDataStringArr[i]) > RangeMax || Double.parseDouble(rawDataStringArr[i]) < RangeMin){
					}//i.e if the data value is above 500 or less than 0 then it will be excluded from the main values array
					else{
						rawDataObjct.addData(Double.parseDouble(rawDataStringArr[i]));
					}
				}
	      }
	      else{
	    	  startScrn();
	      }                                 
	   
	}
	
	public static void main(String[] args) {
		
		startScrn();
		System.exit(0);

	}
	

 
	private static void OldRawDataClear(){
		
		String[] PreviousTimeStamp = rawDataObjct.GetTimeStmp().split(":");

	    
	    String[] currentTime =  timeStamp().split(":");

	    
	    //index 3 is where the minute element is stored and index 4 is where the second element is stored
	    if(!PreviousTimeStamp[3].contains(currentTime[3])){// if a minute has passed then clear data, assuming program will not be run for over 24hrs
	    	rawDataObjct.clearRawDataObjct();
	    }
	   
	    	if(!PreviousTimeStamp[4].contains(currentTime[4])){//If the seconds do not match in the time stamps then check the seconds difference 
	    		
	    		int start = Integer.valueOf(PreviousTimeStamp[4]);
	    		int finish = Integer.valueOf(currentTime[4]);
	    		int difference = finish - start;
	    		
	    		if(difference >= waitTime){//If new data is entered after SEVEN seconds or greater then the old data is removed 
	    			rawDataObjct.clearRawDataObjct();
	    		}
	    	}
	    	
	}
	
	private static void startScrn(){
		
		
		
		
	
		
		
        	JOptionPane optionPane = new JOptionPane("Press OK to Continue, Old data will be deleted in "+waitTime+" seconds");
    		JDialog dialog = optionPane.createDialog("Welcome to the Number Cruncher!");
    		dialog.setAlwaysOnTop(true);
    		dialog.setVisible(true);
        
        try 
		{
        	OldRawDataClear();
        	Object[] options = {"Raw", "Excel(.xls)"};
                int dataType = JOptionPane.showOptionDialog(null,  "Would you like to crunch raw or excel(xls) data?", "The Number Cruncher",  JOptionPane.YES_NO_CANCEL_OPTION,   JOptionPane.QUESTION_MESSAGE,null, options, null);
               
                if(dataType == 0){
                
                	rawData();
                }
                else if(dataType == 1){
                	
                	try{
                		//OldRawDataClear();
                	String xlsFileName = (String) JOptionPane.showInputDialog(null,"Please enter the excel (.xls) file name below with .xls at the end:","The Excel Cruncher",JOptionPane.PLAIN_MESSAGE, null,null,null);
                	if(xlsFileName == null){
                		startScrn();//Recursive method design
                	}
                	while(!xlsFileName.contains(".xls")){
                		
                	System.out.println("This file is not valid");
                    xlsFileName = (String) JOptionPane.showInputDialog(null,"Please enter the excel (.xls) file name below with .xls at the end:","The Excel Cruncher",JOptionPane.PLAIN_MESSAGE, null,null,null);

                	
                	
                	}
                	if(xlsFileName.contains(".xls")){
                
                		excelData(xlsFileName);
                	}
                	}
                	catch(Exception ex)
                	{
                		
                	}
                
                }
   

	    }
		catch (Exception ex) {}	
		
	}
	
	private static String timeStamp(){

        String dateTime = String.valueOf(ZonedDateTime.now());
		
		String time = "DATE: ";
		int timeIndxEnd = 0;
		
		for(int i = 0; i < dateTime.length(); i++){
			
			if(String.valueOf(dateTime.charAt(i)).contains(".")){
				
				timeIndxEnd = i;
			}	
		}
		
		for(int i = 0; i < timeIndxEnd; i++){
			
			if(String.valueOf(dateTime.charAt(i)).contains("T")){
				time += " TIME: ";
			}
			else{
				time += String.valueOf(dateTime.charAt(i));
			}
		}

		
		return time;
	}
	
	private static void resultsOut(){
		
		if(rawDataObjct.getArrSize() == 0){//The values array size would be zero if the user hit No or tries to exit
			System.exit(0);
		}
		else{
			
			System.out.println(rawDataObjct.GetTimeStmp());
       	    System.out.println("Sum: "+number_format.format(sumValue()));
			System.out.println("Mean: "+number_format.format(meanValue()));
			System.out.println("Median: "+number_format.format(medianValue()));
			System.out.println("Range: "+number_format.format(Range));
			System.out.println("Standard deviation: "+number_format.format(standDevi()));
			System.out.println("---------------------------------------------------------------");
			
		}
		
	}
	
	
	private static void rawData(){
		
		rawDataObjct.setTimeStmp(timeStamp());//Time data was to be stored
	
    	new StatsCalculator();
   
    	
        try 
		{
        	
        	resultsOut();
			startScrn();
			System.exit(0);
	    }
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(0);
		}	     
	}
	
	
	
	private static void excelData(String spreadsheetNme){//excelData("sampledata.xls");//New data comes in
		
		try{
			
			rawDataObjct.setTimeStmp(timeStamp());//Time data was to be stored
			newDataFileName = spreadsheetNme;
			OldDataClear();//Old data with non matching names is removed
			FileInputStream input = new FileInputStream(new File(spreadsheetNme));
			
			HSSFWorkbook workbook = new HSSFWorkbook(input);
			HSSFSheet sheet = workbook.getSheetAt(0);
			
			int FirstRow = sheet.getFirstRowNum();
			int LastRow = sheet.getLastRowNum();
       
			//FirstRow+1 because we don't want the title values
			for(int i = FirstRow+1; i < LastRow+1; i++){//+1 to LastRow because it matters to us!
				
				//Try catch blocks in case non numerical values are present in the spreadsheet
				try
				{
				  if(sheet.getRow(i).getCell(colNumDesird)==null){
				}
				else{
					String stringValue = (sheet.getRow(i).getCell(colNumDesird)).toString();
					if(Double.parseDouble(stringValue) > RangeMax || Double.parseDouble(stringValue) < RangeMin){
					}//i.e if the data value is above 500 or less than 0 then it will be excluded from the main values array
					else{
						rawDataObjct.addData(Double.parseDouble(stringValue));
					}
				 }
				}
				catch(Exception e){
					System.out.println("Dataset contains invaild terms therfore value at row "+i+" ,col "+(colNumDesird+1)+ " will be ignored!");//+1 because of the first column
				}
			}
			
			 resultsOut();
			 workbook.close();	
			 startScrn();
		}
		catch(Exception e){
			e.printStackTrace();
			
			System.exit(0);
		}
		
	}
	
	
	private static void OldDataClear(){
		String filedirectory = "C:\\Users\\Cephas Kevin\\workspace\\DemoProgram";  //The file directory of the Program,where old data may lie and is deleted and new data is located
		//The file directory provided above is very specific, please change accordingly
		File file = new File(filedirectory);
		boolean isExist = file.exists();
		if(isExist==true)
		{
			String dir_name = filedirectory; 
			File dir = new File(dir_name);
			
			File[] dir_list = dir.listFiles();
			for(int i=0;i<dir_list.length;++i)
			{
				
				if(!dir_list[i].getName().matches(newDataFileName))//This is the file name of the new Data, any other xls files will be deleted!
				{
					if(dir_list[i].getName().contains(".xls")){
						System.out.println(dir_list[i].getName()+" was deleted!");
						dir_list[i].delete();
					}
					//System.out.println(dir_list[i].getName());
				}
				else
				{
				}
				
			}
			
		}
		else if(isExist == false)
		{
			  System.out.println("File directory has changed OR this dirctory/file doesn't exist ");
		}
	}
	
	private static double sumValue(){
		
	
		double currentSum = 0;
		for(int i = 0; i < rawDataObjct.getArrSize(); i++){
			
			currentSum += rawDataObjct.getRawDtObjctVal(i);
		}
		return currentSum;
	
	}
	
	private static double meanValue(){
		
		
		double meanVal = sumValue()/rawDataObjct.getArrSize();
		return meanVal;
		
	}
	
	private static double medianValue(){
		
		double medianVal = 0;
		//Copying the values array to not mix up data
		ArrayList<Double> valuesCopy = new ArrayList<Double>(rawDataObjct.getArrSize());
		for(int i = 0; i < rawDataObjct.getArrSize(); i++){
			valuesCopy.add(rawDataObjct.getRawDtObjctVal(i));
		}
		

		//Sort the Copied arrayList into Ascending order
		Collections.sort(valuesCopy);//If the program was meant to crunch very large data sets then I would implement a sorting algorithm
		
		
		double minVal = valuesCopy.get(0);
		double maxVal = valuesCopy.get(rawDataObjct.getArrSize()-1);
		Range = (maxVal-minVal); 
		

		
		int valuesCpArraySz = valuesCopy.size();
		if(valuesCpArraySz%2 == 0){
			double midA = valuesCopy.get(((valuesCpArraySz/2)-1));
			double midB = valuesCopy.get((valuesCpArraySz/2));
			double midOfAB = (midA+midB)/2;
			
			medianVal = midOfAB;
		}
		else
		{
			medianVal = valuesCopy.get(((valuesCpArraySz-1)/2));
		}
		
		return medianVal;
	}
	
	private static double standDevi(){
		
		//Copying the values array to not mix up data
		ArrayList<Double> valuesCopyA = new ArrayList<Double>(rawDataObjct.getArrSize());
		for(int i = 0; i < rawDataObjct.getArrSize(); i++){
		     valuesCopyA.add(rawDataObjct.getRawDtObjctVal(i));
		}
		
		ArrayList<Double> valuesCopySqure = new ArrayList<Double>(valuesCopyA.size());
		for(int i = 0; i < valuesCopyA.size(); i++){
			
		
			
			valuesCopySqure.add(Math.pow(valuesCopyA.get(i),2));
		}
		
		double Sum = 0;
		for(int i = 0; i < rawDataObjct.getArrSize(); i++){
			
			Sum += valuesCopyA.get(i);
		}
		
		double SumOfSquard = 0;
		for(int i = 0; i < valuesCopySqure.size(); i++){
			
			SumOfSquard += valuesCopySqure.get(i);
		}
		
		
		double variance = (SumOfSquard/valuesCopySqure.size())-((Sum/rawDataObjct.getArrSize())*(Sum/rawDataObjct.getArrSize()));
		
		double standDeviation = Math.sqrt(variance);
		
		return standDeviation;
	}
	
	
}
