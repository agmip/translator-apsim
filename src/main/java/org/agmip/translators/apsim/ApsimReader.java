package org.agmip.translators.apsim;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.agmip.core.types.TranslatorInput;
import org.agmip.translators.apsim.core.Weather;
import org.agmip.translators.apsim.readers.WeatherReader;
import org.agmip.util.JSONAdapter;
import org.codehaus.jackson.map.ObjectMapper;

public class ApsimReader implements TranslatorInput {

    @SuppressWarnings("rawtypes")
    @Override
    public Map readFile(String fileName) throws Exception {
        HashMap<String, ArrayList> ret = new HashMap();
        WeatherReader reader = new WeatherReader();
        if (fileName.toLowerCase().endsWith(".zip")) {
            ZipFile zf = new ZipFile(fileName);
            Enumeration<? extends ZipEntry> e = zf.entries();
            while (e.hasMoreElements()) {
                ZipEntry ze = (ZipEntry) e.nextElement();
                if (ze.getName().toLowerCase().endsWith(".met")) {
                    Weather wth = reader.read(zf.getInputStream(ze), ze.getName());
                    saveWth(ret, wth);
                }
            }
            zf.close();
        } else if (fileName.toLowerCase().endsWith(".met")) {
            Weather wth = reader.read(fileName);
            saveWth(ret, wth);
        } else {
        }

        return ret;
    }

    private void saveWth(HashMap<String, ArrayList> ret, Weather wth) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        byte[] jsonBytes = mapper.writeValueAsBytes(wth);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean valStart = false;
        boolean nonStrVal = false;
        for (byte b : jsonBytes) {
            if (b == ':') {
                valStart = true;
                nonStrVal = false;
            } else if (valStart) {
                if (b!= '{' && b!= '[' && b!= '"') {
                    out.write('"');
                    nonStrVal = true;
                }
                valStart = false;
            } else if (nonStrVal && (b == ',' || b == ']' || b == '}')) {
                out.write('"');
                nonStrVal = false;
            }
            out.write(b);
        }
        String jsonStr = out.toString();

        HashMap wthData = JSONAdapter.fromJSON(jsonStr);
        if (ret.containsKey("weathers")) {
            ret.get("weathers").add(wthData);
        } else {
            ArrayList wthArr = new ArrayList();
            wthArr.add(wthData);
            ret.put("weathers", wthArr);
        }
    }
}
