import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpeedDateNYC_Reader {
    private static final int MIN_LINE_LENGTH = 250;
    private static final String[] FIELDS = {"Id", "Speed", "TravelTime", "EncodedPolyLine"};
        // Id for debugging
    private static final String DATA_URL = "http://207.251.86.229/nyc-links-cams/LinkSpeedQuery.txt";

    public static void main(String[] args) throws Exception {
        // load data
        URL source = new URL(DATA_URL);
        BufferedReader in = new BufferedReader(new InputStreamReader(source.openStream()));

        // read first line (metadata)
        String str = in.readLine();
        String[] values = str.split("\\t", -1);

        // get fields order
        int[] fieldsNumbers = new int[FIELDS.length];
        for(int i = 0; i < values.length; i++){
            for(int j = 0; j < FIELDS.length; j++) {
                // values in original file round with "
                if (values[i].compareToIgnoreCase("\""+ FIELDS[j] + "\"") == 0)
                    fieldsNumbers[j] = i;
            }
        }

        // read data
        while ((str = in.readLine()) != null) {
            // empty lines
            if (str.trim().length() == 0)
                continue;

            // fix \n in middle of line
            if(str.length() < MIN_LINE_LENGTH)
                str += in.readLine();

            // split
            values = str.split("\\t", -1); // don't truncate empty FIELDS

            // output
            String tmp = "";
            for (int i = 0; i < FIELDS.length; i++){
                tmp += FIELDS[i];
                tmp += ":";
                tmp += values[fieldsNumbers[i]];
                tmp += " ";

                // Decode Google Polylins
                if(FIELDS[i] == "EncodedPolyLine"){
                    tmp += "\nDecoded Poly Line: ";
                    String encodedPolyLine = values[fieldsNumbers[i]];

                    // remove " at the start and end of the original encodedPolyLine
                    encodedPolyLine = encodedPolyLine.replaceAll("^\"|\"$", "");

                    // check strings ends with B's
                    if(encodedPolyLine.matches("(.*)(BB+)$")){
                        // trim the B's
                        encodedPolyLine = encodedPolyLine.replaceAll("BB+$","");
                        // e.g. "_pfxF`}yaMdFsWfDmPpH}^lEgTBBBBB" -> "_pfxF`}yaMdFsWfDmPpH}^lEgT"
                    }

                    try {
                        tmp += PolylineDecoder.decodePoly(encodedPolyLine);
                    }
                    catch (StringIndexOutOfBoundsException e){
                        tmp += "Line could not be decoded...";
                    }
                }
                tmp += "\n";
            }
            System.out.println(tmp);
        }
    }
}

/* Sample output:
Id:"1"
Speed:"16.16"
TravelTime:"321"
EncodedPolyLine:"}btwFx|ubMsD_AgJcAcR{ByJ_AsBFiEbByCXaFuAkLiDsTaNsPoKmCmB"
Decoded Poly Line: [Location{latitude=40.74047, longitude=-74.00925}, Location{latitude=40.74137, longitude=-74.00893}, Location{latitude=40.74317, longitude=-74.00859}, Location{latitude=40.74623, longitude=-74.00797}, Location{latitude=40.74812, longitude=-74.00765}, Location{latitude=40.7487, longitude=-74.00769}, Location{latitude=40.74971, longitude=-74.00819}, Location{latitude=40.75048, longitude=-74.00832}, Location{latitude=40.75161, longitude=-74.00789}, Location{latitude=40.75375, longitude=-74.00704}, Location{latitude=40.75721, longitude=-74.00463}, Location{latitude=40.76003, longitude=-74.00263}, Location{latitude=40.76074, longitude=-74.00208}]

Id:"2"
Speed:"24.23"
TravelTime:"133"
EncodedPolyLine:"y{swFvavbMjANlGSvQn@fa@fBhQdA"
Decoded Poly Line: [Location{latitude=40.73933, longitude=-74.01004}, Location{latitude=40.73895, longitude=-74.01012}, Location{latitude=40.7376, longitude=-74.01002}, Location{latitude=40.7346, longitude=-74.01026}, Location{latitude=40.72912, longitude=-74.01078}, Location{latitude=40.72619, longitude=-74.01113}]

Id:"3"
Speed:"19.88"
TravelTime:"310"
EncodedPolyLine:"mtxwF|}sbMl@^~GpK|LrIbLlH??lK~G|FtD`C~@}@WdWnGdKmC|k@~G`CRzElC"
Decoded Poly Line: [Location{latitude=40.76375, longitude=-73.99919}, Location{latitude=40.76352, longitude=-73.99935}, Location{latitude=40.76208, longitude=-74.00136}, Location{latitude=40.75985, longitude=-74.00306}, Location{latitude=40.75775, longitude=-74.00457}, Location{latitude=40.75775, longitude=-74.00457}, Location{latitude=40.75576, longitude=-74.00601}, Location{latitude=40.75449, longitude=-74.00692}, Location{latitude=40.75384, longitude=-74.00724}, Location{latitude=40.75415, longitude=-74.00712}, Location{latitude=40.75028, longitude=-74.00848}, Location{latitude=40.74833, longitude=-74.00777}, Location{latitude=40.74114, longitude=-74.00921}, Location{latitude=40.74049, longitude=-74.00931}, Location{latitude=40.73939, longitude=-74.01002}]
*/
