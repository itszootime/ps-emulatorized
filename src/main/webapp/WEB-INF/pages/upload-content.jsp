<div class="row">
	<div class="span7">
		<div class="alert hide"></div>
		<form class="form-horizontal">
			<fieldset>
				<div class="control-group">
					<label class="control-label">Identifier</label>
					<div class="controls">
						<input type="text" name="identifier"/>
						<p class="help-block">The unique name for your emulator, e.g. 'YieldEstimation'.</p>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">Emulator</label>
					<div class="controls">
						<textarea name="emulator" class="span4"></textarea>
						<p class="help-block">The emulator as a JSON string.</p>
					</div>
				</div>
  			    <div class="form-actions">        		
			    	<button type="submit" class="btn btn-primary">Upload</button>
			    	<span class="busy hide"></span>
			  	</div>
			</fieldset>
		</form>
	</div>
</div>