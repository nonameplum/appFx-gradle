package appFx.datasource.helpers;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DatasourceMap implements ObservableMap<String, SimpleObjectProperty<Object>> {

    private ObservableMap<String, SimpleObjectProperty<Object>> map;

    public DatasourceMap() {
        ObservableMap<String, SimpleObjectProperty<Object>> obj = FXCollections.observableMap(new LinkedHashMap<String, SimpleObjectProperty<Object>>());
        this.map = obj;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public SimpleObjectProperty<Object> get(Object key) {
        return this.map.get(key);
    }

    @Override
    public SimpleObjectProperty<Object> put(String key, SimpleObjectProperty<Object> value) {
        return this.map.put(key, value);
    }

    @Override
    public SimpleObjectProperty<Object> remove(Object key) {
        return this.map.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends SimpleObjectProperty<Object>> m) {
        this.map.putAll(m);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    public Set<String> keySet() {
        return this.map.keySet();
    }

    @Override
    public Collection<SimpleObjectProperty<Object>> values() {
        return this.map.values();
    }

    @Override
    public Set<Entry<String, SimpleObjectProperty<Object>>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public void addListener(MapChangeListener<? super String, ? super SimpleObjectProperty<Object>> mapChangeListener) {
        this.map.addListener(mapChangeListener);
    }

    @Override
    public void removeListener(MapChangeListener<? super String, ? super SimpleObjectProperty<Object>> mapChangeListener) {
        this.map.remove(mapChangeListener);
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        this.map.addListener(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        this.map.addListener(invalidationListener);
    }
}
