package com.dannyran.test.shared;

import com.google.gwt.user.client.rpc.CustomFieldSerializer;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

public class StoreLocation_CustomFieldSerializer extends CustomFieldSerializer<StoreLocation> {

	public static void deserialize(SerializationStreamReader streamReader, StoreLocation instance) {
	}

	public static StoreLocation instantiate(SerializationStreamReader streamReader) throws SerializationException {
		String city = streamReader.readString();
		String address = streamReader.readString();
		double latitude = streamReader.readDouble();
		double longitude = streamReader.readDouble();
		return new StoreLocation(city, address, latitude, longitude);
	}

	public static void serialize(SerializationStreamWriter streamWriter, StoreLocation instance) throws SerializationException {
		streamWriter.writeString(instance.getCity());
		streamWriter.writeString(instance.getAddress());
		streamWriter.writeDouble(instance.getLatitude());
		streamWriter.writeDouble(instance.getLongitude());
	}

	@Override
	public void deserializeInstance(SerializationStreamReader streamReader, StoreLocation instance) throws SerializationException {
		deserialize(streamReader, instance);
	}

	@Override
	public boolean hasCustomInstantiateInstance() {
		return true;
	}

	@Override
	public StoreLocation instantiateInstance(SerializationStreamReader streamReader) throws SerializationException {
		return instantiate(streamReader);
	}

	@Override
	public void serializeInstance(SerializationStreamWriter streamWriter, StoreLocation instance) throws SerializationException {
		serialize(streamWriter, instance);
	}

}
