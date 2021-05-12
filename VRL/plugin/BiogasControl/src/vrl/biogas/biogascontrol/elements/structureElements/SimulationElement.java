package vrl.biogas.biogascontrol.elements.structureElements;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Interface for simulation elements
 * @author Paul Zügel
 *
 */
public interface SimulationElement{
	public String name();
	public File path();
	public void run() throws IOException, InterruptedException, ExecutionException;
}
