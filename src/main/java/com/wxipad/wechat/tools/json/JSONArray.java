package com.wxipad.wechat.tools.json;

import java.util.ArrayList;

public class JSONArray {
    private final ArrayList<Object> jsonData;
    public boolean noException;

    public JSONArray() {
        this.jsonData = new ArrayList();
        this.noException = false;
    }

    public JSONArray(boolean noException) {
        this.jsonData = new ArrayList();
        this.noException = noException;
    }

    public JSONArray(ArrayList<Object> data) {
        this.jsonData = data;
        this.noException = false;
    }

    public JSONArray(ArrayList<Object> data, boolean noException) {
        this.jsonData = data;
        this.noException = noException;
    }

    public JSONArray(String jsonStr)
            throws JSONException {
        JSONSolver tokener = new JSONSolver(jsonStr);
        this.jsonData = tokener.solveJSONArray(false);
        this.noException = false;
    }

    public JSONArray(String jsonStr, boolean noException)
            throws JSONException {
        ArrayList<Object> data = null;
        try {
            JSONSolver tokener = new JSONSolver(jsonStr);
            data = tokener.solveJSONArray(noException);
        } catch (JSONException ex) {
            if (noException) {
                data = new ArrayList();
            } else {
                throw ex;
            }
        } finally {
            this.jsonData = data;
            this.noException = noException;
        }
    }

    public int length() {
        return this.jsonData.size();
    }

    public Object remove(int index) {
        return this.jsonData.remove(index);
    }

    private JSONArray putObj(Object obj) {
        this.jsonData.add(obj);
        return this;
    }

    public JSONArray put(Object jsonObj) {
        return putObj(jsonObj);
    }

    public JSONArray put(JSONObject jsonObject) {
        return putObj(jsonObject);
    }

    public JSONArray put(JSONArray jsonArray) {
        return putObj(jsonArray);
    }

    public JSONArray put(boolean jsonBoolean) {
        return putObj(Boolean.valueOf(jsonBoolean));
    }

    public JSONArray put(char jsonChar) {
        return putObj(Character.valueOf(jsonChar));
    }

    public JSONArray put(byte jsonByte) {
        return putObj(Byte.valueOf(jsonByte));
    }

    public JSONArray put(short jsonShort) {
        return putObj(Short.valueOf(jsonShort));
    }

    public JSONArray put(int jsonInteger) {
        return putObj(Integer.valueOf(jsonInteger));
    }

    public JSONArray put(long jsonLong) {
        return putObj(Long.valueOf(jsonLong));
    }

    public JSONArray put(float jsonFloat) {
        return putObj(Float.valueOf(jsonFloat));
    }

    public JSONArray put(double jsonDouble) {
        return putObj(Double.valueOf(jsonDouble));
    }

    public JSONArray put(String jsonString) {
        return putObj(jsonString);
    }

    public Object getObject(int index)
            throws JSONException {
        try {
            if ((index >= 0) && (index < this.jsonData.size())) {
                return this.jsonData.get(index);
            }
            throw new JSONException("index " + index + " out of range");
        } catch (JSONException ex) {
            if (this.noException) {
                return null;
            }
            throw ex;
        }
    }

    public JSONObject getJsonObject(int index)
            throws JSONException {
        try {
            return JSONParser.parseJsonObject(getObject(index));
        } catch (JSONException ex) {
            if (this.noException) {
                return null;
            }
            throw ex;
        }
    }

    public JSONArray getJsonArray(int index)
            throws JSONException {
        try {
            return JSONParser.parseJsonArray(getObject(index));
        } catch (JSONException ex) {
            if (this.noException) {
                return null;
            }
            throw ex;
        }
    }

    public boolean getBoolean(int index)
            throws JSONException {
        try {
            return JSONParser.parseBoolean(getObject(index));
        } catch (JSONException ex) {
            if (this.noException) {
                return false;
            }
            throw ex;
        }
    }

    public char getChar(int index)
            throws JSONException {
        try {
            return JSONParser.parseChar(getObject(index));
        } catch (JSONException ex) {
            if (this.noException) {
                return '\000';
            }
            throw ex;
        }
    }

    public byte getByte(int index)
            throws JSONException {
        try {
            return JSONParser.parseByte(getObject(index));
        } catch (JSONException ex) {
            if (this.noException) {
                return 0;
            }
            throw ex;
        }
    }

    public short getShort(int index)
            throws JSONException {
        try {
            return JSONParser.parseShort(getObject(index));
        } catch (JSONException ex) {
            if (this.noException) {
                return 0;
            }
            throw ex;
        }
    }

    public int getInteger(int index)
            throws JSONException {
        try {
            return JSONParser.parseInteger(getObject(index));
        } catch (JSONException ex) {
            if (this.noException) {
                return 0;
            }
            throw ex;
        }
    }

    public long getLong(int index)
            throws JSONException {
        try {
            return JSONParser.parseLong(getObject(index));
        } catch (JSONException ex) {
            if (this.noException) {
                return 0L;
            }
            throw ex;
        }
    }

    public float getFloat(int index)
            throws JSONException {
        try {
            return JSONParser.parseFloat(getObject(index));
        } catch (JSONException ex) {
            if (this.noException) {
                return 0.0F;
            }
            throw ex;
        }
    }

    public double getDouble(int index)
            throws JSONException {
        try {
            return JSONParser.parseDouble(getObject(index));
        } catch (JSONException ex) {
            if (this.noException) {
                return 0.0D;
            }
            throw ex;
        }
    }

    public String getString(int index)
            throws JSONException {
        try {
            return JSONParser.parseString(getObject(index));
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
        writer.writeJSONArrayBegin();
        for (int i = 0; i < this.jsonData.size(); i++) {
            if (i != 0) {
                writer.writeJSONComma();
            }
            Object value = this.jsonData.get(i);
            writer.writeObj(value);
        }
        writer.writeJSONArrayEnd();
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
