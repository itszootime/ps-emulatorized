package org.uncertweb.ps.processes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.uncertweb.ps.data.DataDescription;
import org.uncertweb.ps.data.Metadata;
import org.uncertweb.ps.data.ProcessInputs;
import org.uncertweb.ps.data.ProcessOutputs;
import org.uncertweb.ps.data.SingleOutput;
import org.uncertweb.ps.process.AbstractProcess;
import org.uncertweb.ps.process.ProcessException;

/**
 * RunEmulator is a special process. It'll never be exposed by the name RunEmulator.
 * 
 * @author Richard Jones
 *
 */
public class RunEquation extends AbstractProcess {

	private final Logger logger = Logger.getLogger(RunEmulator.class);

	private Map<String, DataDescription> inputMap;
	private Map<String, DataDescription> outputMap;

	private String identifier;
	private String equation;

	public RunEquation(String identifier, String emulatorML) {
		// setup maps, treemap for ordering
		inputMap = new TreeMap<String, DataDescription>();
		outputMap = new TreeMap<String, DataDescription>();

		// set identifier
		this.identifier = identifier;

		// let's take our emulatorML as being csv
		String tokens[] = emulatorML.split(",");

		// inputs
		if (tokens[0].length() > 0) {
			String inputs[] = tokens[0].split(";");
			for (String input : inputs) {
				inputMap.put(input, new DataDescription(Double.class));
			}
		}

		// outputs
		outputMap.put("Result", new DataDescription(Double.class));
		/*
		String outputs[] = tokens[1].split(";");
		for (String output : outputs) {
			outputMap.put(output, new DataDescription(Double.class));
		}*/

		// equation
		equation = tokens[1];
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public DataDescription getInputDataDescription(String arg0) {
		return inputMap.get(arg0);
	}

	@Override
	public List<String> getInputIdentifiers() {
		return new ArrayList<String>(inputMap.keySet());
	}

	@Override
	public List<Metadata> getInputMetadata(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataDescription getOutputDataDescription(String arg0) {
		return outputMap.get(arg0);
	}

	@Override
	public List<String> getOutputIdentifiers() {
		return new ArrayList<String>(outputMap.keySet());
	}

	@Override
	public List<Metadata> getOutputMetadata(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessOutputs run(ProcessInputs arg0) throws ProcessException {
		// get values
		/*
		Pattern pattern = Pattern.compile("[\\+\\-\\*\\/]");
		Matcher matcher = pattern.matcher(equation);
		if (!matcher.find()) {
			// exception here
		}
		String op = matcher.group();
		String[] equationTokens = pattern.split(equation);
		double x = arg0.get(equationTokens[0]).getAsSingleInput().getObjectAs(Double.class);
		double y = arg0.get(equationTokens[1]).getAsSingleInput().getObjectAs(Double.class);
		 */

		// replace input values in expression
		String expression = equation;
		for (String input : inputMap.keySet()) {
			Double value = arg0.get(input).getAsSingleInput().getObjectAs(Double.class);
			expression = expression.replace(input, String.valueOf(value));
		}
		
		// functions should be called on Math object
		Pattern pattern = Pattern.compile("[a-zA-Z]+\\(");
		Matcher matcher = pattern.matcher(expression);
		while (matcher.find()) {
			String function = matcher.group();
			expression = expression.replace(function, "Math." + function);
		}

		// and evaluate using javascript engine
		logger.info("Evaluating expression " + expression + "...");
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			double result = (Double) engine.eval(expression);

			// create output
			ProcessOutputs outputs = new ProcessOutputs();
			outputs.add(new SingleOutput("Result", result));
			return outputs;
		}
		catch (ScriptException e) {
			throw new ProcessException("Couldn't evaluate expression.", e);
		}
	}

	@Override
	public List<Metadata> getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

}
