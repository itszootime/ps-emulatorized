var ENDPOINT = 'service';
var JSON_ENDPOINT = ENDPOINT + '/json';

var processes;

$(function() {
	function roundNumber(number, digits) {
        var multiple = Math.pow(10, digits);
        var rndedNum = Math.round(number * multiple) / multiple;
        return rndedNum;
    }
	
	$.get(ENDPOINT + '?jsondesc', function(data) {
		processes = data.processes;
		for (var i = 0; i < processes.length; i++) {
			var identifier = processes[i].identifier;
			if (identifier.match(/^Upload/) == null) {
				$('#select select').append('<option value="' + identifier + '">' + identifier + '</option>');
			}
		}
	});
	
	$('#select button').on('click', function() {
		// hide
		$('#params').slideUp();
		
		// add controls
		for (var i = 0; i < processes.length; i++) {
			var process = processes[i];
			$('#params .control-group').remove();
			var $actions = $('#params .form-actions');
			if (process.identifier == $('#select select option:selected').val()) {
				// add inputs
				var inputs = process.inputs;
				for (var j = 0; j < inputs.length; j++) {
					var input = inputs[j];
					var min = parseFloat(input['minimum-value']);
					var max = parseFloat(input['maximum-value']);
					var vars = {
						label: input.identifier,
						name: input.identifier,
						detail: 'Min = ' + min + ', max = ' + max + '. ' + input.description,
						uom: input.uom,
						value: min + ((max - min) / 2)
					};
					$actions.before($(Mustache.to_html($('.template.form-input').val(), vars)));
				}
			}
		}
		
		// show
		$('#params').slideDown();
	});
	
	$('#params form').on('submit', function(event) {
		event.preventDefault();
		var identifier = $('#select select option:selected').val();
		var array = $('#params form').serializeArray();
		var obj = {};
		var base = {};
		base[identifier + 'Request'] = obj;
		for (var i = 0; i < array.length; i++) {
			obj[array[i].name] = [ array[i].value ];
		}
		$.ajax(JSON_ENDPOINT, {
			type: 'POST',
	        contentType: 'text/json',
	        processData: false,
	        data: JSON.stringify(base),
	        success: function(data) {
	        	var result = data[identifier + 'Response'];
	        	var mean;
	        	var covariance;
	        	for (var key in result) {
	        		if (key.match(/Mean$/)) {
	        			mean = result[key][0];
	        		} else if (key.match(/Covariance$/)) {
	        			covariance = result[key][0];
	        		}
	        	}
	        	alert('Mean is ' + roundNumber(mean, 3) + ', covariance is ' + roundNumber(covariance, 3) + '.');
	        }
		});
	});
});