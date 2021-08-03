package com.company.quarium.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service(TestCaseTimerService.NAME)
public class TestCaseTimerServiceBean implements TestCaseTimerService {

    public Map<String, String> getTimeUnitsValues(int seconds) {
        String h = String.valueOf(seconds / 3600);
        String m = seconds > 3600 ?
                String.valueOf((seconds - (seconds / 3600 * 3600)) / 60)
                : String.valueOf(seconds / 60);
        String s = String.valueOf(seconds % 60);

        String hLabel = h.length() > 1 ? h : "0" + h;
        String mLabel = m.length() > 1 ? m : "0" + m;
        String sLabel = s.length() > 1 ? s : "0" + s;

        Map<String, String> units = new HashMap<>();
        units.put("hours", hLabel);
        units.put("minutes", mLabel);
        units.put("seconds", sLabel);

        return units;
    }
}