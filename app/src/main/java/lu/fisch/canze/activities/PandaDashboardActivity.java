package lu.fisch.canze.activities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lu.fisch.canze.actors.Field;
import lu.fisch.canze.classes.Sid;

public class PandaDashboardActivity extends CanzeActivity {
    private final Map<String, String> fieldMapping = Collections.unmodifiableMap(new HashMap<String, String>() {
        {
            put(Sid.DcPowerOut, "");
        }
    });

    @Override
    protected void initListeners() {
        addField(Sid.DcPowerOut);
    }

    @Override
    public void onFieldUpdateEvent(final Field field) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String fieldId = field.getSID();
                switch (fieldId) {
                    case Sid.DcPowerOut:
                        break;
                }
            }
        });
    }
}
