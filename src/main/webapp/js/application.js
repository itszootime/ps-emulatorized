$.fn.spin.defaults = {
  lines: 11, // The number of lines to draw
  length: 3, // The length of each line
  width: 2, // The line thickness
  radius: 4, // The radius of the inner circle
  rotate: 0, // The rotation offset
  color: '#000', // #rgb or #rrggbb
  speed: 1.2, // Rounds per second
  trail: 60, // Afterglow percentage
  shadow: false, // Whether to render a shadow
  hwaccel: false, // Whether to use hardware acceleration
  className: 'spinner', // The CSS class to assign to the spinner
  zIndex: 2e9, // The z-index (defaults to 2000000000)
  top: 'auto', // Top position relative to parent in px
  left: 'auto' // Left position relative to parent in px
};

function showAlert(type, message) {
	// animation that does nothing so addClass doesn't get called before
	// removeClass of hideAlert
	$('.alert').fadeOut(0, function() {
		$(this).addClass('alert-' + type).html(message).slideDown('fast');
	});
}

function hideAlert() {
	var alert = $('.alert');
	alert.slideUp('fast', function() {
		$(this).removeClass('alert-error alert-success');
	});
}

$(function() {
	$('.busy').each(function() {
		var $busy = $(this);
		var hidden = $busy.hasClass('hide');
		if (hidden) { $busy.removeClass('hide').hide(); }
		$busy.spin();
	});
});