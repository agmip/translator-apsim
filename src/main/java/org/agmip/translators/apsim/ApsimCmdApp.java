package org.agmip.translators.apsim;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.agmip.util.JSONAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Meng Zhang
 */
public class ApsimCmdApp {

    private static boolean isCompressed = false;
    private static boolean isToModel = false;
    private static String inputPath = null;
    private static String outputPath = "APSIM";
    private static final Logger LOG = LoggerFactory.getLogger(ApsimCmdApp.class);

    public static void main(String... args) throws IOException {
        readCommand(args);
        if (isToModel) {
            LOG.info("Translate {} to APSIM...", new File(inputPath).getName());
            ApsimWriter translator = new ApsimWriter();
            translator.writeFile(outputPath, readJson());
            if (isCompressed) {
                createZip();
            }
            LOG.info("Job done!");
        } else {
            LOG.info("Translate {} to JSON...", new File(inputPath).getName());
            LOG.warn("It is not implemented yet!");
//            ApsimReader translator = new ApsimReader();
//            Map result = translator.readFile(inputPath);
//            BufferedOutputStream bo;
//            if (!outputPath.equals("")) {
//                outputPath = new File(outputPath).getPath() + File.separator;
//            }
//            File f = new File(outputPath + new File(outputPath).getName().replaceAll("\\.\\w+$", ".json"));
//            bo = new BufferedOutputStream(new FileOutputStream(f));
//
//            // Output json for reading
//            bo.write(JSONAdapter.toJSON(result).getBytes());
//            bo.flush();
//            bo.close();
        }
    }

    private static void readCommand(String[] args) {

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-zip")) {
                isCompressed = true;
            } else if (args[i].toUpperCase().endsWith(".JSON")) {
                isToModel = true;
                inputPath = args[i];
                LOG.info("Read from {}", inputPath);
            } else if (args[i].toUpperCase().endsWith(".ZIP")) {
                isToModel = false;
                inputPath = args[i];
                LOG.info("Read from {}", inputPath);
            } else {
                outputPath = args[i];
                if (outputPath != null && !"".equals(outputPath)) {
                    outputPath = new File(outputPath).getPath() + File.separator + "APSIM";
                } else {
                    outputPath = "APSIM";
                }
                LOG.info("Output to {}", outputPath);
            }
        }
    }

    private static Map readJson() throws IOException {
        return JSONAdapter.fromJSONFile(inputPath);
    }
    
    private static void createZip() throws FileNotFoundException, IOException {

        File[] files = new File(outputPath).listFiles();
        Calendar cal = Calendar.getInstance();
        File outputFile = new File(outputPath + File.separator + "AGMIP_APSIM_" + cal.getTimeInMillis() + ".zip");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFile));
        ZipEntry entry;
        BufferedInputStream bis;
        byte[] data = new byte[1024];
        LOG.info("Start zipping all the files...");
        
        // Check if there is file been created
        if (files == null || files.length == 0) {
            LOG.warn("No files here for zipping");
            return;
        }

        for (File file : files) {
            if (file == null) {
                continue;
            } else if (file.getName().toUpperCase().endsWith(".ZIP")) {
                continue;
            }

            if (outputPath != null) {
                entry = new ZipEntry(file.getPath().substring(new File(outputPath).getPath().length() + 1));
            } else {
                entry = new ZipEntry(file.getPath());
            }
            out.putNextEntry(entry);
            bis = new BufferedInputStream(new FileInputStream(file));

            int count;
            while ((count = bis.read(data)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
            file.delete();
        }

        out.close();
        LOG.info("End zipping");
    }
}
