package org.uncertweb.ps.processes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.apache.log4j.Logger;
import org.uncertweb.et.design.Design;
import org.uncertweb.et.emulator.Emulator;
import org.uncertweb.et.emulator.EmulatorEvaluationResult;
import org.uncertweb.et.emulator.EmulatorEvaluator;
import org.uncertweb.et.emulator.EmulatorEvaluatorException;
import org.uncertweb.ps.data.DataDescription;
import org.uncertweb.ps.data.Input;
import org.uncertweb.ps.data.Metadata;
import org.uncertweb.ps.data.MultipleOutput;
import org.uncertweb.ps.data.ProcessInputs;
import org.uncertweb.ps.data.ProcessOutputs;
import org.uncertweb.ps.process.ProcessException;

public class RunEmulatorSamples extends RunEmulator {

	private final Logger logger = Logger.getLogger(RunEmulatorSamples.class);

	public RunEmulatorSamples(String identifier, Emulator emulator) {
		// this will take care of most of the work
		super(identifier + "Samples", emulator);
		
		// number of samples is also an input
		inputMap.put("NumSamples", new DataDescription(Integer.class, 0, 1));
		
		// outputs are now samples
		outputMap.clear();
		for (String outputIdentifier : emulator.getEvaluationResult().getOutputIdentifiers()) {
			outputMap.put(outputIdentifier + "Samples", new DataDescription(Double[].class, 0, Integer.MAX_VALUE));
		}
	}
	
	@Override
	public List<Metadata> getInputMetadata(String arg0) {
		if (arg0.equals("NumSamples")) {
			return Arrays.asList(new Metadata[] { new Metadata("description", "The number of samples to generate the emulator output distribution") });
		}
		else {
			return super.getInputMetadata(arg0);
		}
	}

	@Override
	public ProcessOutputs run(ProcessInputs inputs) throws ProcessException {
		// get inputs values
		Design design = null;
		int numSamples = 10;
		for (Input input : inputs) {
			if (input.getIdentifier().equals("NumSamples")) {
				numSamples = input.getAsSingleInput().getObjectAs(Integer.class);
			}
			else {
				Double[] points = input.getAsSingleInput().getObjectAs(Double[].class);
				if (design == null) {
					design = new Design(points.length);
				}
				design.addPoints(input.getIdentifier(), points);
			}
		}
		
		// run emulator
		logger.info("Running emulator for " + this.getIdentifier() + "...");
		try {
			// run emulator request
			EmulatorEvaluationResult result = EmulatorEvaluator.run(emulator, design);
			
			// create output
			ProcessOutputs outputs = new ProcessOutputs();
			
			// only supports 1d output at the moment
			String outputIdentifier = emulator.getOutputs().get(0).getIdentifier();
			Double[] predictedMean = result.getMeanResults(outputIdentifier);
			Double[] predictedVar = result.getCovarianceResults(outputIdentifier);
			
			// get samples
			List<Double[]> samples = new ArrayList<Double[]>();
			for (int i = 0; i < predictedMean.length; i++) {
				NormalDistributionImpl dist = new NormalDistributionImpl(predictedMean[i], Math.sqrt(predictedVar[i]));
				double[] distSamples = dist.sample(numSamples);
				samples.add(ArrayUtils.toObject(distSamples));				
			}
			outputs.add(new MultipleOutput(outputIdentifier + "Samples", samples));
			
			return outputs;
		}
		catch (EmulatorEvaluatorException e) {
			throw new ProcessException("Couldn't run emulator. " + e.getMessage(), e);
		}
		catch (MathException e) {
			throw new ProcessException("Couldn't sample emulator output distribution. " + e.getMessage(), e);
		}
	}

	@Override
	public List<Metadata> getMetadata() {
		return null;
	}

}
