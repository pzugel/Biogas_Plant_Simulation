package eu.mihosoft.vrl.user;

import java.nio.file.Path
import java.nio.file.Paths
@ComponentInfo(name="Project Directory", category="Biogas")
public class projectDir implements java.io.Serializable {
  private static final long serialVersionUID=1L;
  public static String dir;
    
  @MethodInfo(name="getDirectory", 
		valueName="Directory",
		valueStyle="silent", 
		hide=false)
  public Path projectDirectory(){
    File scriptDir = new File(getClass().protectionDomain.codeSource.location.path);
    dir = scriptDir.toString();
    String os = System.getProperty("os.name");
    int end = dir.indexOf(".vrlp");   
    int start;
    
    if(os.contains("Windows")){
      start = dir.indexOf("Drive_");
      String drive = dir.substring(start+6, start+7);
      dir = drive + ":\\" + dir.substring(start+8,end+5);
    } else if(os.contains("Linux")){
      start = dir.lastIndexOf("/home");
      dir = dir.substring(start,end+5);
    }

    System.out.println("dir: " + dir);
    return Paths.get(dir);
  }

  @MethodInfo(name="manualDirectory", valueName="Directory",valueStyle="silent", hide=true)
  public Path manualDirectory(
    @ParamInfo(name="Project Directory", 
			style="load-dialog", 
			options="endings=[\".vrlp\"]; description=\"Path to project\"") String dir){
      System.out.println("dir: " + dir);
      return Paths.get(dir); 
  }
}

