var ENDPOINT = 'service';
var JSON_ENDPOINT = ENDPOINT + '/json';

var processes;

$(function() {
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
		for (var i = 0; i < processes.length; i++) {
			var process = processes[i];
			var $fieldset = $('#params fieldset');
			if (process.identifier == $('#select select option:selected').val()) {
				// add inputs
				var inputs = process.inputs;
				for (var j = 0; j < inputs.length; j++) {
					var input = inputs[j];
					var vars = {
						label: input.identifier,
						name: input.identifier,
						detail: input.description,
						uom: input.uom,
						value: 0.5
					};
					$(Mustache.to_html($('.template.form-input').val(), vars)).prependTo($fieldset);
				}
				
				// show
				$('#params').slideDown();
			}
		}
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
	        	alert(JSON.stringify(data));
	        }
		});
	});
});