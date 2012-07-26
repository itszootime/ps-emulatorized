package org.uncertweb.ps.processes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.uncertweb.ps.data.DataDescription;
import org.uncertweb.ps.data.Metadata;
import org.uncertweb.ps.data.ProcessInputs;
import org.uncertweb.ps.data.ProcessOutputs;
import org.uncertweb.ps.data.SingleOutput;
import org.uncertweb.ps.process.AbstractProcess;
import org.uncertweb.ps.process.ProcessException;
import org.uncertweb.ps.process.ProcessRepository;

public class UploadEquation extends AbstractProcess {
	
	private final Logger logger = Logger.getLogger(UploadEquation.class);

	@Override
	public DataDescription getInputDataDescription(String arg0) {
		return new DataDescription(String.class);
	}

	@Override
	public List<String> getInputIdentifiers() {
		return Arrays.asList(new String[] { "Identifier", "Expression" });
	}

	@Override
	public List<Metadata> getInputMetadata(String arg0) {
		List<Metadata> metadata = new ArrayList<Metadata>();
		if (arg0.equals("Identifier")) {
			metadata.add(new Metadata("description", "The desired service identifier for the expression."));
		}
		else if (arg0.equals("Expression")) {
			metadata.add(new Metadata("description", "The JavaScript expression to upload."));
		}
		return metadata;
	}

	@Override
	public DataDescription getOutputDataDescription(String arg0) {
		return new DataDescription(String.class);
	}

	@Override
	public List<String> getOutputIdentifiers() {
		return Arrays.asList(new String[] { "Status" });
	}

	@Override
	public List<Metadata> getOutputMetadata(String arg0) {
		List<Metadata> metadata = new ArrayList<Metadata>();
		if (arg0.equals("Status")) {
			metadata.add(new Metadata("description", "The upload status."));
		}
		return metadata;
	}

	@Override
	public ProcessOutputs run(ProcessInputs arg0) throws ProcessException {
		// get inputs
		String identifier = arg0.get("Identifier").getAsSingleInput().getObjectAs(String.class);
		String expression = arg0.get("Expression").getAsSingleInput().getObjectAs(String.class);
		
		// TODO: make sure the identifier is valid for deployment
				
		// add		
		try {
			logger.info("Adding '" + identifier + "' expression to repository.");
			ProcessRepository.getInstance().addEquationProcess(identifier, expression);
		}
		catch (Exception e) {
			throw new ProcessException(e.getMessage());
		}
		
		// return
		ProcessOutputs outputs = new ProcessOutputs();
		outputs.add(new SingleOutput("Status", "'" + identifier + "' expression uploaded successfully."));
		return outputs;
	}

	@Override
	public List<Metadata> getMetadata() {
		List<Metadata> metadata = new ArrayList<Metadata>();
		metadata.add(new Metadata("description", "Upload a JavaScript expression to be exposed as a process on this service."));
		return metadata;
	}

}
