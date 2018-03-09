package be.umons.ibeatbox.main;

public class TagInit {
public static String TAG_GUITAR = "guitar";
public static String  TAG_PIANO ="piano";
public static String  TAG_BASSE ="basse";
public static String  TAG_DRUM ="drum";
public static String[] instruments=new String[]{"Clarinet", "BlowHole", "Saxofony", "Flute", "Brass",
"BlowBotl", "Bowed", "Plucked", "StifKarp", "Sitar", "Mandolin",
"Rhodey", "Wurley", "TubeBell", "HevyMetl", "PercFlut",
"BeeThree", "FMVoices", "VoicForm", "Moog", "Simple", "Drummer",
"BandedWG", "Shakers", "ModalBar", "Mesh2D", "Resonate", "Whistle"
	};
public static int getNumberInstrument(String instr){
	for(int i=0;i<instruments.length;++i){
		if(instruments[i].equals(instr))
			return i;
	}
  return -1;
}

}