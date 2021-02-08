package bg.maven.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
/**
 * Cette classes genère les  dependences sous forme maven à partir des dependences sous forme gradle
 * @author w1
 *
 */
public class MainTransformeDependenceGradleToMaven {
	private static String nameFile = "src/test/resources/build.gradle";
	public static void main(String[] arg) throws Exception {
		if (arg.length >0) {
			nameFile = arg[0];
		}
		File filegradle = new File(nameFile);
		System.out.println(" file input  ::   exists : " + filegradle.exists() + "  path :" + filegradle.getAbsolutePath());
		Path path = Path.of(filegradle.getPath());
		String s = Files.readString(path);
		System.out.println(s);
		String[] sArray = s.split("\n");
		Stream.of(sArray).filter(Objects::nonNull).filter(isDependency()).forEach(toMaven);		
		toXml(s);
	}

	private static Consumer<String> toMaven = (String s) -> {
		String[] sAr = s.trim().split(" ");
		String[] sDependency = sAr[1].replaceAll("\'", " ").trim().split(":");
		Dependency dependencie = new Dependency(sDependency);
		System.out.println("xxxxxxx"+dependencie);
	};

	private static Predicate<String> isDependency() {
		return s -> s.trim().startsWith("implementation");
	}
	
	private static void toXml(String s) throws Exception{
		String[] sArray = s.split("\n");
		Dependencies dependencies = new Dependencies(sArray);
		System.out.println("Dependencies  "+dependencies);
		JAXBContext contextObj = JAXBContext.newInstance(Dependencies.class); 
		Marshaller marshallerObj = contextObj.createMarshaller();  
	    marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
	    marshallerObj.marshal(dependencies, new FileOutputStream("zorg.xml"));  
	}
	
	@XmlRootElement  
	static class Dependencies {
		
		@XmlElement
		List<Dependency> dependencies = new ArrayList();
		
		public Dependencies() {
			super();
		}
		Dependencies(String[] sArray){
			for(String s : sArray) {
				s = s.trim();
				if (s.startsWith("implementation")) {
					String[] sAr = s.trim().split(" ");
					String[] sDependency = sAr[1].replaceAll("\'", " ").trim().split(":");
					Dependency dependency = new Dependency(sDependency);
					dependencies.add(dependency);
				}
			}
		}
		@Override
		public String toString() {
			return "Dependencies [dependencies=" + dependencies + "]";
		}
		
	}
	
	static class Dependency {
		@XmlElement
		String groupId = "";
		@XmlElement
		String artifactId = "";
		@XmlElement
		String version = "";
		
		public Dependency() {
			super();
		}
		public Dependency(String[] sDependency ) {
			groupId = sDependency[0];
			 artifactId = sDependency[1];
			 if (sDependency.length >= 3) {
				 version = sDependency[2];
			 }
					
		}
		@Override
		public String toString() {
			return "Dependency [groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version + "]";
		}
		
		
		
	}

}


