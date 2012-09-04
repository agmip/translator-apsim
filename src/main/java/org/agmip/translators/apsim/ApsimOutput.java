package org.agmip.translators.apsim;

import static org.agmip.util.JSONAdapter.toJSON;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.agmip.core.types.TranslatorOutput;
import org.agmip.translators.apsim.core.SimulationRun;
import org.agmip.translators.apsim.util.Converter;
import org.agmip.util.MapUtil;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author Ioannis N. Athanasiadis, DUTh
 * @author Dean Holzworth, CSIRO
 * @author Chris Villalobos, CSIRO
 * @since Jul 13, 2012
 */

public class ApsimOutput implements TranslatorOutput {
	static final int BUFFER = 2048;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void writeFile(String filePath, Map input) {
		File path = new File(filePath);
		// if(!path.canWrite()) throw new Exception("Can't write at "
		// +filePath);
		ObjectMapper mapper = new ObjectMapper();
		SimulationRun sim;
		try {
                    String temp = toJSON(MapUtil.decompressAll(input));
                    System.out.println(temp);
                    sim = mapper.readValue(temp, SimulationRun.class);
                        
			sim.initialise();
			Converter.generateMetFile(path, sim);
			Converter.generateAPSIMFile(path, sim);

			BufferedInputStream origin = null;

			File zipfile = new File(path, sim.experimentName + "_apsim.zip");

			if (zipfile.exists())
				zipfile.delete();

			FileOutputStream dest = new FileOutputStream(zipfile);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					dest));
			byte data[] = new byte[BUFFER];
			// get a list of files from current directory

			String files[] = new String[] { sim.experimentName + ".met",
					sim.experimentName + ".apsim" };

			for (int i = 0; i < files.length; i++) {
				// System.out.println("Adding: " + files[i]);
				FileInputStream fi = new FileInputStream(new File(path,
						files[i]));
				origin = new BufferedInputStream(fi);
				ZipEntry entry = new ZipEntry(files[i]);
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();

			for (int i = 0; i < files.length; i++) {
				File f = new File(path, files[i]);
				f.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
