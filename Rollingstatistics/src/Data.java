import java.util.ArrayList;

public class Data {
	
	private static ArrayList<Double> values = new ArrayList<Double>();
	private String timeStamp;

	
	Data(String timStmp)
	{
		timeStamp = timStmp;
	
	}
	public String GetTimeStmp()
	{
		return(timeStamp);
	}
	public void setTimeStmp(String x){
		
		timeStamp = x;
	}
	public ArrayList<Double> GetArrLst()
	{
		return(values);
	}
	public void addData(double x){
		values.add(x);
	}
	public int getArrSize(){
		
		int size = values.size();
		
		return size;
	}
	public void clearRawDataObjct(){
		values.clear();
	}
	public double getRawDtObjctVal(int i){
		
		
		return values.get(i);
	}

}

