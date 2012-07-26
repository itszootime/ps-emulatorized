<div class="row">
	<div class="span7">
		<div id="select">
			<form class="well form-inline">
				<select></select>
				<button type="button" class="btn">Load</button>
			</form>					
		</div>
		
		<div id="params" class="hide">
			<form class="form-horizontal">
			  <fieldset>
				<div class="form-actions">
        			<button type="submit" class="btn btn-primary">Run!</button>
        		</div>
  			  </fieldset>
			</form>
		</div>
	</div>
</div>

<textarea class="template form-input">
<div class="control-group">
  <label class="control-label">{{label}}</label>
  <div class="controls">
    <input type="text" name="{{name}}" value="{{value}}"/>
    {{#help}}<p class="help-block">{{help}}</p>{{/help}}
  </div>
</div>
</textarea>