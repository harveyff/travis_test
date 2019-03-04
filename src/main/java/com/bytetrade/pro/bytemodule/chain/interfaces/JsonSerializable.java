package com.bytetrade.pro.bytemodule.chain.interfaces;


import org.json.JSONArray;

import java.io.Serializable;

/**
 * Interface to be implemented by any entity for which makes sense to
 * have a JSON-formatted string and object representation.
 */
public interface JsonSerializable extends Serializable {

    String toJsonString();

    JSONArray toJsonObject();
}
