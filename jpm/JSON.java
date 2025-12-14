package jpm;
import java.util.Stack;

public class JSON {
    
    public static String list(Object... values) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < values.length; i++) {
            if (values[i] instanceof String &&
                    !(values[i].toString().startsWith("{\"") && values[i].toString().endsWith("}")) &&
                    !(values[i].toString().startsWith("[") && values[i].toString().endsWith("]"))) {
                sb.append("\"").append(values[i]).append("\"");
            } else {
                sb.append(values[i]);
            }
            sb.append(",");
        }
        if (values.length > 0)
            sb.setLength(sb.length() - 1); // remove last comma
        sb.append("]");
        return sb.toString();
    }
    public static String KV(Object... KV) {
        if (KV.length % 2 != 0)
            throw new IllegalArgumentException("JSON.KV needs even number of arguments (key-value pairs)");
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < KV.length; i += 2) {
            
            sb.append("\"").append(KV[i]).append("\":");
            if (KV[i + 1] instanceof String &&
                    !(KV[i + 1].toString().startsWith("{\"") && KV[i + 1].toString().endsWith("}")) &&
                    !(KV[i + 1].toString().startsWith("[") && KV[i + 1].toString().endsWith("]"))) {
                sb.append("\"").append(KV[i + 1]).append("\"");
            } else {
                sb.append(KV[i + 1]);
            }
            sb.append(",");
        }
        if (KV.length > 0)
            sb.setLength(sb.length() - 1); // remove last comma
        sb.append("}");
        return sb.toString();
    }
    public static void print(String s) {
        boolean isKey = true;
        boolean inquotes = false;
        Stack<Boolean> inArray = new java.util.Stack<>();
        boolean escape = false;
        inArray.push(false);
        int i = 0;
        int j = 0;
        int ind = 0;
        s = s.replaceAll("\n", "");
        s = s.replaceAll("\t", "");
        s = s.replaceAll("  ", "");
        for (byte b : s.getBytes()) {
            switch (b) {
                case '{':
                    if (!inquotes) {
                        inArray.push(false);
                        j++;
                        isKey = true;
                        i = 0;
                        inquotes = false;
                        System.out.println((char) b);
                        System.out.print("\u001B[33m"); // yellow
                        ind += 2;
                    } else {
                        System.out.print((char) b);
                    }
                    break;
                case '}':
                    if (!inquotes) {
                        inArray.pop();
                        j--;
                        isKey = true;
                        i = 0;
                        inquotes = false;
                        System.out.println();
                        ind -= 2;
                        pindent(ind);
                        System.out.print("\u001B[0m"); // reset
                        System.out.print((char) b);
                        System.out.print("\u001B[33m"); // yellow
                    } else {
                        System.out.print((char) b);
                    }
                    break;
                case '[':
                    inArray.set(j, true);
                    inquotes = false;
                    System.out.print((char) b);
                    break;
                case ']':
                    inArray.set(j, false);
                    inquotes = false;
                    System.out.print((char) b);
                    break;
                case ',':
                    if (!inquotes && !inArray.peek()) {
                        System.out.print("\u001B[0m"); // reset
                        System.out.print((char) b);
                        System.out.println();
                        isKey = true;
                        System.out.print("\u001B[33m"); // yellow
                        i = 0;
                        break;
                    }
                    System.out.print((char) b);
                    break;
                case '\\':
                    System.out.print((char) b);
                    escape = true;
                    break;
                case '"':
                    if (escape) {
                        System.out.print((char) b);
                        escape = false;
                        break;
                    }
                    if (isKey && !inquotes) {
                        pindent(ind);
                    }

                    inquotes = true;
                    i++;
                    if (i == 2) {
                        inquotes = false;
                        isKey = false;
                        System.out.print((char) b);
                        System.out.print("\u001B[0m"); // reset
                        break;
                    }
                    if (i == 4  && !inArray.peek()) {
                        inquotes = false;
                        isKey = true;
                        System.out.print((char) b);
                        System.out.print("\u001B[33m"); // yellow
                        i = 0;
                        break;
                    }
                    if (i > 4  && inArray.peek()) {
                        inquotes = true;
                        isKey = false;
                        System.out.print((char) b);
                        System.out.print("\u001B[0m"); // reset
                        i = 1;
                        break;
                    }
                    System.out.print((char) b);
                    break;
                default:
                    System.out.print((char) b);
                    break;
            }
        }
        System.out.print("\u001B[0m"); // reset
        System.out.println();
    }
    private static void pindent(int indent) {
        for (int i = 0; i < indent; i++)
            System.out.print(" ");
    }
}
