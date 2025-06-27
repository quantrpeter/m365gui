package org.hkprog.m365gui.spoPanel;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.table.AbstractTableModel;
import java.util.*;

public class TableModelList extends AbstractTableModel {
    private final List<String> columnNames = new ArrayList<>();
    private final List<Map<String, Object>> data = new ArrayList<>();

    public TableModelList(JSONArray jsonArray) {
        // Collect all unique keys from all objects
        Set<String> keys = new LinkedHashSet<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            Iterator<String> it = obj.keys();
            while (it.hasNext()) {
                keys.add(it.next());
            }
        }
        columnNames.addAll(keys);

        // Store each row as a map
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            Map<String, Object> row = new HashMap<>();
            for (String key : columnNames) {
                Object value = obj.has(key) ? obj.get(key) : null;
                row.put(key, value);
            }
            data.add(row);
        }
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public String getColumnName(int column) {
        return columnNames.get(column);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String key = columnNames.get(columnIndex);
        Object value = data.get(rowIndex).get(key);
        // For nested JSONObjects/JSONArrays, return their string representation
        if (value instanceof JSONObject || value instanceof JSONArray) {
            return value.toString();
        }
        return value;
    }
}
