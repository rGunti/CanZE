package lu.fisch.canze.activities;

import static lu.fisch.canze.devices.Device.INTERVAL_ASAP;

import android.os.Build;
import android.os.Bundle;
import android.transition.Visibility;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.RunnableFuture;
import java.util.function.Function;

import lu.fisch.canze.R;
import lu.fisch.canze.actors.Field;
import lu.fisch.canze.classes.Sid;
import lu.fisch.canze.interfaces.DebugListener;

public class PandaDashboardActivity extends CanzeActivity implements DebugListener {
    private static class PandaDashboardRecord {
        private final int textRef;
        private final String format;
        private final String defaultValue;
        private final int interval;
        private final String fallbackFormat;
        private final Function<Double, Double> valueTransformer;

        PandaDashboardRecord(@IdRes int textRef, String format, String defaultValue) {
            this(textRef, format, defaultValue, INTERVAL_ASAP);
        }
        PandaDashboardRecord(@IdRes int textRef, String format, String defaultValue, int interval) {
            this(textRef, format, defaultValue, interval, d -> d, null);
        }
        PandaDashboardRecord(@IdRes int textRef, String format, String defaultValue, int interval, Function<Double, Double> valueTransformer, String fallbackFormat) {
            this.textRef = textRef;
            this.format = format;
            this.defaultValue = defaultValue;
            this.interval = interval;
            this.valueTransformer = valueTransformer != null ? valueTransformer : (d) -> d;
            this.fallbackFormat = fallbackFormat != null ? fallbackFormat : format;
        }

        public int getTextRef() {
            return textRef;
        }
        public int getInterval() {
            return interval;
        }

        String formatValue(Double value) {
            if (Double.isNaN(value)) {
                return defaultValue;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return String.format(Locale.getDefault(), format, transformValue(value));
            } else {
                return String.format(Locale.getDefault(), fallbackFormat, value);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private Double transformValue(double value) {
            return valueTransformer.apply(value);
        }
    }

    private final Map<String, PandaDashboardRecord> fieldMapping = Collections.unmodifiableMap(new HashMap<String, PandaDashboardRecord>() {
        {
            put(Sid.DcPowerOut, new PandaDashboardRecord(R.id.textDcPwr, "%.1f", "--.-"));
            put(Sid.AvailableChargingPower, new PandaDashboardRecord(R.id.textMaxChargeAvailable, "%.1f kW", "0.0 kW", 10000));

            put(Sid.UserSoC, new PandaDashboardRecord(R.id.textPandaUserSoC, "%.1f", "---.-", 10000));
            put(Sid.AvailableEnergy, new PandaDashboardRecord(R.id.textEnergyAvailable, "%.1f kWh", "--.- kWh", 10000));

            put(Sid.HvTemp, new PandaDashboardRecord(R.id.textBatteryTemperature, "%.0f", "--", 5000));

            put(Sid.ThermalComfortPower, new PandaDashboardRecord(R.id.textHvacConsumption, "%.2f", "-.--", 10000, d -> d / 1000, "%.0f"));
            put(Sid.TemperatureExterior, new PandaDashboardRecord(R.id.textTempOut, "%.1f°C", "--.-°C", 15000));
            put(Sid.TemperatureInterior, new PandaDashboardRecord(R.id.textTempIn, "%.1f°C", "--.-°C", 15000));
            //put(Sid.MaxCharge, new PandaDashboardRecord(R.id.textMaxCharge, "%.1f", "--.-", 5000));
        }
    });

    @Override
    protected void initListeners() {
        MainActivity.getInstance().setDebugListener(this);
        for (String field : fieldMapping.keySet()) {
            PandaDashboardRecord data = fieldMapping.get(field);
            addField(field, data.getInterval());
        }
    }

    @Override
    public void onFieldUpdateEvent(final Field field) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String fieldId = field.getSID();
                if (fieldMapping.containsKey(fieldId)) {
                    PandaDashboardRecord record = fieldMapping.get(fieldId);
                    TextView view = findViewById(record.getTextRef());
                    view.setText(record.formatValue(field.getValue()));
                } else {
                    //noinspection SwitchStatementWithTooFewBranches
                    switch (fieldId) {
                        case Sid.EcoMode:
                            setEcoMode(field.getValue() == (double) 2);
                            break;
                    }
                }
            }
        });
    }

    private void setEcoMode(boolean isEcoModeEnabled) {
        findViewById(R.id.textEcoMode).setVisibility(isEcoModeEnabled ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panda_dashboard);
    }
}
