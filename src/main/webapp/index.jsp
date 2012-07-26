<html>
<head>
  <title>uploader</title>
</head>

<body>

<script type="text/javascript" src="jquery-1.6.4.min.js"></script>
<script>
$(document).ready(function() {
  // defaults
  $("#identifier-input").val("Polyfun");
  $("#emulator-text").val(JSON.stringify({"inputDescriptions":[{"identifier":"A","dataType":"Numeric","encodingType":"double"},{"identifier":"B","dataType":"Numeric","encodingType":"double"}],"outputDescriptions":[{"identifier":"Result","dataType":"Numeric","encodingType":"double"}],"trainingDesign":[{"points":[0.1148106047143721,0.0750247694973726,0.1703564565616099,0.8108231852778648,0.34002233642004875,0.7509809251191987,0.08869462354944105,0.8944519261291851,0.37157559065327206,0.09001822306850839],"inputIdentifier":"A"},{"points":[0.27887620228987314,0.36695490073558856,0.4486127824911296,0.6244377461881803,0.4222161755681176,0.9525594646756638,0.6898518173711393,0.6852427131350797,0.08679699260212582,0.7965702537946984],"inputIdentifier":"B"}],"trainingEvaluationResult":[{"outputIdentifier":"Result","results":[0.4222037503467385,0.3597302076659835,0.7123227982992633,2.822392054698169,1.1983335081715136,3.160312309100783,0.7419794005785869,3.1529133542922807,1.1222604898845896,0.9045788384360753]}],"covFunctionName":"covSEiso","covFunctionParam":[0.7419,1.4351],"meanFunctionName":"mean_poly","meanFunctionParam":[1]}));
	
  // check if any emulators
  refreshEmulatorList();
	
  $("#upload-form").submit(function(e) {
    e.preventDefault();
    
    $("#upload-status").html("");
    
    var emulator = JSON.parse($("#emulator-text").val());
    var request = createUploadEmulatorRequest($("#identifier-input").val(), emulator);
    
    $.post("service/json", JSON.stringify(request), function(data) {
      $("#upload-status").html(JSON.stringify(data));
      refreshEmulatorList();
    });
  });
});

$("#process-select").change(function() {
	createInputForm($("#process-select").val());
});

var createUploadEmulatorRequest = function(identifier, emulator) {
  return { "UploadEmulatorRequest":
    { "Identifier": identifier,
      "Emulator": emulator } };
};

var refreshEmulatorList = function(url) {
	$("#process-select").children().remove();
	$.get("service?jsondesc", function(data) {
		for (var i in data.processes) {
			var process = data.processes[i];
			$("#process-select").append("<option value=\"" + process.identifier + "\">" + process.identifier + "</option>");
		}
	});
};

var createInputForm = function(identifier) {
  $.get("service?jsondesc", function(data) {
    // remove existing
    $("#use-form").children().remove();
    
    // get the process we're interested in
    for (var i in data.processes) {
      var process = data.processes[i];
      if (processes.identifier === identifier) {
        // set form
        for (var j in process.inputs) {
        	var input = process.inputs[j];
        	var id = input.identifier.toLowerCase() + "-input";
        	$("#use-form").append("<label for=\"" + id + "\">" + input.identifier + "</label>");
        	$("#use-form").append("<input id=\"" + id + "\" type=\"text\">" + input.identifier + "</label>");
        }
      
        // done with loop
        break;
      }
    }
  });
};
</script>

<form id="upload-form">
  <label for="identifier-input">Identifier</label>
  <input id="identifier-input" type="text"></input>
  
  <label for="emulator-text">Emulator</label>
  <textarea id="emulator-text" name="emulator"></textarea>
  
  <input type="submit" />
  
  <span id="upload-status"></span>
</form>

<form id="select-form">
  <label for="process-select">Process</label>
  <select id="process-select">  
  </select>
</form>

<form id="use-form">

</form>

</body>

</html>