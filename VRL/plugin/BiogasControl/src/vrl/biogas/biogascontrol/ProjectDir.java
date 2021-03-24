package vrl.biogas.biogascontrol;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import eu.mihosoft.vrl.annotation.MethodInfo;

// Doesnt work 
// Returns the location of the plugin

//dir before: /home/paul/.vrl/0.4.4/default/plugins/mainpanel-plugin.jar
//dir after: /hom

public class ProjectDir implements java.io.Serializable{
	private static final long serialVersionUID=1L;
	  public static String dir;
	  @MethodInfo(valueName="Directory", valueStyle="silent", hide=false)
		public Path projectDirectory(){
	        File scriptDir = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
	        dir = scriptDir.toString();
	        System.out.println("dir before: " + dir);
	        int start = dir.lastIndexOf("/home");
	        int end = dir.indexOf(".vrlp");
	        dir = dir.substring(start,end+5);
	        System.out.println("dir after: " + dir);
	        return Paths.get(dir);
		}
}
