package patatavival.Antonio.mvm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class LangFile {
	
	private HashMap<String, String> keyvalues;
	
	@SuppressWarnings("resource")
	public LangFile(File f) {
		
		this.keyvalues = new HashMap<String, String>();
		if (f.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String d;
				while((d=br.readLine())!=null) {
					if (d.contains("=") && !d.startsWith("//")) {
						String key = d.split("=")[0];
						String value = d.substring((d.split("=")[0] + "=").length());
						this.keyvalues.put(key, value);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String get(String key) {
		if (this.keyvalues.containsKey(key)) {
			return this.keyvalues.get(key);
		} else {
			return key;
		}
	}
	
	public String get(String key, Object[] params) {
		int i = 0;
		if (this.keyvalues.containsKey(key)) {
			String val = this.get(key);
			while(val.contains("%" + i)) {
				val = val.replaceAll("%" + i, params[i] + "");
				i++;
			}
			return val;
		} else {
			return key;
		}
	}
}
