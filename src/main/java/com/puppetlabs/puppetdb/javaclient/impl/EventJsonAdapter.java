package com.puppetlabs.puppetdb.javaclient.impl;

import com.google.gson.*;
import com.puppetlabs.puppetdb.javaclient.model.Event;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nik Ogura on 2014-10-24.
 */
public class EventJsonAdapter implements JsonDeserializer<Event> {
	@Override
	public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		final JsonObject in = json.getAsJsonObject();
		GsonProvider.DateJsonAdapter dateAdapter = new GsonProvider.DateJsonAdapter();
		List<String> pathList = new ArrayList<String>();

		Event event = new Event();
		try {
			// String Fields
			event.setCertname(in.get("certname").getAsString());
			event.setReport(in.get("report").getAsString());
			event.setResourceType(in.get("resource-type").getAsString());
			event.setResourceTitle(in.get("resource-title").getAsString());

			event.setProperty(in.get("property").getAsString());

			event.setOldValue(in.get("old-value").getAsString());
			event.setMessage(in.get("message").getAsString());
			event.setFile(in.get("file").getAsString());
			event.setContainingClass(in.get("containing-class").getAsString());
			// //Date Fields
			event.setTimestamp(dateAdapter.deserialize(in.get("timestamp"), Date.class, context));
			event.setRunStartTime(dateAdapter.deserialize(in.get("run-start-time"), Date.class, context));
			event.setRunEndTime(dateAdapter.deserialize(in.get("run-end-time"), Date.class, context));
			event.setReportReceiveTime(dateAdapter.deserialize(in.get("report-receive-time"), Date.class, context));
			// //int Fields
			event.setLine(in.get("line").getAsInt());
		} catch (UnsupportedOperationException e1) {
			//Disregard, will get a JSON Null exception for empty fields
		}

		// containment-path
		for (JsonElement item : in.get("containment-path").getAsJsonArray()) {
			if (!item.isJsonNull())
				pathList.add(item.getAsString());
		}
		event.setContainmentPath(pathList);

		// status is an enum
		event.setStatus(Event.Status.valueOf(in.get("status").getAsString()));

		if (in.get("new-value").isJsonArray()) {
			StringBuilder sb = new StringBuilder();

			JsonArray newValues = in.get("new-value").getAsJsonArray();

			if (newValues.size() == 1) { // If it's a one element array, we'll
											// just return the only element as a
											// string.
				sb.append(newValues.get(0).getAsString());
			} else {
				for (int i = 0; i < newValues.size(); i++) {
					JsonElement e = newValues.get(i);

					if (!e.isJsonNull()) {
						sb.append(e.getAsString());
					}

					if (!(i == (newValues.size() - 1))) { // append a separator
															// if it's not the
															// last element
						sb.append(" "); // is separating things with a space
										// safe? will there be things with
										// embedded spaces? dunno.
					}
				}
			}

			event.setNewValue(sb.toString());

		} else {
			if (!in.get("new-value").isJsonNull()) {
				event.setNewValue(in.get("new-value").getAsString());
			}
		}

		return event;
	}
}