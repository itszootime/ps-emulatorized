var ENDPOINT = 'service';
var JSON_ENDPOINT = ENDPOINT + '/json';

var processes;

$(function() {
	function roundNumber(number, digits) {
        var multiple = Math.pow(10, digits);
        var rndedNum = Math.round(number * multiple) / multiple;
        return rndedNum;
    }
	
	function generateForm() {
		// clear previous
		hideAlert();
		
		// create new params
		$('#params').slideUp('fast', function() {
			// add controls
			for (var i = 0; i < processes.length; i++) {
				var process = processes[i];
				$('#params .control-group').remove();
				var $actions = $('#params .form-actions');
				var selectedIdentifier = $('#select select option:selected').val();
				if (process.identifier == selectedIdentifier) {
					// add inputs
					var inputs = process.inputs;
					for (var j = 0; j < inputs.length; j++) {
						var input = inputs[j];
						var min = parseFloat(input['minimum-value']);
						var max = parseFloat(input['maximum-value']);
						var value;
						var detail = '';
						if (!isNaN(min) && !isNaN(max)) {
							detail += 'Min = ' + min + ', max = ' + max + '. ';
							value = min + ((max - min) / 2);
						}
						if (input.identifier.match(/Samples$/)) {
							value = 10;
						}
						detail += input.description;
						var vars = {
							label: input.identifier,
							name: input.identifier,
							detail: detail,
							uom: input.uom,
							value: value
						};
						$actions.before($(Mustache.to_html($('.template.form-input').val(), vars)));
					}
					
					// show form
					$('#params').slideDown();
					
					break;
				}
			}
		});
	}
	
	$.get(ENDPOINT + '?jsondesc', function(data) {
		processes = data.processes;
		$select = $('#select select');
		for (var i = 0; i < processes.length; i++) {
			var identifier = processes[i].identifier;
			if (identifier.match(/^Upload/) == null) {
				$select.append('<option value="' + identifier + '">' + identifier + '</option>');
			}
		}
		$('#select .busy').fadeOut('fast');
		$select.prop('disabled', false);
		generateForm();
	});
	
	$('#select select').on('change', function() {
		generateForm();
	});
	
	$('#params form').on('submit', function(event) {
		// stop form submitting
		event.preventDefault();
		
		// get rid of alert
		hideAlert();
		
		// execute
		var identifier = $('#select select option:selected').val();
		var array = $('#params form').serializeArray();
		var obj = {};
		var base = {};
		base[identifier + 'Request'] = obj;
		for (var i = 0; i < array.length; i++) {
			obj[array[i].name] = [ array[i].value ];
		}
		
		// loading
		$('#select select').prop('disabled', true);
		$('#params input').prop('disabled', true);
		$('#params button').prop('disabled', true);
		$('#params .busy').fadeIn('fast');
		
		$.ajax(JSON_ENDPOINT, {
			type: 'POST',
	        contentType: 'text/json',
	        processData: false,
	        data: JSON.stringify(base),
	        complete: function() {
	        	// all done
	        	$('#select select').prop('disabled', false);
	        	$('#params input').prop('disabled', false);
	        	$('#params button').prop('disabled', false);
	        	$('#params .busy').fadeOut('fast');
	        	
	        	// scroll
	        	$('html, body').animate({ scrollTop: 0 }, 600);
	        },
	        success: function(data) {
	        	if ('ProcessException' in data) {
	        		showAlert('error', data.ProcessException.message);
	        	} else {
		        	var result = data[identifier + 'Response'];
		        	
		        	// results
		        	var mean;
		        	var variance;
		        	var samples;
		        	for (var key in result) {
		        		if (key.match(/Mean$/)) {
		        			mean = result[key][0];
		        		} else if (key.match(/Variance$/)) {
		        			variance = result[key][0];
		        		} else if (key.match(/Samples$/)) {
		        			samples = result[key][0];
		        		}
		        	}
		        	
		        	// check process type
		        	if (identifier.match(/Samples$/)) {
		        		roundSamples = [];
		        		for (var i = 0; i < samples.length; i++) {
		        			roundSamples.push(roundNumber(samples[i], 3));
		        		}
		        		showAlert('success', 'Samples are ' + roundSamples.join(', ') + '.');
		        	} else {
		        		showAlert('success', 'Mean is ' + roundNumber(mean, 3) + ', variance is ' + roundNumber(variance, 3) + '.');
		        	}
	        	}
	        },
	        error: function(jqXHR, textStatus, errorThrown) {
	        	showAlert('error', textStatus);
	        }
		});
	});
});