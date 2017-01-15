package com.lzchat.LZChat.utilities;


import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by patelkev on 11/13/16.
 */

public class AutoReplyClient {

    public Function jsFunction;
    public Context rhino;
    public Scriptable scope;

    private AutoReplyClient() {

        rhino = Context.enter();
        rhino.setOptimizationLevel(-1);

        scope = rhino.initStandardObjects();

        // Note the forth argument is 1, which means the JavaScript source has
        // been compressed to only one line using something like YUI
        String javaScriptCode = "function sayHello(a) {return \"Hello \" + a;}";
        try {
            javaScriptCode = contentsOfAssetFile(ChatApplication.getAppContext(), "eliza-min.js");
        } catch (IOException e) {
            e.printStackTrace();
        }
        rhino.evaluateString(scope, javaScriptCode, "JavaScript", 1, null);
        // Get the functionName defined in JavaScriptCode
        Object obj = scope.get("transformText", scope);

        if (obj instanceof Function) {
            jsFunction = (Function) obj;
        }
    }

    public String getResponseForText (String input) {
        Object[] params = new Object[]{input};
        // Call the function with params
        Object jsResult = jsFunction.call(rhino, scope, scope, params);
        // Parse the jsResult object to a String
        String result = Context.toString(jsResult);
        return result;
    }
    private static class LazyHolder {
        private static final AutoReplyClient INSTANCE = new AutoReplyClient();
    }

    public static AutoReplyClient getInstance() {
        return AutoReplyClient.LazyHolder.INSTANCE;
    }

    //File Reader Util
    public static String contentsOfFile(InputStream is) throws IOException {

        BufferedReader buf = new BufferedReader(new InputStreamReader(is));

        String line = buf.readLine();
        StringBuilder sb = new StringBuilder();

        while (line != null) {
            sb.append(line).append("\n");
            line = buf.readLine();
        }
        return sb.toString();
    }

    public static String contentsOfAssetFile(android.content.Context mContext, String filename) throws IOException {
        return contentsOfFile(mContext.getAssets().open(filename));
    }
}
