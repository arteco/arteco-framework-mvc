package com.arteco.mvc.model;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.arteco.mvc.utils.SeoUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Created by rarnau on 11/11/16.
 * Arteco Consulting Sl.
 * mailto: info@arteco-consulting.com
 */
public class PathExpression {

    public static final String ATTR_KEY = PathExpression.class.getName() + ".varsMap";

    private enum PathType {
        typeDef(null, "([^\\/]+)"),
        typeInt("int", "([\\d]+)");

        private final String term;
        private String regexp;

        PathType(String term, String regexp) {
            this.term = term;
            this.regexp = regexp;
        }

        public String getTerm() {
            return term;
        }

        public String getRegexp() {
            return regexp;
        }

        public static PathType valueOfTerm(String term) {
            for (PathType p : PathType.values()) {
                if (StringUtils.equalsIgnoreCase(term, p.getTerm())) {
                    return p;
                }
            }
            return null;
        }
    }

    Pattern pattern;
    String[] pathVariables;
    String path;

    public PathExpression(String path) {
        this.path = path;
        path = SeoUtils.removeEndSlash(path);
        path = StringUtils.replace(path, "/", "\\/");
        pathVariables = StringUtils.substringsBetween(path, "{", "}");
        if (pathVariables != null) {
            for (int i = 0; i < pathVariables.length; i++) {
                String pathVar = pathVariables[i];
                PathType type = null;
                if (pathVar.contains(":")) {
                    String[] tuple = pathVar.split(":");
                    pathVariables[i] = tuple[0];
                    type = PathType.valueOfTerm(tuple[1]);

                }
                String expvar = "{" + pathVar + "}";
                String term = PathType.typeDef.getRegexp();
                if (type != null) {
                    term = type.getRegexp();
                }
                path = StringUtils.replace(path, expvar, term);
            }
        }
        pattern = Pattern.compile("^" + path + "$");
    }

    public boolean match(HttpServletRequest req) {
        String uri = SeoUtils.getUriWithouContextPath(req);
        Matcher m = pattern.matcher(uri);
        if (m.find()) {
            if (pathVariables != null && pathVariables.length > 0) {
                Map<String, String> map = new HashMap<String, String>();
                int i = 1;
                for (String var : pathVariables) {
                    map.put(var, m.group(i++));
                }
                req.setAttribute(ATTR_KEY, map);
            }
            return true;
        }
        return false;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "PathExpression{" +
                "path='" + path + '\'' +
                '}';
    }
}
