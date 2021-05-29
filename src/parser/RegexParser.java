package parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexParser implements  Parser
{

    @Override
    public List<String> parse(String s) {
        // Note: can see following page for special characters in Java Regex:
        //       https://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html
        List<String> ret = new ArrayList<>();
        Pattern p = Pattern.compile("\\w+");
        Matcher m = p.matcher(s);
        while (m.find()) {
            ret.add(m.group());
            // Equivalently we could also say: ret.add(s.substring(m.start(), m.end()));
        }
        return ret;
    }
}
