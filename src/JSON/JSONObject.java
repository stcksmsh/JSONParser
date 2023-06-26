package JSON;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
            case 'O': /// object
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

    public void add(JSONObject value) throws JSONException {
        if (type != 'A' && type != 'N') { /// it is not an array or NULL
            throw new JSONException("Error: JSONObject is not of type array");
        }
        if (type == 'N') {
            type = 'A';
            values = new ArrayList<JSONObject>();
        }
        values.add(value);
    }

    public void add(int i) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.setValue(i);
        add(obj);
    }

    public void add(double d) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.setValue(d);
        add(obj);
    }

    public void add(String s) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.setValue(s);
        add(obj);
    }

    public void add(boolean b) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.setValue(b);
        add(obj);
    }

    public void add(String key, JSONObject value) {

    }

    public static void main(String[] args) {
        JSONObject obj = new JSONObject();
        obj.type = 'J';

        obj.keys.add("Test");
        JSONObject value = new JSONObject();
        value.type = 'S';
        value.value = "TESTING";
        obj.values.add(value);

        obj.keys.add("Again");
        value = new JSONObject();
        value.type = 'D';
        value.value = Double.valueOf(10.123);
        obj.values.add(value);
        System.out.println(obj.toString());
    }
}
