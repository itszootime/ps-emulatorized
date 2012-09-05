package org.uncertweb.ps.process;

import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.uncertweb.et.emulator.Emulator;
import org.uncertweb.et.json.JSON;
import org.uncertweb.ps.Config;
import org.uncertweb.ps.processes.RunEmulator;
import org.uncertweb.ps.processes.RunEquation;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * Special edition process repository, with emulator support!
 * 
 * @author Richard Jones
 *
 */
// FIXME: should extend, not overwrite
public class ProcessRepository {

	private static Map<String, AbstractProcess> processes;
	private static ProcessRepository instance;
	private static final Logger logger = Logger.getLogger(ProcessRepository.class);
	
	private ProcessRepository() {
		processes = new LinkedHashMap<String, AbstractProcess>();
		
		// load from config
		List<String> processClasses = Config.getInstance().getProcessClasses();
		for (String c : processClasses) {
			try {
				addProcess((AbstractProcess) Class.forName(c).newInstance());
				logger.info("Loaded process class " + c + ".");
			}
			catch (ClassNotFoundException e) {
				logger.error("Couldn't find process class " + c + ", skipping.");
			}
			catch (InstantiationException e) {
				logger.error("Couldn't instantiate process class " + c + ", skipping.");
			}
			catch (IllegalAccessException e) {
				logger.error("Couldn't access process class " + c + ", skipping.");
			}
			catch (ClassCastException e) {
				logger.error("Process class " + c + " does not extend org.uncertweb.ps.process.AbstractProcess, skipping.");
			}
		}
		
		// FIXME: integrate this transactional process business with main framework
		// load equations
		try {
			DBCollection coll = getEquationsCollection();
			DBCursor cur = coll.find();
			while (cur.hasNext()) {
				DBObject obj = cur.next();
				String identifier = (String)obj.get("identifier");
				RunEquation process = createEquationProcess(identifier, (String)obj.get("expression"));
				addProcess(process);
				logger.info("Loaded stored equation '" + identifier + "'.");
			}
		}
		catch (UnknownHostException e) {
			// ignore?
		}
		
		// load emulators
		try {
			DBCollection coll = getEmulatorsCollection();
			DBCursor cur = coll.find();
			while (cur.hasNext()) {
				DBObject obj = cur.next();
				JSON json = new JSON();
				String identifier = (String)obj.get("identifier");
				Emulator emulator = json.parse(new StringReader((String) obj.get("emulator")), Emulator.class);
				RunEmulator process = new RunEmulator(identifier, emulator);
				addProcess(process);
				logger.info("Loaded stored emulator '" + identifier + "'.");
			}
		}
		catch (UnknownHostException e) {
			// ignore?
		}
	}
	public static ProcessRepository getInstance() {
		if (instance == null) {
			instance = new ProcessRepository();
		}
		return instance;
	}
	
	public void addProcess(AbstractProcess process) {
		String name = process.getIdentifier();
		processes.put(name, process);
	}
	
	private RunEquation createEquationProcess(String identifier, String expression) {
		// find functions in expression, they'll be ignored
		Pattern pattern = Pattern.compile("[a-zA-Z]+\\("); // \\b[a-zA-Z]+
		Matcher matcher = pattern.matcher(expression);
		List<Integer> ignoreList = new ArrayList<Integer>();
		while (matcher.find()) {
			ignoreList.add(matcher.start());
		}
		
		// find inputs in expression		
		pattern = Pattern.compile("\\b[a-zA-Z]+");
		matcher = pattern.matcher(expression);		
		String inputs = "";
		while (matcher.find()) {
			if (!ignoreList.contains(matcher.start())) {
				inputs += matcher.group() + ";";
			}
		}
		if (inputs.length() > 0) {
			inputs = inputs.substring(0, inputs.length() - 1);
		}
		
		// add
		RunEquation process = new RunEquation(identifier, inputs + "," + expression);		
		return process;
	}
	
	public void addEquationProcess(String identifier, String expression) throws Exception {
		if (processes.containsKey(identifier)) {
			// already exists
			throw new Exception("Equation with identifier '" + identifier + "' already exists, please try another.");
		}
		else {
			// add to db
			DBCollection coll = getEquationsCollection();
			BasicDBObject doc = new BasicDBObject();
			doc.put("identifier", identifier);
			doc.put("expression", expression);
			coll.insert(doc);				
			
			// add to instance
			RunEquation process = createEquationProcess(identifier, expression);
			addProcess(process);
		}				
	}
	
	// this could be done outside of the process repository, in UploadEmulator. as long as process serialization works correctly	
	public void addEmulatorProcess(String identifier, Emulator emulator) throws Exception {
		if (processes.containsKey(identifier)) {
			// already exists
			throw new Exception("Emulator with identifier '" + identifier + "' already exists, please try another.");
		}
		else {
			// add to db
			DBCollection coll = getEmulatorsCollection();
			BasicDBObject doc = new BasicDBObject();
			doc.put("identifier", identifier);
			JSON json = new JSON();
			StringBuilder sb = new StringBuilder();
			json.encode(emulator, sb);
			doc.put("emulator", sb.toString());
			coll.insert(doc);				
			
			// add to instance
			RunEmulator process = new RunEmulator(identifier, emulator);
			addProcess(process);
		}				
	}
	
	public AbstractProcess getProcess(String processIdentifier) {
		return processes.get(processIdentifier);
	}
	
	public List<AbstractProcess> getProcesses() {
		List<AbstractProcess> processList = new ArrayList<AbstractProcess>();
		processList.addAll(processes.values());
		return processList;
	}
	
	private DBCollection getEquationsCollection() throws UnknownHostException, MongoException {
		return getCollection("equations");
	}
	
	private DBCollection getEmulatorsCollection() throws UnknownHostException, MongoException {
		return getCollection("emulators");
	}
	
	private DBCollection getCollection(String name) throws UnknownHostException, MongoException {
		Mongo m = new Mongo();
		DB db = m.getDB("ps-emulatorized");
		return db.getCollection(name);
	}
	
}
