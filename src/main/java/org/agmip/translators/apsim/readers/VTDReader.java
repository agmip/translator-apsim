package org.agmip.translators.apsim.readers;

import java.util.List;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathParseException;

public abstract class VTDReader {
	protected final VTDNav vn;
	protected final String file;
	protected String prefix;

	
	public VTDReader(String file){
		final VTDGen vg = new VTDGen();
		vg.parseFile(file, false);
		vn = vg.getNav();
		this.prefix="";
		this.file = file;
	}
	
	public VTDReader(String file, String prefix){
		this(file);
		this.prefix = prefix;
	}
	
	
	public AutoPilot xpath(String xpath) throws Exception{
		AutoPilot ap = new AutoPilot(vn);
		ap.selectXPath(prefix+xpath);
		return ap;
	}
	
	
	public  String xPathText(String path) throws XPathParseException{
		AutoPilot ap = new AutoPilot(vn);
		ap.selectXPath(prefix+path);
		return ap.evalXPathToString();
	}
	
	public  double xPathDouble(String path) throws XPathParseException{
		AutoPilot ap = new AutoPilot(vn);
		ap.selectXPath(prefix+path);
		return ap.evalXPathToNumber();
	}	
	
	public  String xAbsPathText(String path) throws XPathParseException{
		AutoPilot ap = new AutoPilot(vn);
		ap.selectXPath(path);
		return ap.evalXPathToString();
	}
	
	public  String xpath(int count, String path) throws XPathParseException{
		AutoPilot ap = new AutoPilot(vn);
		ap.selectXPath(prefix+"["+count+"]"+path);
		return ap.evalXPathToString();
	}
	
	public int count(String xpath) throws Exception{
		AutoPilot ap = new AutoPilot(vn);
		ap.selectXPath("count("+prefix+xpath+")");
		return (int) ap.evalXPathToNumber(); 
	}
	
	abstract public<T> List<T> read() throws Exception;
	
	abstract public <T> T read(String path) throws Exception;
	
	
}
