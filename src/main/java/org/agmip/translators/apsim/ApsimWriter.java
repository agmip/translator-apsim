package org.agmip.translators.apsim;

import adjustments.WeatherAdjustAdaptor;
import adjustments.WeatherAdjustAdaptor.WeatherAdjustment;
import static org.agmip.util.JSONAdapter.toJSON;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import org.agmip.common.Functions;

import org.agmip.core.types.TranslatorOutput;
import org.agmip.translators.apsim.core.ACE;
import org.agmip.translators.apsim.core.Simulation;
import org.agmip.translators.apsim.core.Weather;
import org.agmip.translators.apsim.events.Event;
import org.agmip.translators.apsim.events.Planting;
import org.agmip.translators.apsim.util.Util;
import org.agmip.util.MapUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 * @author Dean Holzworth, CSIRO
 * @author Chris Villalobos, AgMIP IT
 * @since Jul 13, 2012
 */

public class ApsimWriter implements TranslatorOutput {
    private static Logger LOG = LoggerFactory.getLogger(ApsimWriter.class);
    static final int BUFFER = 2048;
    static final HashMap<String, String> modSpecVars = defMpdSpecVars();
    private String apsimFileName = "AgMip.apsim";
    private final ArrayList<String> files = new ArrayList();

    private static HashMap defMpdSpecVars() {
        HashMap ret = new HashMap();
        ret.put("fl_lat", "soil");
//            ret.put("apsim_summerdate", "soil");
//            ret.put("apsim_winterdate", "soil");
        return ret;
    }

	// Convert a JSON string into a collection of simulations
    public static ACE jsonToACE(String json) throws Exception{       
		ObjectMapper mapper = new ObjectMapper();
        ACE ace = mapper.readValue(json, ACE.class);
        ace.initialise();
        return ace;
    }	
	
	
    @SuppressWarnings({ "rawtypes" })
    @Override
    public void writeFile(String filePath, Map input) {

        try {
            adjustSpecVarLoc(input);
            ACE ace = jsonToACE(toJSON(input));
            File path = new File(filePath);

            // Check to see which files should be generated.
//            ArrayList<String> files = new ArrayList<String>();

            // Generate weather files
            generateMetFiles(path, input, ace, files);

            // Generate apsim file
            if (ace.getSoils().size() > 0 || ace.getExperiments().size() > 0) {
                generateAPSIMFile(apsimFileName, path, ace, files);
                if (isPaddyApplied(ace)) {
                    generateBatchFile(new String[]{"77"}, path, ace, files);
                } else {
                    generateBatchFile(new String[]{"74", "75", "77"}, path, ace, files);
                }
            }

        } catch (Exception e) {
//			e.printStackTrace();
            LOG.error(Functions.getStackTrace(e));
        }

    }

    public static void generateMetFiles(File path, Map input, ACE ace, ArrayList<String> files) throws Exception {

        WeatherAdjustAdaptor wthAdjAdp = new WeatherAdjustAdaptor(input);
        if (wthAdjAdp.hasAdjustments()) {
            Collection<Weather> weathers = ace.getWeathers();
            for (Weather weather : weathers) {
                String wstId = weather.getId();
                Collection<Weather> tmp = new Vector<Weather>();
                Iterator<WeatherAdjustment> it = wthAdjAdp.getIterator(wstId);
                if (it.hasNext()) {
                    while (it.hasNext()) {
                        WeatherAdjustment wthAdj = it.next();
                        ObjectMapper mapper = new ObjectMapper();
                        Weather w = mapper.readValue(wthAdj.getAdjustedWthJson(), Weather.class);
                        tmp.add(w);
                        generateMetFiles(path, tmp, files);
                        tmp.clear();
                    }
                } else {
                    tmp.add(weather);
                    generateMetFiles(path, tmp, files);
                    tmp.clear();
                }
            }

            Collection<Simulation> exps = ace.getExperiments();
            for (Simulation exp : exps) {
                String newWstId = wthAdjAdp.getAdjustedWstId(exp.getExperimentName());
                if (newWstId != null && !"".equals(newWstId)) {
                    exp.setWeatherID(newWstId);
                }
            }
        } else {
            if (!ace.getWeathers().isEmpty()) {
                generateMetFiles(path, ace.getWeathers(), files);
            }
        }
    }

    public static void generateMetFiles(File path, Collection<Weather> weathers, ArrayList<String> files) throws Exception {
        path.mkdirs();
        for(Weather weather:weathers){
            String fileName = weather.getId()+".met";
            File file = new File(path, fileName);
            if (files.contains(fileName)) {
                continue;
            }
            files.add(fileName);
            file.createNewFile();
            Velocity.init();
            VelocityContext context = new VelocityContext();
            context.put("weather", weather);
            FileWriter writer = new FileWriter(file);
            Reader R = new InputStreamReader(Util.class.getClassLoader().getResourceAsStream("template.met"));
            Velocity.evaluate(context, writer, "Generate Met", R);
            writer.close();
        }
    }


    public static void generateAPSIMFile(String fileNameToGenerate, File path, ACE ace, ArrayList<String> files)
            throws Exception {
        path.mkdirs();
        File file = new File(path, fileNameToGenerate);
        if (files.contains(fileNameToGenerate)) {
            return;
        }
        files.add(fileNameToGenerate);
        file.createNewFile();
        Velocity.init();
        VelocityContext context = new VelocityContext();
        FileWriter writer;
        try {
            context.put("collection", ace);
            writer = new FileWriter(file);
            Reader R = new InputStreamReader(Util.class.getClassLoader().getResourceAsStream("template.apsim"));
            Velocity.evaluate(context, writer, "Generate APSIM", R);
            writer.close();
        } catch (IOException ex) {
//            ex.printStackTrace();
            LOG.error(Functions.getStackTrace(ex));
        }
    }


    public static void generateBatchFile(String[] apsimVersions, File path, ACE ace, ArrayList<String> files)
            throws Exception {
        path.mkdirs();
        String crop = null; // TODO to support multiple crops included in the ACE data set, here need to be an array
        String model;
        for (Simulation sim : ace.getExperiments()) {
            for (Event e : sim.getManagement().getEvents()) {
                if (e instanceof Planting) {
                    crop =((Planting) e).getCropName().toLowerCase();
                    break;
                }
            }
            if (crop != null || !crop.equals("")) {
                break;
            }
        }
        if (crop == null) {
            crop = "unknow";
            model = "unknow";
        } else if (crop.equals("rice")) {
            model = "oryza";
        } else if (crop.equals("cotton")) {
            model ="ozcot";
        } else {
            model = crop;
        }
        
        String[] apsimDir = new String[2];
        String apsimExe;
        for (int i = 0; i < apsimVersions.length; i++) {
            if (apsimVersions[i].equals("74")) {
                apsimDir[0] = "C:\\Program Files (x86)\\Apsim74-r2286\\Model\\";
                apsimDir[1] = "C:\\Program Files\\Apsim74-r2286\\Model\\";
                apsimExe = "ApsimRun";
            } else if (apsimVersions[i].equals("75")) {
                apsimDir[0] = "C:\\Program Files (x86)\\Apsim75-r3008\\Model\\";
                apsimDir[1] = "C:\\Program Files\\Apsim75-r3008\\Model\\";
                apsimExe = "Apsim";
            } else if (apsimVersions[i].equals("77")) {
                apsimDir[0] = "C:\\Program Files (x86)\\Apsim77-r3615\\Model\\";
                apsimDir[1] = "C:\\Program Files\\Apsim77-r3615\\Model\\";
                apsimExe = "Apsim";
            } else {
                apsimDir[0] = "C:\\Program Files (x86)\\Apsim" + apsimVersions[i] + "\\Model\\";
                apsimDir[1] = "C:\\Program Files\\Apsim" + apsimVersions[i] + "\\Model\\";
                apsimExe = "Apsim";
            }
            File file = new File(path, "runApsim" + apsimVersions[i] + ".bat");
            if (files.contains("runApsim" + apsimVersions[i] + ".bat")) {
                return;
            }
            files.add("runApsim" + apsimVersions[i] + ".bat");
            file.createNewFile();
            Velocity.init();
            VelocityContext context = new VelocityContext();
            FileWriter writer;
            try {
                context.put("crop", crop);
                context.put("model", model);
                context.put("apsimExe", apsimExe);
                context.put("apsimDirs", apsimDir);
                writer = new FileWriter(file);
                Reader R = new InputStreamReader(Util.class.getClassLoader().getResourceAsStream("template.batch"));
                Velocity.evaluate(context, writer, "Generate RunBatch", R);
                writer.close();
            } catch (IOException ex) {
//                ex.printStackTrace();
                LOG.error(Functions.getStackTrace(ex));
            }
        }
    }
    
    protected static void adjustSpecVarLoc(Map input) {
        ArrayList<HashMap> exps = MapUtil.getObjectOr(input, "experiments", new ArrayList<HashMap>());
        ArrayList<HashMap> soils = MapUtil.getObjectOr(input, "soils", new ArrayList<HashMap>());
        ArrayList<HashMap> wths = MapUtil.getObjectOr(input, "weathers", new ArrayList<HashMap>());
        HashSet<String> register = new HashSet();
        for (HashMap exp : exps) {
            for (String var : modSpecVars.keySet()) {
                String path = modSpecVars.get(var);
                String val = MapUtil.getValueOr(exp, var, "");
                if (!val.equals("")) {
                    ArrayList<HashMap> arr;
                    String idName;
                    String idVal;
                    if (path.startsWith("soil")) {
                        idName = "soil_id";
                        idVal = MapUtil.getValueOr(exp, "soil_id", "");
                        arr = soils;
                    } else {
                        idName = "weather";
                        idVal = MapUtil.getValueOr(exp, "wst_id", "");
                        arr = wths;
                    }
                    if (!register.contains(var + "_" + idVal)) {
                        for (HashMap data : arr) {
                            if (MapUtil.getValueOr(data, idName, "").equals(idVal)) {
                                data.put(var, val);
                                register.add(var + "_" + idVal);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected static boolean isPaddyApplied(ACE ace) {
        boolean ret = false;
        for (Simulation exp : ace.getExperiments()) {
            if (exp.getManagement().isPaddyApplied()) {
                ret = true;
                break;
            }
        }
        return ret;
    }
    
    protected void setApsimFileName(String apsimfileName) {
        if (!apsimfileName.toLowerCase().endsWith(".apsim")) {
            apsimfileName += ".apsim";
        }
        this.apsimFileName = apsimfileName;
    }
}
