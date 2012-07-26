$(function() {	
  $('form').submit(function(e) {
    e.preventDefault();
        
    $identifier = $('input[name="identifier"]');
    $emulator = $('textarea[name="emulator"]');
    $btn = $('button');
    
    hideAlert();
    
    var emulator;
    try {
    	emulator = JSON.parse($emulator.val());
    } catch(e) {
    	showAlert('error', 'Couldn\'t parse emulator - are you sure it\'s valid JSON?');
    	return;
    }
    
    var request = { UploadEmulatorRequest:
    	{ Identifier: $identifier.val(),
          Emulator: emulator }
    };
    
    $.post('service/json', JSON.stringify(request))
    .success(function(data) {
      var message;
      var type;
      if ('UploadEmulatorResponse' in data) {
    	  message = data.UploadEmulatorResponse.Status;
    	  type = 'success';
    	  $identifier.val('');
    	  $emulator.val('');
      } else {
    	  message = data.ProcessException.message;
    	  type = 'error';
      }
      showAlert(type, message)
    })
    .error(function(jqXHR, textStatus, errorThrown) {
    	showAlert('error', textStatus);
    });
  });
});

function showAlert(type, message) {
	$('.alert').addClass('alert-' + type).html(message).slideDown('fast');
}

function hideAlert() {
	$('.alert').removeClass('alert-error alert-success').slideUp('fast');
}