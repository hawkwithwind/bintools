package com.wxipad.wechat.tools.json;

import java.util.HashMap;
import java.util.Set;

public class JSONObject {
    private final HashMap<String, Object> jsonData;
    public boolean noException;

    public JSONObject() {
        this.jsonData = new HashMap();
        this.noException = false;
    }

    public JSONObject(boolean noException) {
        this.jsonData = new HashMap();
        this.noException = noException;
    }

    public JSONObject(HashMap<String, Object> data) {
        this.jsonData = data;
        this.noException = false;
    }

    public JSONObject(HashMap<String, Object> data, boolean noException) {
        this.jsonData = data;
        this.noException = noException;
    }

    public JSONObject(String jsonStr)
            throws JSONException {
        JSONSolver tokener = new JSONSolver(jsonStr);
        this.jsonData = tokener.solveJSONObject(false);
        this.noException = false;
    }

    public JSONObject(String jsonStr, boolean noException)
            throws JSONException {
        HashMap<String, Object> data = null;
        try {
            JSONSolver tokener = new JSONSolver(jsonStr);
            data = tokener.solveJSONObject(noException);
        } catch (JSONException ex) {
            if (noException) {
                data = new HashMap();
            } else {
                throw ex;
            }
        } finally {
            this.jsonData = data;
            this.noException = noException;
        }
    }

    public Set<String> keySet() {
        return this.jsonData.keySet();
    }

    public boolean has(String key) {
        return this.jsonData.containsKey(key);
    }

    public Object remove(String key) {
        return this.jsonData.remove(key);
    }

    private JSONObject putObj(String key, Object obj) {
        if (this.jsonData.containsKey(key)) {
            this.jsonData.remove(key);
        }
        this.jsonData.put(key, obj);
        return this;
    }

    public JSONObject put(String key, Object jsonObj) {
        return putObj(key, jsonObj);
    }

    public JSONObject put(String key, JSONObject jsonObject) {
        return putObj(key, jsonObject);
    }

    public JSONObject put(String key, JSONArray jsonArray) {
        return putObj(key, jsonArray);
    }

    public JSONObject put(String key, boolean jsonBoolean) {
        return putObj(key, Boolean.valueOf(jsonBoolean));
    }

    public JSONObject put(String key, char jsonChar) {
        return putObj(key, Character.valueOf(jsonChar));
    }

    public JSONObject put(String key, byte jsonByte) {
        return putObj(key, Byte.valueOf(jsonByte));
    }

    public JSONObject put(String key, short jsonShort) {
        return putObj(key, Short.valueOf(jsonShort));
    }

    public JSONObject put(String key, int jsonInteger) {
        return putObj(key, Integer.valueOf(jsonInteger));
    }

    public JSONObject put(String key, long jsonLong) {
        return putObj(key, Long.valueOf(jsonLong));
    }

    public JSONObject put(String key, float jsonFloat) {
        return putObj(key, Float.valueOf(jsonFloat));
    }

    public JSONObject put(String key, double jsonDouble) {
        return putObj(key, Double.valueOf(jsonDouble));
    }

    public JSONObject put(String key, String jsonString) {
        return putObj(key, jsonString);
    }

    public Object getObject(String key)
            throws JSONException {
        try {
            if (this.jsonData.containsKey(key)) {
                return this.jsonData.get(key);
            }
            throw new JSONException("element of key \"" + key + "\" not exist");
        } catch (JSONException ex) {
            if (this.noException) {
                return null;
            }
            throw ex;
        }
    }

    public JSONObject getJsonObject(String key)
            throws JSONException {
        try {
            return JSONParser.parseJsonObject(getObject(key));
        } catch (JSONException ex) {
            if (this.noException) {
                return null;
            }
            throw ex;
        }
    }

    public JSONArray getJsonArray(String key)
            throws JSONException {
        try {
            return JSONParser.parseJsonArray(getObject(key));
        } catch (JSONException ex) {
            if (this.noException) {
                return null;
            }
            throw ex;
        }
    }

    public boolean getBoolean(String key)
            throws JSONException {
        try {
            return JSONParser.parseBoolean(getObject(key));
        } catch (JSONException ex) {
            if (this.noException) {
                return false;
            }
            throw ex;
        }
    }

    public char getChar(String key)
            throws JSONException {
        try {
            return JSONParser.parseChar(getObject(key));
        } catch (JSONException ex) {
            if (this.noException) {
                return '\000';
            }
            throw ex;
        }
    }

    public byte getByte(String key)
            throws JSONException {
        try {
            return JSONParser.parseByte(getObject(key));
        } catch (JSONException ex) {
            if (this.noException) {
                return 0;
            }
            throw ex;
        }
    }

    public short getShort(String key)
            throws JSONException {
        try {
            return JSONParser.parseShort(getObject(key));
        } catch (JSONException ex) {
            if (this.noException) {
                return 0;
            }
            throw ex;
        }
    }

    public int getInteger(String key)
            throws JSONException {
        try {
            return JSONParser.parseInteger(getObject(key));
        } catch (JSONException ex) {
            if (this.noException) {
                return 0;
            }
            throw ex;
        }
    }

    public long getLong(String key)
            throws JSONException {
        try {
            return JSONParser.parseLong(getObject(key));
        } catch (JSONException ex) {
            if (this.noException) {
                return 0L;
            }
            throw ex;
        }
    }

    public float getFloat(String key)
            throws JSONException {
        try {
            return JSONParser.parseFloat(getObject(key));
        } catch (JSONException ex) {
            if (this.noException) {
                return 0.0F;
            }
            throw ex;
        }
    }

    public double getDouble(String key)
            throws JSONException {
        try {
            return JSONParser.parseDouble(getObject(key));
        } catch (JSONException ex) {
            if (this.noException) {
                return 0.0D;
            }
            throw ex;
        }
    }

    public String getString(String key)
            throws JSONException {
        try {
            return JSONParser.parseString(getObject(key));
        } catch (JSONException ex) {
            if (this.noException) {
                return null;
            }
            throw ex;
        }
    }

    public String toJsonString()
            throws JSONException {
        return toJsonString(false, false);
    }

    public String toJsonString(boolean simple)
            throws JSONException {
        return toJsonString(simple, false);
    }

    public String toJsonString(boolean simple, boolean nonc)
            throws JSONException {
        JSONWriter writer = new JSONWriter(simple, nonc);
        writer.writeJSONObjectBegin();
        Set<String> keys = this.jsonData.keySet();
        int count = 0;
        for (String key : keys) {
            if (count != 0) {
                writer.writeJSONComma();
            }
            writer.writeKey(key);
            writer.writeJSONColon();
            Object value = this.jsonData.get(key);
            writer.writeObj(value);
            count++;
        }
        writer.writeJSONObjectEnd();
        return writer.getBuffString();
    }

    public String toString() {
        try {
            return toJsonString();
        } catch (JSONException ex) {
        }
        return null;
    }

    public String toString(boolean simple) {
        try {
            return toJsonString(simple);
        } catch (JSONException ex) {
        }
        return null;
    }

    public String toString(boolean simple, boolean nonc) {
        try {
            return toJsonString(simple, nonc);
        } catch (JSONException ex) {
        }
        return null;
    }
}
