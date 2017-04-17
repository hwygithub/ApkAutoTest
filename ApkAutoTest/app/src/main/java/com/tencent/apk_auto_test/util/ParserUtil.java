package com.tencent.apk_auto_test.util;

import android.util.Xml;

import com.tencent.apk_auto_test.data.TestCase;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ParserUtil {

    public List<TestCase> parse(InputStream is) throws Exception {
        List<TestCase> caseList = null;
        TestCase testCase = null;

        XmlPullParser parser = Xml.newPullParser(); // android.util.Xml create
        // XmlPullParser
        parser.setInput(is, "UTF-8"); // set mode

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    caseList = new ArrayList<TestCase>();
                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("case")) {
                        testCase = new TestCase();
                    } else if (parser.getName().equals("name")) {
                        eventType = parser.next();
                        testCase.setName(parser.getText());
                    } else if (parser.getName().equals("className")) {
                        eventType = parser.next();
                        testCase.setClassName(parser.getText());
                    } else if (parser.getName().equals("timeGene")) {
                        eventType = parser.next();
                        testCase.setTimeGene(parser.getText());
                    } else if (parser.getName().equals("runState")) {
                        eventType = parser.next();
                        testCase.setRunState(parser.getText());
                    } else if (parser.getName().equals("caseName")) {
                        eventType = parser.next();
                        testCase.setCaseName(parser.getText());
                    } else if (parser.getName().equals("caseOrder")) {
                        eventType = parser.next();
                        testCase.setCaseOrder(parser.getText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("case")) {
                        caseList.add(testCase);
                        testCase = null;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return caseList;
    }
}
