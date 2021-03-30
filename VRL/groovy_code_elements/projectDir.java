package eu.mihosoft.vrl.user;

import java.nio.file.Path
import java.nio.file.Paths
@ComponentInfo(name="projectDir", category="Biogas")
public class projectDir implements java.io.Serializable {
	private static final long serialVersionUID=1L;
	public static String dir;
	  
	@MethodInfo(valueName="Directory",valueStyle="silent", hide=false)
	public Path projectDirectory(){
		File scriptDir = new File(getClass().protectionDomain.codeSource.location.path);
		dir = scriptDir.toString();
		int start = dir.lastIndexOf("/home");
		int end = dir.indexOf(".vrlp");
		dir = dir.substring(start,end+5);
		System.out.println("dir: " + dir);
		return Paths.get(dir);
	}
}
