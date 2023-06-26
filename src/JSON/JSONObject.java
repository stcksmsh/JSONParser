package JSON;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.print.DocFlavor.STRING;

import JSON.JSONException;

public class JSONObject {

    private List<String> keys;
    private List<JSONObject> values;
    private Object value;
    private char type;

    public JSONObject() {
        super();
        keys = null;
        values = null;
        type = 'N'; /// NULL object
        value = null;
    }

    public JSONObject(int i) {
        this();
        setValue(i);
    }

    public JSONObject(double d) {
        this();
        setValue(d);
    }

    public JSONObject(String s) {
        this();
        setValue(s);
    }

    public JSONObject(boolean b) {
        this();
        setValue(b);
    }

    public void setValue(int i) {
        value = Integer.valueOf(i);
        type = 'I';
    }

    public void setValue(double d) {
        value = Double.valueOf(d);
        type = 'D';
    }

    public void setValue(String s) {
        value = new String(s);
        type = 'S';
    }

    public void setValue(boolean b) {
        value = Boolean.valueOf(b);
        type = 'B';
    }

    public static JSONObject parseString(String string) {
        JSONObject obj = new JSONObject();

        return obj;
    }

    public Iterator<String> keys() {
        return keys.iterator();
    }

    public Iterator<JSONObject> values() {
        return values.iterator();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("");
        switch (type) {
            case 'I': /// integer
                sb.append((Integer) value);
                break;
            case 'D': /// double
                sb.append((Double) value);
                break;
            case 'S': /// string
                sb.append('"');
                sb.append((String) value);
                sb.append('"');
                break;
            case 'B': /// boolean
                sb.append((Boolean) value);
                break;
            case 'N': /// NULL
                sb.append("NULL");
                break;
            case 'A': /// array
                sb.append('[');
                for (JSONObject obj : values) {
                    sb.append(obj.toString());
                    sb.append(',');
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);
                sb.append(']');
                break;
            case 'M': /// map
                sb.append('{');
                for (int i = 0; i < keys.size(); i++) {
                    sb.append(keys.get(i));
                    sb.append(':');
                    sb.append(values.get(i).toString());
                    sb.append(',');
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);
                sb.append('}');
                break;
            default:
                System.err.println("Invalid type: '" + type + "'...");
                break;
        }
        return sb.toString();
    }

    public void add(JSONObject value) {
        if (type != 'A' && type != 'N') /// it is not an array or NULL
            throw new JSONException("Error: JSONObject is not of type array");

        if (type == 'N') {
            type = 'A';
            values = new ArrayList<JSONObject>();
        }
        values.add(value);
    }

    public void add(int i) {
        JSONObject obj = new JSONObject();
        obj.setValue(i);
        add(obj);
    }

    public void add(double d) {
        JSONObject obj = new JSONObject();
        obj.setValue(d);
        add(obj);
    }

    public void add(String s) {
        JSONObject obj = new JSONObject();
        obj.setValue(s);
        add(obj);
    }

    public void add(boolean b) {
        JSONObject obj = new JSONObject();
        obj.setValue(b);
        add(obj);
    }

    public void add(String key, JSONObject value) {
        if (type != 'M' && type != 'N')
            throw new JSONException("Error: JSONObject is not of type map...");

        if (type == 'N') {
            keys = new ArrayList<String>();
            values = new ArrayList<JSONObject>();
            type = 'M';
        }
        keys.add(key);
        values.add(value);
    }

    public JSONObject get(String key) {
        if (type != 'M')
            throw new JSONException("Error: JSONObject is not of type map...");
        int index = keys.indexOf(key);
        if (index == -1)
            return null;
        return values.get(index);
    }

    public JSONObject get(int index) {
        if (type != 'A')
            throw new JSONException("Error: JSONObject is not of type map...");
        return values.get(index);
    }

    public static void main(String[] args) {
        JSONObject obj = new JSONObject();

        obj.add(new JSONObject("test"));
        obj.add(new JSONObject(false));

        System.out.println(obj.toString());
        System.out.println(obj.get(0));
        System.out.println(obj.get(1));
        System.out.println(obj.get("test"));
    }
}
