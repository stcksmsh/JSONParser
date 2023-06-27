package JSON;

import java.security.CryptoPrimitive;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class JSONObject {

    private List<String> keys;
    private List<JSONObject> values;
    private Object value;
    private char type;

    public JSONObject() {
        super();
        keys = null;
        values = null;
        type = 'U'; /// Undefined object
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

    public void setNull(){
        value = null;
        type = 'N';
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

    public char getType() {
        return type;
    }

    public boolean isEmpty(){
        if(type == 'A' || type == 'M')
            return values.isEmpty();
        return true;
    }

    public static JSONObject parseString(String string) {
        JSONObject currentContainer = new JSONObject();
        Stack<JSONObject> containerStack = new Stack<JSONObject>();
        containerStack.push(currentContainer);
        char currentChar = ' ';
        char type;
        boolean expectingComma = false;
        int index = -1;
        while (!containerStack.empty()) {
            currentContainer = containerStack.pop();
            type = currentContainer.getType();
            /// skip all whitespace
            while ((currentChar == ' ' || currentChar == '\t' || currentChar == '\n') && index < string.length())
            currentChar = string.charAt(++index);
            if (type == 'U') {
                if (currentChar == '[') { /// array
                    type = 'A';
                    currentContainer.type = 'A';
                } else if (currentChar == '{') { /// map
                    type = 'M';
                    currentContainer.type = 'M';
                } else if (( currentChar >= '0' && currentChar <= '9' ) || currentChar == '-') { /// integer or double                    
                    int number = 0;
                    int decimals = -1;
                    if(currentChar == '-'){
                        if(index == string.length() - 1){
                            throw new JSONException(index, string);
                        }
                        number = -(string.charAt(++index) - '0');
                    }else{
                        number = currentChar - '0';
                    }
                    while(index < string.length() - 1){
                        currentChar = string.charAt(++index);
                        if(currentChar >= '0' && currentChar <='9'){
                            int dig = currentChar - '0';
                            if(number < 0)dig = -dig;
                            number = number*10 + dig;
                        }else
                            break;
                    }
                    if(currentChar == '.'){
                        decimals = 0;
                        while(index < string.length() - 1){
                            currentChar = string.charAt(++index);
                            if(currentChar >= '0' && currentChar <='9'){
                                decimals = decimals*10 + currentChar - '0';
                            }else{
                                break;
                            }
                        }
                    }
                    if(decimals == -1){
                        currentContainer.setValue(number);
                    }else{
                        double d = number;
                        if(d < 0){
                            decimals = - decimals;
                        }
                        d += decimals * ( Math.pow(10, -(int)(Math.log10(decimals) + 1)));
                        currentContainer.setValue(d);
                    }
                    index--;
                } else if (currentChar == '"') { /// string
                    type = 'S';
                    int stringEnd = string.indexOf('\"', index + 1);
                    if(stringEnd == -1){
                        throw new JSONException("Error: String block started but never finished at " + Integer.toString(index));
                    }
                    currentContainer.setValue(string.substring(index+1, stringEnd));
                    index = stringEnd;
                } else if (currentChar == 'f' || currentChar == 't') { /// boolean
                    type = 'B';
                    if (index + 4 > string.length()) {
                        throw new JSONException(index, string);
                    }
                    String value = string.substring(index, index + 4);
                    if(value.equals("true")){
                        currentContainer.setValue(true);
                        index += 4;
                    }else if(value.equals("fals")){
                        index += 4;
                        if(index >= string.length() || string.charAt(index) != 'e')
                            throw new JSONException(index, string);
                        currentContainer.setValue(false);
                        index++;                            
                    }else{
                        throw new JSONException(index, string);
                    }
                } else if (currentChar == 'n') { /// nulll
                    type = 'N';
                    if (index + 4 < string.length() || !string.substring(index, index + 4).equals("null")) {
                        throw new JSONException(index, string);
                    }
                    currentContainer.setNull();
                } else {
                    throw new JSONException(index, string);
                }
                if (++index < string.length()) {
                    currentChar = string.charAt(index);
                    /// skip all following whitespace
                    while ((currentChar == ' ' || currentChar == '\t' || currentChar == '\n')
                            && index < string.length())
                        currentChar = string.charAt(++index);
                } else {

                }
            }
            type = currentContainer.getType();
            if (type == 'M') {
                if(currentChar == '"'){ /// new entry
                    int nameEnd = string.indexOf('\"', index+1);
                    String name = string.substring(index+1, nameEnd);
                    JSONObject obj = new JSONObject();
                    currentContainer.add(name, obj);
                    containerStack.push(currentContainer);
                    containerStack.push(obj);
                    index = nameEnd + 1;
                    currentChar = string.charAt(index);
                    while ((currentChar == ' ' || currentChar == '\t' || currentChar == '\n') && index < string.length())
                        currentChar = string.charAt(++index);
                    if(currentChar != ':')
                        throw new JSONException("Error: expected ':' but found '" + currentChar + "' at " + Integer.toString(index) + " while parsing string:" + string);
                    currentChar = string.charAt(++index);
                    while ((currentChar == ' ' || currentChar == '\t' || currentChar == '\n') && index < string.length())
                        currentChar = string.charAt(++index);
                }else if(currentChar == ','){ 
                    currentChar = string.charAt(++index);
                    containerStack.push(currentContainer);                    
                }else if(currentChar == '}'){ /// end of map
                    if(!containerStack.empty()){
                        JSONObject parentContainer = containerStack.peek();
                        if(parentContainer.getType() == 'M' || parentContainer.getType() == 'A'){
                            parentContainer.add(currentContainer);
                        }else{
                            throw new JSONException("Error: invalid JSONObject type '" + parentContainer.getType() + "'...");
                        }
                    }
                    index++;
                    if(index < string.length())currentChar = string.charAt(index);
                }else{
                    containerStack.push(currentContainer);                    
                    containerStack.push(new JSONObject());
                }

            }else if (type == 'A') {
                if(currentChar == ','){
                    currentChar = string.charAt(++index);
                    containerStack.push(currentContainer);                    
                    containerStack.push(new JSONObject());
                }else if(currentChar == ']'){ /// end of array
                    if(!containerStack.empty()){
                        JSONObject parentContainer = containerStack.peek();
                        if(parentContainer.getType() == 'M' || parentContainer.getType() == 'A'){
                            parentContainer.add(currentContainer);
                        }else{
                            throw new JSONException("Error: invalid JSONObject type '" + parentContainer.getType() + "'...");
                        }
                    }
                    index++;
                    if(index < string.length())currentChar = string.charAt(index);
                }else{
                    containerStack.push(currentContainer);                    
                    containerStack.push(new JSONObject());
                }
            }else if(type == 'I' || type == 'D' || type == 'S' || type == 'B' || type == 'N'){
                if(!containerStack.empty()){
                    JSONObject parentContainer = containerStack.peek();
                    if(parentContainer.getType() == 'M' || parentContainer.getType() == 'A'){
                        parentContainer.add(currentContainer);
                    }else{
                        throw new JSONException("Error: invalid JSONObject type '" + parentContainer.getType() + "'...");
                    }
                }
            }
        }
        if(index != string.length()){
            throw new JSONException(index, string);
        }
        return currentContainer;
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
        if (type != 'A' && type != 'M' && type != 'U') /// it is not an array or NULL
            throw new JSONException("Error: JSONObject is not of type array");

        if (type == 'U')
            type = 'A';
        if(values == null)
            values = new ArrayList<JSONObject>();
        if(type == 'A')
            values.add(value);
        if(type == 'M')
            values.set(values.size()-1, value);
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
        if (type != 'M' && type != 'U')
            throw new JSONException("Error: JSONObject is not of type map...");

        if (type == 'U')
            type = 'M';
        if(keys == null)
            keys = new ArrayList<String>();
        if(values == null)
            values = new ArrayList<JSONObject>();
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
        String str = new String("[{\"a\":[1, 2], \"abc\":12}, \"test\"]");
        JSONObject obj = JSONObject.parseString(str);
        System.out.println(obj);
    }
}
