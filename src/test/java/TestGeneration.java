
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import de.montiarcautomaton.generator.MontiArcGeneratorTool;
import de.se_rwth.commons.logging.Log;


public class TestGeneration {
	  
	  private static final String MODELPATH = "C:\\Users\\Flo\\Desktop\\usage\\";
	  private static final String TARGETPATH = "C:\\\\Users\\\\Flo\\\\Desktop\\result\\";
	  
	  
	  
	  @Before
	  public void setup() {
	    Log.enableFailQuick(false);
	  }
	  
	  @Test
	  public void testGeneration() {
	    MontiArcGeneratorTool script = new MontiArcGeneratorTool();
	    script.generate(Paths.get(MODELPATH).toFile(), Paths.get(TARGETPATH).toFile(), Paths.get(MODELPATH).toFile());
	  }
	  
	}
