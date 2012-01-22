package com.dannyran.test.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * A suggest Oracle for Places autocomplete requests<BR>
 * A requests is sent only a after an Interval of request inactivity
 */
public class PlacesSuggestOracle extends SuggestOracle {

	/**
	 * Request inactivity interval in ms
	 */
	private static final int INTERVAL = 500;

	private final DataServiceAsync dataService = GWT.create(DataService.class);
	private Request lastRequest;
	private Callback lastCallback;
	private final Timer timer;

	public PlacesSuggestOracle() {
		timer = new Timer() {

			@Override
			public void run() {
				querySuggestion(lastRequest, lastCallback);
			}
		};
	}

	@Override
	public void requestSuggestions(Request request, final Callback callback) {
		// save state
		lastRequest = request;
		lastCallback = callback;

		// cancel and shcedule for INTERVAL
		timer.cancel();
		timer.schedule(INTERVAL);
	}

	private void querySuggestion(final Request request, final Callback callback) {
		String query = request.getQuery();
		dataService.locationAutoComplete(query, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				// silently ignore
			}

			@Override
			public void onSuccess(String result) {
				// parse JSON object
				List<Suggestion> suggestions = new ArrayList<Suggestion>();
				JSONValue parseStrict = JSONParser.parseStrict(result);
				JSONObject object = parseStrict.isObject();
				JSONValue predictions = object.get("predictions");
				JSONArray array = predictions.isArray();
				for (int i = 0; i < array.size(); i++) {
					JSONValue jsonValue = array.get(i);
					String stringValue = jsonValue.isObject().get("description").isString().stringValue();
					suggestions.add(new MultiWordSuggestion(stringValue, stringValue));
				}
				Response response = new Response(suggestions);
				callback.onSuggestionsReady(request, response);
			}
		});
	}
}
