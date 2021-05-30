package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexParser2 implements  Parser
{

    @Override
    public List<String> parse(String s) {
        ArrayList<String> ret = new ArrayList<String>();
        Pattern p = Pattern.compile("(\\w-*)+"); //regex conditions given in project doc
        Matcher m = p.matcher(s);
        while(m.find()){
            ret.add(m.group());
        }
        return ret;
    }
}
