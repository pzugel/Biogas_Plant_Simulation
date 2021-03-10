package vrl.biogas.biogascontrol;

public interface Structure {
	public int numHydrolysis();
	public String name();
	public boolean methane();
	public boolean storage();
	public boolean feedback();
	
	public void run();
}
