package migu.demo;

import cn.bmob.v3.BmobObject;

public class Vote extends	BmobObject{

	private	String results;
	private	String imei;
	
	public	void	setIMEI(String imei){
		this.imei=imei;
	}
	
	public	String getIMEI(){
		return	imei;
	}
	
    
    public	String getResults(){
    	return	results;
    }
    
    public	void	setResults(String results){
    	this.results=results;
    }
}
