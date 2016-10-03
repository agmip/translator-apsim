package org.agmip.translators.apsim;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.agmip.common.Functions;
import org.agmip.core.types.DividableOutputTranslator;
import static org.agmip.translators.apsim.ApsimWriter.jsonToACE;

import org.agmip.translators.apsim.core.ACE;
import org.agmip.translators.apsim.core.Simulation;
import org.agmip.translators.apsim.events.Event;
import org.agmip.translators.apsim.events.Planting;
import org.agmip.translators.apsim.util.Util;
import static org.agmip.util.JSONAdapter.toJSON;
import org.agmip.util.MapUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Meng Zhang, AgMIP IT
 * @since Jun 14, 2016
 */
public class ApsimWriterDiv extends ApsimWriter implements DividableOutputTranslator {

    private static final Logger LOG = LoggerFactory.getLogger(ApsimWriterDiv.class);

    @SuppressWarnings({"rawtypes"})
    @Override
    public void writeFile(String filePath, Map input) {
        writeFile(filePath, input, 1);
    }
    
    @Override
    public void writeFile(String filePath, Map input, int size) {

        try {
//            ArrayList<Map> dataArr = divideData(input);
            ArrayList<String> files = new ArrayList();
//            PartitionBuilder pb = new PartitionBuilder(toJSON(input).getBytes());
//            Iterator<byte[]> it = pb.iterator();
            MapPartitionBuilder pb = new MapPartitionBuilder(input, size);
            Iterator<Map> it = pb.iterator();
            while (it.hasNext()) {
//            for (Map data : dataArr) {
//                String json = new String(it.next());
//                HashMap data = JSONAdapter.fromJSON(json);
                Map data = it.next();
                String fileName = getFirstExname(data);
                files.add(fileName);
                super.setApsimFileName(fileName);
                super.writeFile(filePath, data);
            }
            ACE ace = jsonToACE(toJSON(input));
            File path = new File(filePath);
            if (isPaddyApplied(ace)) {
                generateBatchFile(new String[]{"77"}, path, ace, files);
            } else {
                generateBatchFile(new String[]{"74", "75", "77"}, path, ace, files);
            }
        } catch (Exception e) {
            LOG.error(Functions.getStackTrace(e));
        }

    }
    
    // TODO
//    protected static ArrayList<Map> divideData(Map data) {
//        ArrayList<Map> ret = new ArrayList();
//        ArrayList<Map> exps = MapUtil.getObjectOr(data, "experiments", new ArrayList());
//        ArrayList<Map> wths = MapUtil.getObjectOr(data, "weathers", new ArrayList());
//        ArrayList<Map> soils = MapUtil.getObjectOr(data, "soils", new ArrayList());
//        HashMap<String, Integer> expLookup = new HashMap();
//        HashMap<String, Integer> wthLookup = new HashMap();
//        HashMap<String, Integer> soilLookup = new HashMap();
//        
//        // Scan weather
//        for (int i = 0; i < wths.size(); i++) {
//            String wstId = MapUtil.getValueOr(wths.get(i), "wst_id", "");
//            String climId = MapUtil.getValueOr(wths.get(i), "clim_id", "");
//            wthLookup.put(wstId, i);
//            wthLookup.put(wstId+climId, i);
//        }
//        
//        // Scan soil
//        for (int i = 0; i < soils.size(); i++) {
//            String siolId = MapUtil.getValueOr(soils.get(i), "soil_id", "");
//            soilLookup.put(siolId, i);
//        }
//        
//        // Scan experiment
//        for (int i = 0; i < exps.size(); i++) {
//            Map exp = exps.get(i);
//            String exname = getFirstExname(exp);
//            String wstId = MapUtil.getValueOr(exp, "wst_id", "");
//            String climId = MapUtil.getValueOr(exp, "clim_id", "");
//            String soilId = MapUtil.getValueOr(exp, "soil_id", "");
//            if (wstId.endsWith(climId)) {
//                climId = "";
//            }
//            
//            Map m;
//            if (expLookup.containsKey(exname)) {
//                m = ret.get(expLookup.get(exname));
//            } else {
//                m = new HashMap();
//                m.put("experiments", new ArrayList());
//                m.put("soils", new HashSet());
//                m.put("weathers", new HashSet());
//                ret.add(m);
//                expLookup.put(exname, ret.size() - 1);
//            }
//            
//            MapUtil.getObjectOr(m, "experiments", new ArrayList()).add(exp);
//            if (wthLookup.containsKey(wstId + climId)) {
//                MapUtil.getObjectOr(m, "weathers", new HashSet()).add(wths.get(wthLookup.get(wstId + climId)));
//            }
//            if (soilLookup.containsKey(soilId)) {
//                MapUtil.getObjectOr(m, "soils", new HashSet()).add(soils.get(soilLookup.get(soilId)));
//            }
//        }
//        
//        // reorganize weather and soil data structure
//        for (Map m : ret) {
//            m.put("weathers", new ArrayList(MapUtil.getObjectOr(m, "weathers", new HashSet())));
//            m.put("soils", new ArrayList(MapUtil.getObjectOr(m, "soils", new HashSet())));
//        }
//        return ret;
//    }
    
    private class MapPartitionBuilder implements Iterable<Map> {
        
        private final ArrayList<Map> exps;
        private final ArrayList<Map> wths;
        private final ArrayList<Map> soils;
        private final HashMap<String, Integer> wthLookup = new HashMap();
        private final HashMap<String, Integer> soilLookup = new HashMap();
        private final int size;
        
        public MapPartitionBuilder(Map data) {
            this(data, 1);
        }
        
        public MapPartitionBuilder(Map data, int size) {
            this.size = size;
            exps = MapUtil.getObjectOr(data, "experiments", new ArrayList());
            wths = MapUtil.getObjectOr(data, "weathers", new ArrayList());
            soils = MapUtil.getObjectOr(data, "soils", new ArrayList());
            
            // Scan weather
            for (int i = 0; i < wths.size(); i++) {
                String wstId = MapUtil.getValueOr(wths.get(i), "wst_id", "");
                String climId = MapUtil.getValueOr(wths.get(i), "clim_id", "");
                wthLookup.put(wstId, i);
                wthLookup.put(wstId+climId, i);
            }

            // Scan soil
            for (int i = 0; i < soils.size(); i++) {
                String siolId = MapUtil.getValueOr(soils.get(i), "soil_id", "");
                soilLookup.put(siolId, i);
            }
        }

        @Override
        public Iterator<Map> iterator() {
            return new MapPartitionIterator(size);
        }
        
        private class MapPartitionIterator implements Iterator<Map> {
            
            private int idx = 0;
            private int size;
            
            public MapPartitionIterator(int size) {
                this.size = size;
            }

            @Override
            public boolean hasNext() {
                return idx < exps.size();
            }

            @Override
            public Map next() {
                HashMap ret = new HashMap();
                if (size <= 0) {
                    return new HashMap();
                }
                int last = idx + size;
                if (last > exps.size()) {
                    last = exps.size();
                }
                List<Map> subExps = new ArrayList(exps.subList(idx, last));
                idx = last;
                ret.put("experiments", subExps);
                
                HashSet<String> wthNames = new HashSet();
                HashSet<String> soilNames = new HashSet();
                for (Map exp : subExps) {
                    String wstId = MapUtil.getValueOr(exp, "wst_id", "");
                    String climId = MapUtil.getValueOr(exp, "clim_id", "");
                    String soilId = MapUtil.getValueOr(exp, "soil_id", "");
                    if (wstId.endsWith(climId)) {
                        climId = "";
                    }
                    wthNames.add(wstId + climId);
                    soilNames.add(soilId);
                }
                
                List<Map> subWths = new ArrayList();
                for (String wthName : wthNames) {
                    subWths.add(wths.get(wthLookup.get(wthName)));
                }
                ret.put("weathers", subWths);
                
                List<Map> subSoils = new ArrayList();
                for (String soilName : soilNames) {
                    subSoils.add(soils.get(soilLookup.get(soilName)));
                }
                ret.put("soils", subSoils);
                    
                return ret;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            
        }
    }
    
    protected static String getFirstExname(Map data) {
        
        ArrayList<Map> exps = MapUtil.getObjectOr(data, "experiments", new ArrayList());
        String exname;
        if (exps.isEmpty()) {
            exname = "";
        } else if (exps.size() > 1) {
            String exnameFirst = MapUtil.getValueOr(exps.get(0), "exname", "").replaceAll("(_+\\d+)+$", "");
            String exnameLast = MapUtil.getValueOr(exps.get(exps.size() - 1), "exname", "").replaceAll("(_+\\d+)+$", "");
            if (exnameFirst.equals(exnameLast)) {
                exname = exnameFirst;
            } else {
                exname = exnameFirst + "_" + exnameLast.substring(getCommonPart(exnameFirst, exnameLast).length());
            }
        } else {
            exname = MapUtil.getValueOr(exps.get(0), "exname", "");
        }
//        if (exname.matches(".+(_+\\d+)+$")) {
//            exname = exname.replaceAll("(_+\\d+)+$", "");
//        }
        return exname;
    }
    
    private static String getCommonPart(String str1, String str2) {
        int len = Math.min(str1.length(), str2.length());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char c1 = str1.charAt(i);
            char c2 = str2.charAt(i);
            if (c1 != c2 || (c1 >= '0' && c1 <= '9')) {
                break;
            } else {
                sb.append(c1);
            }
        }
        return sb.toString();
    }
    
    // TODO
    public static void generateBatchFile(String[] apsimVersions, File path, ACE ace, ArrayList<String> apsimFiles) throws Exception {
        
        if (apsimFiles.isEmpty()) {
            return;
        }
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
            if (crop != null || !"".equals(crop)) {
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
        for (String apsimVersion : apsimVersions) {
            if (apsimVersion.equals("74")) {
                apsimDir[0] = "C:\\Program Files (x86)\\Apsim74-r2286\\Model\\";
                apsimDir[1] = "C:\\Program Files\\Apsim74-r2286\\Model\\";
                apsimExe = "ApsimRun";
            } else if (apsimVersion.equals("75")) {
                apsimDir[0] = "C:\\Program Files (x86)\\Apsim75-r3008\\Model\\";
                apsimDir[1] = "C:\\Program Files\\Apsim75-r3008\\Model\\";
                apsimExe = "Apsim";
            } else if (apsimVersion.equals("77")) {
                apsimDir[0] = "C:\\Program Files (x86)\\Apsim77-r3615\\Model\\";
                apsimDir[1] = "C:\\Program Files\\Apsim77-r3615\\Model\\";
                apsimExe = "Apsim";
            } else {
                apsimDir[0] = "C:\\Program Files (x86)\\Apsim" + apsimVersion + "\\Model\\";
                apsimDir[1] = "C:\\Program Files\\Apsim" + apsimVersion + "\\Model\\";
                apsimExe = "Apsim";
            }
            File file = new File(path, "runApsim" + apsimVersion + ".bat");
            file.createNewFile();
            Velocity.init();
            VelocityContext context = new VelocityContext();
            FileWriter writer;
            try {
                context.put("crop", crop);
                context.put("model", model);
                context.put("apsimExe", apsimExe);
                context.put("apsimDirs", apsimDir);
                context.put("apsimFiles", apsimFiles);
                writer = new FileWriter(file);
                Reader R = new InputStreamReader(Util.class.getClassLoader().getResourceAsStream("template_div.batch"));
                Velocity.evaluate(context, writer, "Generate RunBatch", R);
                writer.close();
            } catch (IOException ex) {
                LOG.error(Functions.getStackTrace(ex));
            }
        }
    }
}
